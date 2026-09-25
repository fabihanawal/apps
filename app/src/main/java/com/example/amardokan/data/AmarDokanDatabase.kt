package com.example.amardokan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.amardokan.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Shop::class,
        Product::class,
        Order::class,
        MarketingPost::class,
        Review::class,
        PlatformSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AmarDokanDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun marketingPostDao(): MarketingPostDao
    abstract fun reviewDao(): ReviewDao
    abstract fun platformSettingsDao(): PlatformSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AmarDokanDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AmarDokanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AmarDokanDatabase::class.java,
                    "amar_dokan_database"
                )
                    .addCallback(AmarDokanDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AmarDokanDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(database: AmarDokanDatabase) {
            database.shopDao().insertShops(InitialData.SHOPS)
            database.productDao().insertProducts(InitialData.PRODUCTS)
            database.orderDao().insertOrders(InitialData.ORDERS)
            database.marketingPostDao().insertMarketingPosts(InitialData.MARKETING_POSTS)
            database.reviewDao().insertReviews(InitialData.REVIEWS)
            database.platformSettingsDao().insertOrUpdateSettings(InitialData.SETTINGS)
        }
    }
}
