package com.example.amardokan.data.repository

import com.example.amardokan.data.local.AmarDokanDatabase
import com.example.amardokan.data.local.Converters
import com.example.amardokan.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.random.Random

class AmarDokanRepository(private val database: AmarDokanDatabase) {
    val allShops: Flow<List<Shop>> = database.shopDao().getAllShops()
    val activeShops: Flow<List<Shop>> = database.shopDao().getActiveShops()
    val allProducts: Flow<List<Product>> = database.productDao().getAllProducts()
    val allOrders: Flow<List<Order>> = database.orderDao().getAllOrders()
    val allReviews: Flow<List<Review>> = database.reviewDao().getAllReviews()
    val allMarketingPosts: Flow<List<MarketingPost>> = database.marketingPostDao().getAllMarketingPosts()
    val platformSettings: Flow<PlatformSettings?> = database.platformSettingsDao().getSettings()

    fun getProductsByShop(shopId: String): Flow<List<Product>> =
        database.productDao().getProductsByShop(shopId)

    fun getOrdersByShop(shopId: String): Flow<List<Order>> =
        database.orderDao().getOrdersByShop(shopId)

    fun getReviewsByShop(shopId: String): Flow<List<Review>> =
        database.reviewDao().getReviewsByShop(shopId)

    fun searchOrders(query: String): Flow<List<Order>> =
        database.orderDao().searchOrders(query)

    suspend fun getShopById(shopId: String): Shop? = withContext(Dispatchers.IO) {
        database.shopDao().getShopById(shopId)
    }

    suspend fun getShopByEmail(email: String): Shop? = withContext(Dispatchers.IO) {
        database.shopDao().getShopByEmail(email)
    }

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
            status = "Pending", // Awaiting admin approval
            description = description,
            logoUrl = logoUrl.ifBlank { "https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=500&auto=format&fit=crop&q=60" },
            rating = 5.0,
            totalReviews = 1,
            createdAt = System.currentTimeMillis()
        )
        database.shopDao().insertShop(newShop)
        newShop
    }

    suspend fun approveShop(shopId: String) = withContext(Dispatchers.IO) {
        val shop = database.shopDao().getShopById(shopId)
        if (shop != null) {
            database.shopDao().updateShop(shop.copy(status = "Active"))
        }
    }

    suspend fun rejectShop(shopId: String) = withContext(Dispatchers.IO) {
        val shop = database.shopDao().getShopById(shopId)
        if (shop != null) {
            database.shopDao().updateShop(shop.copy(status = "Rejected"))
        }
    }

    suspend fun deleteShop(shopId: String) = withContext(Dispatchers.IO) {
        database.shopDao().deleteShopById(shopId)
    }

    suspend fun addProduct(
        shopId: String,
        productName: String,
        price: Double,
        discountPrice: Double?,
        stock: Int,
        unit: String,
        category: String,
        description: String,
        imageUrl: String
    ): Product = withContext(Dispatchers.IO) {
        val newProduct = Product(
            productId = "prod-${System.currentTimeMillis()}",
            shopId = shopId,
            productName = productName,
            price = price,
            discountPrice = discountPrice,
            stock = stock,
            unit = unit.ifBlank { "১ টি" },
            category = category,
            description = description,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1542838132-92c53300491e?w=700&auto=format&fit=crop&q=80" },
            createdAt = System.currentTimeMillis()
        )
        database.productDao().insertProduct(newProduct)
        newProduct
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        database.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(productId: String) = withContext(Dispatchers.IO) {
        database.productDao().deleteProductById(productId)
    }

    suspend fun placeOrder(
        cartItems: List<CartItem>,
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        union: String,
        paymentMethod: String,
        notes: String?
    ): List<Order> = withContext(Dispatchers.IO) {
        if (cartItems.isEmpty()) return@withContext emptyList()

        val grouped = cartItems.groupBy { it.shopId }
        val createdOrders = mutableListOf<Order>()
        val converters = Converters()

        for ((shopId, items) in grouped) {
            val shop = database.shopDao().getShopById(shopId)
            val totalAmount = items.sumOf { it.price * it.quantity }
            val commission = Math.round(totalAmount * 0.05).toDouble()
            val orderId = "ORD-BDL-${Random.nextInt(10000, 99999)}"

            val orderItems = items.map {
                OrderItem(
                    productId = it.productId,
                    productName = it.productName,
                    quantity = it.quantity,
                    price = it.price,
                    imageUrl = it.imageUrl
                )
            }

            val order = Order(
                orderId = orderId,
                shopId = shopId,
                shopName = shop?.shopName ?: "আমার দোকান বিক্রেতা",
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                union = union,
                itemsJson = converters.fromOrderItemList(orderItems),
                totalAmount = totalAmount,
                platformCommission = commission,
                orderStatus = "Pending",
                paymentMethod = paymentMethod,
                timestamp = System.currentTimeMillis(),
                notes = notes
            )
            database.orderDao().insertOrder(order)
            createdOrders.add(order)

            // Decrement stock
            for (it in items) {
                database.productDao().decrementStock(it.productId, it.quantity)
            }
        }

        createdOrders
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        database.orderDao().updateOrderStatus(orderId, status)
    }

    suspend fun deleteOrder(orderId: String) = withContext(Dispatchers.IO) {
        database.orderDao().deleteOrderById(orderId)
    }

    suspend fun addReview(
        shopId: String,
        productId: String?,
        customerName: String,
        rating: Int,
        comment: String
    ): Review = withContext(Dispatchers.IO) {
        val review = Review(
            reviewId = "rev-${System.currentTimeMillis()}",
            shopId = shopId,
            productId = productId,
            customerName = customerName,
            rating = rating,
            comment = comment,
            createdAt = System.currentTimeMillis()
        )
        database.reviewDao().insertReview(review)
        review
    }

    suspend fun saveMarketingPost(
        shopId: String,
        shopName: String,
        productName: String,
        generatedText: String,
        imageUrl: String?
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
        database.marketingPostDao().insertMarketingPost(post)
        post
    }

    suspend fun updatePlatformSettings(settings: PlatformSettings) = withContext(Dispatchers.IO) {
        database.platformSettingsDao().updateSettings(settings)
    }

    suspend fun resetDatabaseToDefaults() = withContext(Dispatchers.IO) {
        database.shopDao().clearAllShops()
        database.productDao().clearAllProducts()
        database.orderDao().clearAllOrders()
        database.reviewDao().clearAllReviews()
        database.marketingPostDao().clearAllMarketingPosts()
        AmarDokanDatabase.populateInitialData(database)
    }
}
