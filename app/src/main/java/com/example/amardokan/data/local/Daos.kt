package com.example.amardokan.data.local

import androidx.room.*
import com.example.amardokan.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops ORDER BY createdAt DESC")
    fun getAllShops(): Flow<List<Shop>>

    @Query("SELECT * FROM shops WHERE status = 'Active' ORDER BY createdAt DESC")
    fun getActiveShops(): Flow<List<Shop>>

    @Query("SELECT * FROM shops WHERE shopId = :shopId LIMIT 1")
    suspend fun getShopById(shopId: String): Shop?

    @Query("SELECT * FROM shops WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email)) LIMIT 1")
    suspend fun getShopByEmail(email: String): Shop?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: Shop)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShops(shops: List<Shop>)

    @Update
    suspend fun updateShop(shop: Shop)

    @Query("DELETE FROM shops WHERE shopId = :shopId")
    suspend fun deleteShopById(shopId: String)

    @Query("DELETE FROM shops")
    suspend fun clearAllShops()
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getProductsByShop(shopId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE productId = :productId LIMIT 1")
    suspend fun getProductById(productId: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET stock = MAX(0, stock - :quantity) WHERE productId = :productId")
    suspend fun decrementStock(productId: String, quantity: Int)

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProductById(productId: String)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE shopId = :shopId ORDER BY timestamp DESC")
    fun getOrdersByShop(shopId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): Order?

    @Query("SELECT * FROM orders WHERE orderId = :query OR customerPhone LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchOrders(query: String): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>)

    @Query("UPDATE orders SET orderStatus = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrderById(orderId: String)

    @Query("DELETE FROM orders")
    suspend fun clearAllOrders()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getReviewsByShop(shopId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)

    @Query("DELETE FROM reviews WHERE reviewId = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("DELETE FROM reviews")
    suspend fun clearAllReviews()
}

@Dao
interface MarketingPostDao {
    @Query("SELECT * FROM marketing_posts ORDER BY createdAt DESC")
    fun getAllMarketingPosts(): Flow<List<MarketingPost>>

    @Query("SELECT * FROM marketing_posts WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getMarketingPostsByShop(shopId: String): Flow<List<MarketingPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketingPost(post: MarketingPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketingPosts(posts: List<MarketingPost>)

    @Query("DELETE FROM marketing_posts WHERE postId = :postId")
    suspend fun deleteMarketingPostById(postId: String)

    @Query("DELETE FROM marketing_posts")
    suspend fun clearAllMarketingPosts()
}

@Dao
interface PlatformSettingsDao {
    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<PlatformSettings?>

    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsOnce(): PlatformSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: PlatformSettings)
}
