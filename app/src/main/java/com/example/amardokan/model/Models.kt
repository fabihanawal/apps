package com.example.amardokan.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object Constants {
    val BADALGACHHI_UNIONS = listOf(
        "বদলগাছী সদর",
        "পাহাড়পুর",
        "মথুরাপুর",
        "বালুভরা",
        "কোলা",
        "বিলাশবাড়ী",
        "আধাইপুর",
        "মিঠাপুর"
    )

    val PRODUCT_CATEGORIES = listOf(
        "সব ক্যাটাগরি",
        "কাঁচাবাজার ও মুদি",
        "তৈরি পোশাক ও বস্ত্র",
        "স্থানীয় হস্তশিল্প ও ঐতিহ্য",
        "কৃষি পণ্য ও বীজ",
        "মিষ্টি, দই ও বেকারি",
        "ইলেকট্রনিক্স ও গ্যাজেট",
        "স্বাস্থ্য ও প্রসাধনী"
    )

    val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }
}

@Serializable
@Entity(tableName = "shops")
data class Shop(
    @PrimaryKey val shopId: String,
    val shopName: String,
    val ownerName: String,
    val phone: String,
    val email: String,
    val address: String = "",
    val union: String,
    val upazila: String = "Badalgachhi",
    val district: String = "Naogaon",
    val category: String,
    val status: String = "Active", // "Active", "Pending", "Rejected"
    val description: String = "",
    val logoUrl: String = "",
    val rating: Double = 5.0,
    val totalReviews: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val productId: String,
    val shopId: String,
    val productName: String,
    val price: Double,
    val discountPrice: Double? = null,
    val stock: Int,
    val description: String = "",
    val imageUrl: String = "",
    val category: String,
    val unit: String = "১ টি",
    val createdAt: Long = System.currentTimeMillis()
) {
    val effectivePrice: Double
        get() = discountPrice ?: price
}

@Serializable
data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String
)

@Serializable
@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val orderId: String,
    val shopId: String,
    val shopName: String = "",
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val union: String,
    val itemsJson: String = "[]",
    val totalAmount: Double,
    val platformCommission: Double,
    val orderStatus: String = "Pending", // "Pending", "Confirmed", "Packed", "Shipped", "Delivered", "Cancelled"
    val paymentMethod: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    fun getItems(): List<OrderItem> {
        return try {
            Constants.json.decodeFromString<List<OrderItem>>(itemsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Serializable
data class CartItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String,
    val shopId: String,
    val maxStock: Int
)

@Serializable
@Entity(tableName = "marketing_posts")
data class MarketingPost(
    @PrimaryKey val postId: String,
    val shopId: String,
    val shopName: String,
    val productName: String,
    val generatedText: String,
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey val reviewId: String,
    val shopId: String,
    val productId: String? = null,
    val customerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "platform_settings")
data class PlatformSettings(
    @PrimaryKey val id: Int = 1,
    val announcementText: String = "বদলগাছী উপজেলার ৮টি ইউনিয়নে দ্রুততম হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা!",
    val heroHeadline: String = "ঘরের কাছে সেরা পণ্য, আমার দোকান এ সরাসরি অর্ডার!",
    val heroSubheadline: String = "ঐতিহাসিক পাহাড়পুর থেকে শুরু করে কোলা, বালুভরা ও সদর ইউনিয়নের বিশ্বস্ত উদ্যোক্তাদের তৈরি খাঁটি মিষ্টি, হস্তশিল্প, তাজা কৃষিপণ্য ও গ্রোসারি।",
    val helplinePhone: String = "01755383039",
    val whatsappNumber: String = "01755383039",
    val deliveryCharge: Double = 30.0,
    val minFreeDeliveryAmount: Double = 500.0,
    val commissionPercent: Double = 5.0
)
