package com.example.amardokan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.amardokan.model.BadalgachhiConstants
import com.example.amardokan.model.Product
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.components.ProductGridCard
import com.example.amardokan.ui.theme.*
import com.example.amardokan.ui.viewmodel.AppScreen
import com.example.amardokan.ui.viewmodel.StoreViewModel

@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val filteredShops by viewModel.filteredShops.collectAsState()
    val allShops by viewModel.allShops.collectAsState()
    val settings by viewModel.platformSettings.collectAsState()

    val selectedUnion by viewModel.selectedUnion.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val shopMap = remember(allShops) {
        allShops.associateBy { it.shopId }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_grid"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Search Bar
        item(span = { GridItemSpan(2) }) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                placeholder = {
                    Text(
                        "পণ্য, মিষ্টি, চাল বা দোকান খুঁজুন...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Emerald700
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Emerald700,
                    unfocusedBorderColor = Stone300,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                )
            )
        }

        // 2. Hero Banner
        item(span = { GridItemSpan(2) }) {
            HeroBannerCard(
                headline = settings.heroHeadline,
                subheadline = settings.heroSubheadline,
                totalShops = allShops.count { it.status == "Active" },
                totalProducts = viewModel.allProducts.collectAsState().value.size,
                onRegisterShopClick = { viewModel.navigateTo(AppScreen.SHOP_REGISTRATION) }
            )
        }

        // 3. Badalgachhi 8 Unions Quick Selector
        item(span = { GridItemSpan(2) }) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍 ইউনিয়ন নির্বাচন করুন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                    if (selectedUnion.isNotBlank()) {
                        TextButton(
                            onClick = { viewModel.selectedUnion.value = "" },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "সব ইউনিয়ন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald700,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadalgachhiConstants.UNIONS.forEach { union ->
                        val isSelected = selectedUnion == union
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectedUnion.value = if (isSelected) "" else union
                            },
                            label = {
                                Text(
                                    union,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald700,
                                selectedLabelColor = White,
                                containerColor = White,
                                labelColor = Stone700
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Emerald700 else Stone300
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("union_chip_$union")
                        )
                    }
                }
            }
        }

        // 4. Category Pills Filter
        item(span = { GridItemSpan(2) }) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "🏷️ ক্যাটাগরি",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Emerald900
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadalgachhiConstants.CATEGORIES.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectedCategory.value = cat }
                                .testTag("cat_chip_$cat"),
                            color = if (isSelected) Emerald800 else White,
                            contentColor = if (isSelected) White else Stone700,
                            shape = RoundedCornerShape(20.dp),
                            tonalElevation = 1.dp,
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Active Filters Notice (if any filter is applied)
        if (selectedUnion.isNotBlank() || selectedCategory != "সব ক্যাটাগরি" || searchQuery.isNotBlank()) {
            item(span = { GridItemSpan(2) }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Emerald50,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("ফিল্টার:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Emerald900)
                            if (selectedUnion.isNotBlank()) {
                                Text("📍 $selectedUnion", style = MaterialTheme.typography.labelSmall, color = Emerald800)
                            }
                            if (selectedCategory != "সব ক্যাটাগরি") {
                                Text("🏷️ $selectedCategory", style = MaterialTheme.typography.labelSmall, color = Emerald800)
                            }
                        }

                        TextButton(
                            onClick = {
                                viewModel.selectedUnion.value = ""
                                viewModel.selectedCategory.value = "সব ক্যাটাগরি"
                                viewModel.searchQuery.value = ""
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("রিসেট", style = MaterialTheme.typography.labelSmall, color = Red600, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Featured Badalgachhi Shops section
        if (filteredShops.isNotEmpty() && searchQuery.isBlank() && selectedCategory == "সব ক্যাটাগরি") {
            item(span = { GridItemSpan(2) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏪 স্থানীয় বিশ্বস্ত দোকানসমূহ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )
                        Text(
                            text = "${filteredShops.size} টি দোকান",
                            style = MaterialTheme.typography.labelSmall,
                            color = Stone500
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(filteredShops) { shop ->
                            ShopMiniCard(shop = shop)
                        }
                    }
                }
            }
        }

        // 7. Products Section Header
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📦 জনপ্রিয় পণ্যসমূহ (${filteredProducts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Emerald900
                )
            }
        }

        // 8. Products List
        if (filteredProducts.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Stone300,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "কোনো পণ্য পাওয়া যায়নি",
                            style = MaterialTheme.typography.titleMedium,
                            color = Stone700,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "অন্য কোনো ইউনিয়ন বা ক্যাটাগরি ফিল্টার করে দেখুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500
                        )
                    }
                }
            }
        } else {
            items(filteredProducts, key = { it.productId }) { product ->
                ProductGridCard(
                    product = product,
                    shop = shopMap[product.shopId],
                    onProductClick = onProductClick,
                    onAddToCart = { viewModel.addToCart(it) }
                )
            }
        }

        // 9. Footer Note
        item(span = { GridItemSpan(2) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 10.dp),
                color = Stone100,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "আমার দোকান - বদলগাছী, নওগাঁ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                    Text(
                        text = "Developed by RSTS-BD (01755383039)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500
                    )
                }
            }
        }
    }
}

@Composable
fun HeroBannerCard(
    headline: String,
    subheadline: String,
    totalShops: Int,
    totalProducts: Int,
    onRegisterShopClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Emerald800),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Emerald800, Emerald900)
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Top Tag
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Amber500.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sparkles,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "বদলগাছীর ডিজিটাল হাট",
                            style = MaterialTheme.typography.labelSmall,
                            color = Amber100,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp
                )

                Text(
                    text = subheadline,
                    style = MaterialTheme.typography.bodySmall,
                    color = Emerald100,
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column {
                            Text(
                                text = "$totalShops+",
                                style = MaterialTheme.typography.titleMedium,
                                color = Amber500,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "স্থানীয় দোকান",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald100
                            )
                        }
                        Column {
                            Text(
                                text = "$totalProducts+",
                                style = MaterialTheme.typography.titleMedium,
                                color = Amber500,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "খাঁটি পণ্য",
                                style = MaterialTheme.typography.labelSmall,
                                color = Emerald100
                            )
                        }
                    }

                    Button(
                        onClick = onRegisterShopClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Amber500,
                            contentColor = Stone900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_register_shop_hero")
                    ) {
                        Text(
                            "দোকান নিবন্ধন",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShopMiniCard(shop: Shop) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .testTag("shop_mini_card_${shop.shopId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = shop.logoUrl,
                    contentDescription = shop.shopName,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = shop.shopName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Stone900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "📍 ${shop.union}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald700
                    )
                }
            }

            Text(
                text = shop.description,
                style = MaterialTheme.typography.bodySmall,
                color = Stone500,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Emerald50
                ) {
                    Text(
                        text = "⭐ ${shop.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald800,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = shop.phone,
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone500
                )
            }
        }
    }
}
