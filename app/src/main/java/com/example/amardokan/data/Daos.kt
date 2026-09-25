package com.example.amardokan.data

import androidx.room.*
import com.example.amardokan.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops ORDER BY createdAt DESC")
    fun getAllShops(): Flow<List<Shop>>

    @Query("SELECT * FROM shops WHERE shopId = :shopId")
    suspend fun getShopById(shopId: String): Shop?

    @Query("SELECT * FROM shops WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getShopByEmail(email: String): Shop?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: Shop)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShops(shops: List<Shop>)

    @Update
    suspend fun updateShop(shop: Shop)

    @Query("DELETE FROM shops WHERE shopId = :shopId")
    suspend fun deleteShop(shopId: String)

    @Query("DELETE FROM shops")
    suspend fun deleteAllShops()
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE shopId = :shopId ORDER BY createdAt DESC")
    fun getProductsByShop(shopId: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("UPDATE products SET stock = MAX(0, stock - :quantity) WHERE productId = :productId")
    suspend fun decrementStock(productId: String, quantity: Int)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE shopId = :shopId ORDER BY timestamp DESC")
    fun getOrdersByShop(shopId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId OR customerPhone = :query ORDER BY timestamp DESC")
    suspend fun searchOrders(orderId: String, query: String): List<Order>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>)

    @Query("UPDATE orders SET orderStatus = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Query("DELETE FROM orders WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: String)

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()
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
    suspend fun deleteMarketingPost(postId: String)

    @Query("DELETE FROM marketing_posts")
    suspend fun deleteAllMarketingPosts()
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
    suspend fun deleteReview(reviewId: String)

    @Query("DELETE FROM reviews")
    suspend fun deleteAllReviews()
}

@Dao
interface PlatformSettingsDao {
    @Query("SELECT * FROM platform_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<PlatformSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: PlatformSettings)
}
