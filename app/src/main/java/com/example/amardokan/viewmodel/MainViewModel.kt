package com.example.amardokan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.amardokan.data.InitialData
import com.example.amardokan.model.*
import com.example.amardokan.repository.AmarDokanRepository
import com.example.amardokan.repository.VendorLoginResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: AmarDokanRepository
) : ViewModel() {

    // Database flows
    val shops = repository.shops.stateIn(viewModelScope, SharingStarted.Lazily, InitialData.SHOPS)
    val products = repository.products.stateIn(viewModelScope, SharingStarted.Lazily, InitialData.PRODUCTS)
    val orders = repository.orders.stateIn(viewModelScope, SharingStarted.Lazily, InitialData.ORDERS)
    val marketingPosts = repository.marketingPosts.stateIn(viewModelScope, SharingStarted.Lazily, InitialData.MARKETING_POSTS)
    val reviews = repository.reviews.stateIn(viewModelScope, SharingStarted.Lazily, InitialData.REVIEWS)
    val platformSettings = repository.platformSettings.stateIn(viewModelScope, SharingStarted.Lazily, InitialData.SETTINGS)

    val cart = repository.cart
    val activeShopId = repository.activeShopId
    val loggedInVendorEmail = repository.loggedInVendorEmail
    val isAdminLoggedIn = repository.isAdminLoggedIn

    val cartItemCount = cart.map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val cartSubtotal = cart.map { items -> items.sumOf { it.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    // Navigation and UI state
    private val _activeTab = MutableStateFlow(0) // 0: Storefront, 1: Vendor, 2: Tracker, 3: Admin
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _selectedUnion = MutableStateFlow("")
    val selectedUnion: StateFlow<String> = _selectedUnion.asStateFlow()

    private val _selectedCategory = MutableStateFlow("সব ক্যাটাগরি")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Modals / Sheets
    private val _isCartSheetOpen = MutableStateFlow(false)
    val isCartSheetOpen: StateFlow<Boolean> = _isCartSheetOpen.asStateFlow()

    private val _isCheckoutDialogOpen = MutableStateFlow(false)
    val isCheckoutDialogOpen: StateFlow<Boolean> = _isCheckoutDialogOpen.asStateFlow()

    private val _isShopRegistrationOpen = MutableStateFlow(false)
    val isShopRegistrationOpen: StateFlow<Boolean> = _isShopRegistrationOpen.asStateFlow()

    private val _isVendorLoginOpen = MutableStateFlow(false)
    val isVendorLoginOpen: StateFlow<Boolean> = _isVendorLoginOpen.asStateFlow()

    private val _detailedProduct = MutableStateFlow<Product?>(null)
    val detailedProduct: StateFlow<Product?> = _detailedProduct.asStateFlow()

    private val _orderSuccessIds = MutableStateFlow<List<String>?>(null)
    val orderSuccessIds: StateFlow<List<String>?> = _orderSuccessIds.asStateFlow()

    private val _trackerSearchQuery = MutableStateFlow("")
    val trackerSearchQuery: StateFlow<String> = _trackerSearchQuery.asStateFlow()

    private val _trackerResults = MutableStateFlow<List<Order>>(emptyList())
    val trackerResults: StateFlow<List<Order>> = _trackerResults.asStateFlow()

    private val _isTrackerSearched = MutableStateFlow(false)
    val isTrackerSearched: StateFlow<Boolean> = _isTrackerSearched.asStateFlow()

    // SnackBar / Toast message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun showUserMessage(message: String) {
        _userMessage.value = message
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun setSelectedUnion(union: String) {
        _selectedUnion.value = union
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun resetFilters() {
        _selectedUnion.value = ""
        _selectedCategory.value = "সব ক্যাটাগরি"
        _searchQuery.value = ""
    }

    fun setCartSheetOpen(isOpen: Boolean) {
        _isCartSheetOpen.value = isOpen
    }

    fun setCheckoutDialogOpen(isOpen: Boolean) {
        _isCheckoutDialogOpen.value = isOpen
    }

    fun setShopRegistrationOpen(isOpen: Boolean) {
        _isShopRegistrationOpen.value = isOpen
    }

    fun setVendorLoginOpen(isOpen: Boolean) {
        _isVendorLoginOpen.value = isOpen
    }

    fun setDetailedProduct(product: Product?) {
        _detailedProduct.value = product
    }

    fun clearOrderSuccess() {
        _orderSuccessIds.value = null
    }

    // Cart actions
    fun addToCart(product: Product, quantity: Int = 1) {
        repository.addToCart(product, quantity)
        showUserMessage("\"${product.productName}\" ব্যাগে যোগ করা হয়েছে।")
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        repository.updateCartQuantity(productId, quantity)
    }

    fun removeFromCart(productId: String) {
        repository.removeFromCart(productId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    // Place Order
    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        union: String,
        paymentMethod: String,
        notes: String
    ) {
        viewModelScope.launch {
            val orderIds = repository.placeOrder(
                customerName,
                customerPhone,
                deliveryAddress,
                union,
                paymentMethod,
                notes
            )
            if (orderIds.isNotEmpty()) {
                _orderSuccessIds.value = orderIds
                _isCheckoutDialogOpen.value = false
                _isCartSheetOpen.value = false
            }
        }
    }

    // Order Tracker Search
    fun searchTracker(query: String) {
        _trackerSearchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _trackerResults.value = emptyList()
                _isTrackerSearched.value = false
            } else {
                val results = repository.searchOrders(query)
                _trackerResults.value = results
                _isTrackerSearched.value = true
            }
        }
    }

    // Vendor actions
    fun setActiveShopId(shopId: String) {
        repository.setActiveShopId(shopId)
    }

    fun loginVendorByEmail(email: String, onResult: (VendorLoginResult) -> Unit) {
        viewModelScope.launch {
            val res = repository.loginVendorByEmail(email)
            onResult(res)
        }
    }

    fun logoutVendor() {
        repository.logoutVendor()
        showUserMessage("উদ্যোক্তা প্যানেল থেকে লগ আউট সম্পন্ন হয়েছে।")
    }

    fun addProduct(
        shopId: String,
        productName: String,
        price: Double,
        discountPrice: Double?,
        stock: Int,
        description: String,
        imageUrl: String,
        category: String,
        unit: String
    ) {
        viewModelScope.launch {
            repository.addProduct(
                shopId,
                productName,
                price,
                discountPrice,
                stock,
                description,
                imageUrl,
                category,
                unit
            )
            showUserMessage("নতুন পণ্য সফলভাবে যুক্ত হয়েছে!")
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            showUserMessage("পণ্য আপডেট সম্পন্ন হয়েছে!")
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            showUserMessage("পণ্য ডিলিট করা হয়েছে।")
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            showUserMessage("অর্ডার #$orderId এর স্ট্যাটাস: $status")
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
            showUserMessage("অর্ডার ডিলিট করা হয়েছে।")
        }
    }

    // AI Marketing Generation
    fun generateMarketingCopy(
        product: Product,
        shop: Shop,
        tone: String,
        customInstruction: String,
        onGenerated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val copy = repository.generateMarketingCopy(product, shop, tone, customInstruction)
            onGenerated(copy)
        }
    }

    fun saveMarketingPost(
        shopId: String,
        shopName: String,
        productName: String,
        generatedText: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            repository.saveMarketingPost(shopId, shopName, productName, generatedText, imageUrl)
            showUserMessage("মার্কেটিং পোস্ট সেভ করা হয়েছে!")
        }
    }

    fun deleteMarketingPost(postId: String) {
        viewModelScope.launch {
            repository.deleteMarketingPost(postId)
            showUserMessage("পোস্ট ডিলিট করা হয়েছে।")
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
        logoUrl: String
    ) {
        viewModelScope.launch {
            repository.registerShop(
                shopName,
                ownerName,
                phone,
                email,
                address,
                union,
                category,
                description,
                logoUrl
            )
            _isShopRegistrationOpen.value = false
            showUserMessage("আপনার দোকানের আবেদনটি জমা হয়েছে! অ্যাডমিন অনুমোদনের পর ড্যাশবোর্ডে প্রবেশ করতে পারবেন।")
        }
    }

    // Reviews
    fun addReview(
        shopId: String,
        productId: String?,
        customerName: String,
        rating: Int,
        comment: String
    ) {
        viewModelScope.launch {
            repository.addReview(shopId, productId, customerName, rating, comment)
            showUserMessage("আপনার রিভিউ সফলভাবে যুক্ত হয়েছে! ধন্যবাদ।")
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            showUserMessage("রিভিউ ডিলিট করা হয়েছে।")
        }
    }

    // Admin
    fun loginAdmin(email: String, pass: String): Boolean {
        val success = repository.loginAdmin(email, pass)
        if (success) {
            showUserMessage("অ্যাডমিন প্যানেলে সফলভাবে লগইন করেছেন!")
        } else {
            showUserMessage("ভুল ইমেইল বা পাসওয়ার্ড!")
        }
        return success
    }

    fun logoutAdmin() {
        repository.logoutAdmin()
        showUserMessage("অ্যাডমিন থেকে লগ আউট করা হয়েছে।")
    }

    fun approveShop(shopId: String) {
        viewModelScope.launch {
            repository.approveShop(shopId)
            showUserMessage("দোকানটি সক্রিয় (Active) করা হয়েছে!")
        }
    }

    fun rejectShop(shopId: String) {
        viewModelScope.launch {
            repository.rejectShop(shopId)
            showUserMessage("দোকানটির আবেদন বাতিল (Rejected) করা হয়েছে।")
        }
    }

    fun deleteShop(shopId: String) {
        viewModelScope.launch {
            repository.deleteShop(shopId)
            showUserMessage("দোকান ডিলিট করা হয়েছে।")
        }
    }

    fun updateShop(shop: Shop) {
        viewModelScope.launch {
            repository.updateShop(shop)
            showUserMessage("দোকানের তথ্য আপডেট করা হয়েছে।")
        }
    }

    fun addShopDirectly(shop: Shop) {
        viewModelScope.launch {
            repository.addShopDirectly(shop)
            showUserMessage("নতুন দোকান সরাসরি যুক্ত হয়েছে!")
        }
    }

    fun updatePlatformSettings(settings: PlatformSettings) {
        viewModelScope.launch {
            repository.updatePlatformSettings(settings)
            showUserMessage("প্ল্যাটফর্ম সেটিংস আপডেট সম্পন্ন!")
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaults()
            showUserMessage("সিস্টেমের সকল ডেটা প্রাথমিক অবস্থায় রিস্টোর করা হয়েছে।")
        }
    }
}

class MainViewModelFactory(
    private val repository: AmarDokanRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}
