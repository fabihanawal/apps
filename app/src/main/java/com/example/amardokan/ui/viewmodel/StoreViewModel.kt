package com.example.amardokan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.amardokan.data.local.InitialData
import com.example.amardokan.data.repository.AmarDokanRepository
import com.example.amardokan.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    STORE,
    PRODUCT_DETAIL,
    CART,
    CHECKOUT,
    ORDER_TRACKER,
    VENDOR_DASHBOARD,
    SHOP_REGISTRATION,
    ADMIN
}

class StoreViewModel(private val repository: AmarDokanRepository) : ViewModel() {

    // Active Screen & Selection
    private val _currentScreen = MutableStateFlow(AppScreen.STORE)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Filters
    val selectedUnion = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("সব ক্যাটাগরি")
    val searchQuery = MutableStateFlow("")

    // Database flows
    val allShops: StateFlow<List<Shop>> = repository.allShops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.SHOPS)

    val activeShops: StateFlow<List<Shop>> = repository.activeShops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.SHOPS.filter { it.status == "Active" })

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.PRODUCTS)

    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.ORDERS)

    val allReviews: StateFlow<List<Review>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.REVIEWS)

    val allMarketingPosts: StateFlow<List<MarketingPost>> = repository.allMarketingPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.MARKETING_POSTS)

    val platformSettings: StateFlow<PlatformSettings> = repository.platformSettings
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.SETTINGS)

    // Filtered Products for Consumer Storefront
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        activeShops,
        selectedUnion,
        selectedCategory,
        searchQuery
    ) { products, shops, union, category, query ->
        val activeShopIds = shops.associateBy { it.shopId }

        products.filter { product ->
            val shop = activeShopIds[product.shopId] ?: return@filter false

            if (union.isNotBlank() && shop.union != union) return@filter false
            if (category != "সব ক্যাটাগরি" && product.category != category) return@filter false

            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                val match = product.productName.lowercase().contains(q) ||
                        shop.shopName.lowercase().contains(q) ||
                        product.category.lowercase().contains(q) ||
                        shop.union.lowercase().contains(q)
                if (!match) return@filter false
            }
            true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.PRODUCTS)

    // Filtered Shops for Consumer Storefront
    val filteredShops: StateFlow<List<Shop>> = combine(
        activeShops,
        selectedUnion
    ) { shops, union ->
        if (union.isBlank()) shops else shops.filter { it.union == union }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialData.SHOPS.filter { it.status == "Active" })

    // Cart
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    val cartTotalCount: StateFlow<Int> = _cart.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = _cart.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Vendor State
    val activeShopId = MutableStateFlow(InitialData.SHOPS[0].shopId)
    val loggedInVendorEmail = MutableStateFlow<String?>(null)

    // Admin State
    val isAdminLoggedIn = MutableStateFlow(false)

    // Navigation methods
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun viewProductDetail(product: Product) {
        _selectedProduct.value = product
        _currentScreen.value = AppScreen.PRODUCT_DETAIL
    }

    // Cart operations
    fun addToCart(product: Product, quantity: Int = 1) {
        val effectivePrice = product.discountPrice ?: product.price
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.productId == product.productId }
        if (index != -1) {
            val existing = current[index]
            val newQty = (existing.quantity + quantity).coerceAtMost(product.stock)
            current[index] = existing.copy(quantity = newQty)
        } else {
            current.add(
                CartItem(
                    productId = product.productId,
                    shopId = product.shopId,
                    productName = product.productName,
                    price = effectivePrice,
                    quantity = quantity.coerceAtMost(product.stock),
                    imageUrl = product.imageUrl,
                    maxStock = product.stock
                )
            )
        }
        _cart.value = current
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        _cart.value = _cart.value.map { item ->
            if (item.productId == productId) item.copy(quantity = quantity.coerceAtMost(item.maxStock)) else item
        }
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filter { it.productId != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Order Placement
    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        union: String,
        paymentMethod: String,
        notes: String?,
        onSuccess: (List<Order>) -> Unit
    ) {
        viewModelScope.launch {
            val orders = repository.placeOrder(
                cartItems = _cart.value,
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                union = union,
                paymentMethod = paymentMethod,
                notes = notes
            )
            clearCart()
            onSuccess(orders)
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
        }
    }

    // Product Management
    fun addProduct(
        shopId: String,
        productName: String,
        price: Double,
        discountPrice: Double?,
        stock: Int,
        unit: String,
        category: String,
        description: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            repository.addProduct(
                shopId = shopId,
                productName = productName,
                price = price,
                discountPrice = discountPrice,
                stock = stock,
                unit = unit,
                category = category,
                description = description,
                imageUrl = imageUrl
            )
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            removeFromCart(productId)
        }
    }

    // Shop Registration
    fun registerShop(
        shopName: String,
        ownerName: String,
        phone: String,
        email: String,
        address: String,
        union: String,
        category: String,
        description: String,
        logoUrl: String,
        onComplete: (Shop) -> Unit
    ) {
        viewModelScope.launch {
            val shop = repository.registerShop(
                shopName = shopName,
                ownerName = ownerName,
                phone = phone,
                email = email,
                address = address,
                union = union,
                category = category,
                description = description,
                logoUrl = logoUrl
            )
            onComplete(shop)
        }
    }

    fun approveShop(shopId: String) {
        viewModelScope.launch {
            repository.approveShop(shopId)
        }
    }

    fun rejectShop(shopId: String) {
        viewModelScope.launch {
            repository.rejectShop(shopId)
        }
    }

    fun deleteShop(shopId: String) {
        viewModelScope.launch {
            repository.deleteShop(shopId)
        }
    }

    // Review
    fun addReview(
        shopId: String,
        productId: String?,
        customerName: String,
        rating: Int,
        comment: String
    ) {
        viewModelScope.launch {
            repository.addReview(
                shopId = shopId,
                productId = productId,
                customerName = customerName,
                rating = rating,
                comment = comment
            )
        }
    }

    // Marketing Post
    fun generateMarketingPost(product: Product, shop: Shop, tone: String): String {
        val discountText = if (product.discountPrice != null && product.discountPrice < product.price) {
            "🏷️ বিশেষ অফার মূল্য: মাত্র ৳${product.discountPrice.toInt()} (পূর্বমূল্য ৳${product.price.toInt()})"
        } else {
            "🏷️ সেরা মূল্য: মাত্র ৳${product.price.toInt()}"
        }

        return when (tone) {
            "আকর্ষণীয় ও ধামাকা অফার" -> """
                🔥 বদলগাছীবাসীর জন্য বিশেষ অফার! ✨
                
                ${shop.shopName} নিয়ে এলো "${product.productName}"।
                
                $discountText
                📦 বদলগাছীর ${shop.union} সহ সব ইউনিয়নে দ্রুততম হোম ডেলিভারি!
                📍 ${shop.address.ifBlank { "${shop.union}, বদলগাছী, নওগাঁ" }}
                
                👉 স্টক সীমিত! আজই অর্ডার কনফার্ম করুন।
                📞 WhatsApp/কল: ${shop.phone}
                🌐 "আমার দোকান" অনলাইন মার্কেটপ্লেস
                
                #বদলগাছী #${shop.union.replace(" ", "")} #আমারদোকান #RSTS_BD
            """.trimIndent()

            "গুণগত মান ও ঐতিহ্য" -> """
                🌿 খাঁটি ও বিশ্বস্ত পণ্যের নিশ্চয়তা! 🌾
                
                ${shop.shopName} থেকে সংগ্রহ করুন আসল "${product.productName}"।
                ${product.description}
                
                $discountText
                🛵 ঘরের কাছে দ্রুত হোম ডেলিভারি।
                📞 যোগাযোগ: ${shop.phone}
                
                #নওগাঁ #বদলগাছী #ঐতিহ্য #আমারদোকান
            """.trimIndent()

            else -> """
                📢 নতুন কালেকশন পৌঁছে গেছে! 🛍️
                
                ${shop.shopName} এ এখন পাওয়া যাচ্ছে "${product.productName}"।
                $discountText
                
                📞 সরাসরি অর্ডার করুন: ${shop.phone}
                🚚 বদলগাছী উপজেলার ৮টি ইউনিয়নে দ্রুত ডেলিভারি!
            """.trimIndent()
        }
    }

    fun saveMarketingPost(
        shopId: String,
        shopName: String,
        productName: String,
        generatedText: String,
        imageUrl: String?
    ) {
        viewModelScope.launch {
            repository.saveMarketingPost(
                shopId = shopId,
                shopName = shopName,
                productName = productName,
                generatedText = generatedText,
                imageUrl = imageUrl
            )
        }
    }

    // Authentication helpers
    fun loginVendor(email: String): Pair<Boolean, String> {
        val normalized = email.trim().lowercase()
        val shop = allShops.value.find { it.email.trim().lowercase() == normalized }
            ?: return Pair(false, "এই ইমেইল দিয়ে কোনো দোকানের আবেদন পাওয়া যায়নি।")

        return when (shop.status) {
            "Pending" -> Pair(false, "দোকান \"${shop.shopName}\" এর আবেদনটি অ্যাডমিনের বিবেচনাধীন রয়েছে। অনুমোদন সম্পন্ন হলে লগইন করতে পারবেন।")
            "Rejected" -> Pair(false, "দোকান \"${shop.shopName}\" এর আবেদনটি স্থগিত করা হয়েছে। হেল্পলাইনে যোগাযোগ করুন।")
            else -> {
                loggedInVendorEmail.value = shop.email
                activeShopId.value = shop.shopId
                Pair(true, "স্বাগতম! \"${shop.shopName}\" এর ড্যাশবোর্ডে প্রবেশ সফল হয়েছে।")
            }
        }
    }

    fun logoutVendor() {
        loggedInVendorEmail.value = null
    }

    fun loginAdmin(email: String, pass: String): Pair<Boolean, String> {
        return if (email.trim().lowercase() == "rstsbd@gmail.com" && pass == "rstsbd1234") {
            isAdminLoggedIn.value = true
            Pair(true, "এডমিন প্যানেলে স্বাগতম!")
        } else {
            Pair(false, "ভুল ইমেইল বা পাসওয়ার্ড! অনুগ্রহ করে সঠিক তথ্য দিন।")
        }
    }

    fun logoutAdmin() {
        isAdminLoggedIn.value = false
    }

    fun updateSettings(settings: PlatformSettings) {
        viewModelScope.launch {
            repository.updatePlatformSettings(settings)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetDatabaseToDefaults()
            clearCart()
        }
    }
}

class StoreViewModelFactory(private val repository: AmarDokanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
