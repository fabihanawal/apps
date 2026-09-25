package com.example.amardokan.repository

import com.example.amardokan.data.AmarDokanDatabase
import com.example.amardokan.data.InitialData
import com.example.amardokan.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlin.random.Random

sealed class VendorLoginResult {
    data class Success(val shop: Shop, val message: String) : VendorLoginResult()
    data class Pending(val shop: Shop, val message: String) : VendorLoginResult()
    data class Rejected(val shop: Shop, val message: String) : VendorLoginResult()
    data class NotFound(val message: String) : VendorLoginResult()
}

class AmarDokanRepository(
    private val database: AmarDokanDatabase,
    private val scope: CoroutineScope
) {
    private val shopDao = database.shopDao()
    private val productDao = database.productDao()
    private val orderDao = database.orderDao()
    private val marketingPostDao = database.marketingPostDao()
    private val reviewDao = database.reviewDao()
    private val settingsDao = database.platformSettingsDao()

    val shops: Flow<List<Shop>> = shopDao.getAllShops()
    val products: Flow<List<Product>> = productDao.getAllProducts()
    val orders: Flow<List<Order>> = orderDao.getAllOrders()
    val marketingPosts: Flow<List<MarketingPost>> = marketingPostDao.getAllMarketingPosts()
    val reviews: Flow<List<Review>> = reviewDao.getAllReviews()
    val platformSettings: Flow<PlatformSettings> = settingsDao.getSettings()
        .map { it ?: InitialData.SETTINGS }

    // In-memory cart state
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    // Active Vendor Shop ID
    private val _activeShopId = MutableStateFlow<String>("shop-paharpur-crafts")
    val activeShopId: StateFlow<String> = _activeShopId.asStateFlow()

    // Logged in Vendor Email
    private val _loggedInVendorEmail = MutableStateFlow<String?>(null)
    val loggedInVendorEmail: StateFlow<String?> = _loggedInVendorEmail.asStateFlow()

    // Admin Logged In State
    private val _isAdminLoggedIn = MutableStateFlow<Boolean>(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    fun setActiveShopId(shopId: String) {
        _activeShopId.value = shopId
    }

    // Cart Operations
    fun addToCart(product: Product, quantity: Int = 1) {
        val currentList = _cart.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.productId === product.productId }
        val effectivePrice = product.effectivePrice

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            val newQty = (existing.quantity + quantity).coerceAtMost(product.stock)
            currentList[existingIndex] = existing.copy(quantity = newQty)
        } else {
            currentList.add(
                CartItem(
                    productId = product.productId,
                    productName = product.productName,
                    quantity = quantity.coerceAtMost(product.stock),
                    price = effectivePrice,
                    imageUrl = product.imageUrl,
                    shopId = product.shopId,
                    maxStock = product.stock
                )
            )
        }
        _cart.value = currentList
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        _cart.value = _cart.value.map { item ->
            if (item.productId == productId) {
                item.copy(quantity = quantity.coerceAtMost(item.maxStock))
            } else {
                item
            }
        }
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filter { it.productId != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Checkout / Place Order
    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        union: String,
        paymentMethod: String,
        notes: String = ""
    ): List<String> = withContext(Dispatchers.IO) {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) return@withContext emptyList()

        // Group by shopId
        val grouped = currentCart.groupBy { it.shopId }
        val createdOrderIds = mutableListOf<String>()

        grouped.forEach { (shopId, items) ->
            val shop = shopDao.getShopById(shopId)
            val totalAmount = items.sumOf { it.price * it.quantity }
            val commission = Math.round(totalAmount * 0.05).toDouble()
            val randomId = Random.nextInt(10000, 99999)
            val orderId = "ORD-BDL-$randomId"

            val orderItems = items.map {
                OrderItem(
                    productId = it.productId,
                    productName = it.productName,
                    quantity = it.quantity,
                    price = it.price,
                    imageUrl = it.imageUrl
                )
            }

            val newOrder = Order(
                orderId = orderId,
                shopId = shopId,
                shopName = shop?.shopName ?: "আমার দোকান বিক্রেতা",
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                union = union,
                itemsJson = Constants.json.encodeToString(orderItems),
                totalAmount = totalAmount,
                platformCommission = commission,
                orderStatus = "Pending",
                paymentMethod = paymentMethod,
                timestamp = System.currentTimeMillis(),
                notes = notes
            )

            orderDao.insertOrder(newOrder)
            createdOrderIds.add(orderId)

            // Decrement stock for ordered products
            items.forEach { item ->
                productDao.decrementStock(item.productId, item.quantity)
            }
        }

        clearCart()
        createdOrderIds
    }

    // Search Order for Tracker
    suspend fun searchOrders(query: String): List<Order> = withContext(Dispatchers.IO) {
        orderDao.searchOrders(query.trim(), query.trim())
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun deleteOrder(orderId: String) = withContext(Dispatchers.IO) {
        orderDao.deleteOrder(orderId)
    }

    // Product CRUD
    suspend fun addProduct(
        shopId: String,
        productName: String,
        price: Double,
        discountPrice: Double?,
        stock: Int,
        description: String,
        imageUrl: String,
        category: String,
        unit: String
    ): Product = withContext(Dispatchers.IO) {
        val newProduct = Product(
            productId = "prod-${System.currentTimeMillis()}",
            shopId = shopId,
            productName = productName,
            price = price,
            discountPrice = discountPrice,
            stock = stock,
            description = description,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=700" },
            category = category,
            unit = unit.ifBlank { "১ টি" },
            createdAt = System.currentTimeMillis()
        )
        productDao.insertProduct(newProduct)
        newProduct
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(productId: String) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(productId)
        removeFromCart(productId)
    }

    // Shop Management
    suspend fun registerShop(
        shopName: String,
        ownerName: String,
        phone: String,
        email: String,
        address: String,
        union: String,
        category: String,
        description: String,
        logoUrl: String
    ): Shop = withContext(Dispatchers.IO) {
        val newShop = Shop(
            shopId = "shop-${System.currentTimeMillis()}",
            shopName = shopName,
            ownerName = ownerName,
            phone = phone,
            email = email,
            address = address,
            union = union,
            category = category,
            status = "Pending",
            description = description,
            logoUrl = logoUrl.ifBlank { "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=500" },
            rating = 5.0,
            totalReviews = 1,
            createdAt = System.currentTimeMillis()
        )
        shopDao.insertShop(newShop)
        newShop
    }

    suspend fun addShopDirectly(shop: Shop) = withContext(Dispatchers.IO) {
        shopDao.insertShop(shop)
    }

    suspend fun updateShop(shop: Shop) = withContext(Dispatchers.IO) {
        shopDao.updateShop(shop)
    }

    suspend fun approveShop(shopId: String) = withContext(Dispatchers.IO) {
        val shop = shopDao.getShopById(shopId)
        if (shop != null) {
            shopDao.updateShop(shop.copy(status = "Active"))
        }
    }

    suspend fun rejectShop(shopId: String) = withContext(Dispatchers.IO) {
        val shop = shopDao.getShopById(shopId)
        if (shop != null) {
            shopDao.updateShop(shop.copy(status = "Rejected"))
        }
    }

    suspend fun deleteShop(shopId: String) = withContext(Dispatchers.IO) {
        shopDao.deleteShop(shopId)
    }

    // Vendor Login by Email
    suspend fun loginVendorByEmail(email: String): VendorLoginResult = withContext(Dispatchers.IO) {
        val normalized = email.trim().lowercase()
        val shop = shopDao.getShopByEmail(normalized)
            ?: return@withContext VendorLoginResult.NotFound(
                "এই ইমেইল এড্রেস দিয়ে কোনো দোকান পাওয়া যায়নি। অনুগ্রহ করে নতুন দোকান নিবন্ধন আবেদন করুন।"
            )

        when (shop.status) {
            "Pending" -> VendorLoginResult.Pending(
                shop,
                "দোকান \"${shop.shopName}\" এর আবেদনটি এখনও অ্যাডমিনের বিবেচনাধীন রয়েছে। অনুমোদন নিশ্চিত হলে ড্যাশবোর্ডে প্রবেশ করতে পারবেন। হেল্পলাইন: 01755383039"
            )
            "Rejected" -> VendorLoginResult.Rejected(
                shop,
                "দোকান \"${shop.shopName}\" এর আবেদনটি স্থগিত করা হয়েছে। যোগাযোগ: 01755383039"
            )
            else -> {
                _loggedInVendorEmail.value = shop.email
                _activeShopId.value = shop.shopId
                VendorLoginResult.Success(
                    shop,
                    "স্বাগতম! \"${shop.shopName}\" এর ড্যাশবোর্ডে আপনি সফলভাবে প্রবেশ করেছেন।"
                )
            }
        }
    }

    fun logoutVendor() {
        _loggedInVendorEmail.value = null
    }

    // Admin Auth
    fun loginAdmin(email: String, pass: String): Boolean {
        if (email.trim().equals("rstsbd@gmail.com", ignoreCase = true) && pass == "rstsbd1234") {
            _isAdminLoggedIn.value = true
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    // Platform Settings update
    suspend fun updatePlatformSettings(settings: PlatformSettings) = withContext(Dispatchers.IO) {
        settingsDao.insertOrUpdateSettings(settings)
    }

    // Reviews
    suspend fun addReview(
        shopId: String,
        productId: String?,
        customerName: String,
        rating: Int,
        comment: String
    ): Review = withContext(Dispatchers.IO) {
        val newReview = Review(
            reviewId = "rev-${System.currentTimeMillis()}",
            shopId = shopId,
            productId = productId,
            customerName = customerName,
            rating = rating,
            comment = comment,
            createdAt = System.currentTimeMillis()
        )
        reviewDao.insertReview(newReview)

        // Recalculate average rating for shop
        val shop = shopDao.getShopById(shopId)
        if (shop != null) {
            val updatedTotalReviews = shop.totalReviews + 1
            val updatedRating = ((shop.rating * shop.totalReviews) + rating) / updatedTotalReviews
            val roundedRating = Math.round(updatedRating * 10.0) / 10.0
            shopDao.updateShop(shop.copy(rating = roundedRating, totalReviews = updatedTotalReviews))
        }
        newReview
    }

    suspend fun deleteReview(reviewId: String) = withContext(Dispatchers.IO) {
        reviewDao.deleteReview(reviewId)
    }

    // AI Marketing Generator
    suspend fun generateMarketingCopy(
        product: Product,
        shop: Shop,
        tone: String,
        customInstruction: String
    ): String {
        val discountText = if (product.discountPrice != null) {
            "🏷️ বিশেষ ছাড় মূল্য: মাত্র ৳${product.discountPrice.toInt()} (পূর্বমূল্য ৳${product.price.toInt()})"
        } else {
            "🏷️ মূল্য: মাত্র ৳${product.price.toInt()}"
        }

        return """
            🔥 বদলগাছীর সেরা অফার! ✨ [${tone}]
            
            ${shop.shopName} নিয়ে এলো আপনার পছন্দের "${product.productName}"।
            ${product.description.ifBlank { "খাঁটি মান ও অতুলনীয় স্বাদের নিশ্চয়তা।" }}
            
            $discountText
            📦 ইউনিয়ন: ${shop.union}, বদলগাছী, নওগাঁ
            🛵 বদলগাছী উপজেলার যেকোনো প্রান্তে দ্রুততম ক্যাশ অন ডেলিভারি সুবিধা!
            ${if (customInstruction.isNotBlank()) "\n💡 বিশেষ বার্তা: $customInstruction" else ""}
            
            👉 স্টক শেষ হবার আগেই অর্ডার করুন!
            📞 WhatsApp / সরাসরি কল: ${shop.phone}
            🌐 "আমার দোকান" মোবাইল অ্যাপে ঘরে বসেই অর্ডার করুন।
            
            #বদলগাছী #নওগাঁ #আমারদোকান #${shop.union.replace(" ", "")} #RSTS_BD
        """.trimIndent()
    }

    suspend fun saveMarketingPost(
        shopId: String,
        shopName: String,
        productName: String,
        generatedText: String,
        imageUrl: String
    ): MarketingPost = withContext(Dispatchers.IO) {
        val post = MarketingPost(
            postId = "post-${System.currentTimeMillis()}",
            shopId = shopId,
            shopName = shopName,
            productName = productName,
            generatedText = generatedText,
            imageUrl = imageUrl,
            createdAt = System.currentTimeMillis()
        )
        marketingPostDao.insertMarketingPost(post)
        post
    }

    suspend fun deleteMarketingPost(postId: String) = withContext(Dispatchers.IO) {
        marketingPostDao.deleteMarketingPost(postId)
    }

    // Reset to Defaults
    suspend fun resetToDefaults() = withContext(Dispatchers.IO) {
        shopDao.deleteAllShops()
        productDao.deleteAllProducts()
        orderDao.deleteAllOrders()
        marketingPostDao.deleteAllMarketingPosts()
        reviewDao.deleteAllReviews()

        shopDao.insertShops(InitialData.SHOPS)
        productDao.insertProducts(InitialData.PRODUCTS)
        orderDao.insertOrders(InitialData.ORDERS)
        marketingPostDao.insertMarketingPosts(InitialData.MARKETING_POSTS)
        reviewDao.insertReviews(InitialData.REVIEWS)
        settingsDao.insertOrUpdateSettings(InitialData.SETTINGS)

        clearCart()
        _activeShopId.value = InitialData.SHOPS[0].shopId
    }
}
