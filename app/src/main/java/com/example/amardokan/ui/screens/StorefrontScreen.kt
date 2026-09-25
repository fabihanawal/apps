package com.example.amardokan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amardokan.model.Constants
import com.example.amardokan.model.Product
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.components.*
import com.example.amardokan.ui.theme.*
import com.example.amardokan.viewmodel.MainViewModel

@Composable
fun StorefrontScreen(
    viewModel: MainViewModel,
    onProductClick: (Product) -> Unit,
    onShopClick: (Shop) -> Unit
) {
    val shops by viewModel.shops.collectAsState()
    val products by viewModel.products.collectAsState()
    val settings by viewModel.platformSettings.collectAsState()
    val selectedUnion by viewModel.selectedUnion.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Shop map for rapid lookup
    val shopMap = remember(shops) {
        shops.associateBy { it.shopId }
    }

    // Filtered Products: only from Active shops
    val filteredProducts = remember(products, shopMap, selectedUnion, selectedCategory, searchQuery) {
        products.filter { product ->
            val shop = shopMap[product.shopId]
            if (shop == null || shop.status != "Active") return@filter false

            if (selectedUnion.isNotEmpty() && shop.union != selectedUnion) return@filter false

            if (selectedCategory != "সব ক্যাটাগরি" && product.category != selectedCategory) return@filter false

            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                val matchName = product.productName.lowercase().contains(q)
                val matchShop = shop.shopName.lowercase().contains(q)
                val matchCategory = product.category.lowercase().contains(q)
                val matchUnion = shop.union.lowercase().contains(q)
                if (!matchName && !matchShop && !matchCategory && !matchUnion) return@filter false
            }

            true
        }
    }

    // Active Shops
    val activeShops = remember(shops, selectedUnion) {
        val active = shops.filter { it.status == "Active" }
        if (selectedUnion.isEmpty()) active else active.filter { it.union == selectedUnion }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Stone50),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Section
        item {
            HeroBannerView(
                headline = settings.heroHeadline,
                subheadline = settings.heroSubheadline,
                totalShops = activeShops.size,
                totalProducts = products.size,
                selectedUnion = selectedUnion,
                onUnionSelected = { viewModel.setSelectedUnion(it) },
                onOpenRegisterShop = { viewModel.setShopRegistrationOpen(true) }
            )
        }

        // Category Filter Chips
        item {
            CategoryFilterChips(
                categories = Constants.PRODUCT_CATEGORIES,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setSelectedCategory(it) }
            )
        }

        // Active Filter Indicators
        if (selectedUnion.isNotEmpty() || selectedCategory != "সব ক্যাটাগরি" || searchQuery.isNotEmpty()) {
            item {
                Surface(
                    color = Emerald50,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ফিল্টার: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald800)
                            if (selectedUnion.isNotEmpty()) {
                                Text("📍 $selectedUnion  ", fontSize = 11.sp, color = Emerald700)
                            }
                            if (selectedCategory != "সব ক্যাটাগরি") {
                                Text("🏷️ $selectedCategory  ", fontSize = 11.sp, color = Emerald700)
                            }
                            if (searchQuery.isNotEmpty()) {
                                Text("🔍 \"$searchQuery\"", fontSize = 11.sp, color = Emerald700)
                            }
                        }

                        TextButton(
                            onClick = { viewModel.resetFilters() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("রিসেট", color = Emerald800, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Products Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "পণ্য তালিকা (${filteredProducts.size}টি পণ্য)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                }
            }
        }

        // Products 2-column Grid within LazyColumn
        if (filteredProducts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = Stone400, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো পণ্য খুঁজে পাওয়া যায়নি",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone800
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "অনুগ্রহ করে অন্য ইউনিয়ন বা ক্যাটাগরি নির্বাচন করে চেষ্টা করুন।",
                            fontSize = 12.sp,
                            color = Stone500
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.resetFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("সকল ফিল্টার মুছুন")
                        }
                    }
                }
            }
        } else {
            // Render products chunked in rows of 2 for smooth scrolling in LazyColumn
            val productPairs = filteredProducts.chunked(2)
            items(productPairs) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProductCard(
                        product = pair[0],
                        shop = shopMap[pair[0].shopId],
                        onCardClick = { onProductClick(pair[0]) },
                        onAddToCart = { viewModel.addToCart(pair[0], 1) },
                        modifier = Modifier.weight(1f)
                    )

                    if (pair.size > 1) {
                        ProductCard(
                            product = pair[1],
                            shop = shopMap[pair[1].shopId],
                            onCardClick = { onProductClick(pair[1]) },
                            onAddToCart = { viewModel.addToCart(pair[1], 1) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Active Shops Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "বদলগাছীর শীর্ষ উদ্যোক্তা ও দোকানসমূহ (${activeShops.size}টি)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Stone900
                )
            }
        }

        items(activeShops) { shop ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                ShopCard(
                    shop = shop,
                    onShopClick = { onShopClick(shop) }
                )
            }
        }
    }
}
