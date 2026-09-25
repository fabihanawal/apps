package com.example.amardokan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.amardokan.model.Product
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.theme.*
import com.example.amardokan.ui.viewmodel.AppScreen

@Composable
fun TopAnnouncementBar(
    announcement: String,
    helplinePhone: String
) {
    Surface(
        color = Emerald900,
        contentColor = White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = Amber500,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = announcement,
                    style = MaterialTheme.typography.bodySmall,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Helpline",
                    tint = Amber500,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = helplinePhone,
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber500,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigation(
    currentScreen: AppScreen,
    cartCount: Int,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.STORE,
            onClick = { onNavigate(AppScreen.STORE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.STORE) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                    contentDescription = "কেনাকাটা"
                )
            },
            label = { Text("দোকান", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald100
            ),
            modifier = Modifier.testTag("nav_store")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.CART || currentScreen == AppScreen.CHECKOUT,
            onClick = { onNavigate(AppScreen.CART) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = Red600,
                                contentColor = White
                            ) {
                                Text(cartCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentScreen == AppScreen.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "কার্ট"
                    )
                }
            },
            label = { Text("কার্ট", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald100
            ),
            modifier = Modifier.testTag("nav_cart")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.ORDER_TRACKER,
            onClick = { onNavigate(AppScreen.ORDER_TRACKER) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.ORDER_TRACKER) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                    contentDescription = "অর্ডার ট্র্যাক"
                )
            },
            label = { Text("ট্র্যাকিং", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald100
            ),
            modifier = Modifier.testTag("nav_tracker")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.VENDOR_DASHBOARD,
            onClick = { onNavigate(AppScreen.VENDOR_DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.VENDOR_DASHBOARD) Icons.Filled.Store else Icons.Outlined.Store,
                    contentDescription = "বিক্রেতা"
                )
            },
            label = { Text("বিক্রেতা", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald100
            ),
            modifier = Modifier.testTag("nav_vendor")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.ADMIN,
            onClick = { onNavigate(AppScreen.ADMIN) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.ADMIN) Icons.Filled.Shield else Icons.Outlined.Shield,
                    contentDescription = "এডমিন"
                )
            },
            label = { Text("এডমিন", style = MaterialTheme.typography.labelSmall) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald100
            ),
            modifier = Modifier.testTag("nav_admin")
        )
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    shop: Shop?,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) }
            .testTag("product_card_${product.productId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Stone100)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.productName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Category pill
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                    color = Emerald800.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        maxLines = 1
                    )
                }

                // Discount badge
                if (product.discountPrice != null && product.discountPrice < product.price) {
                    val discountPercent = ((product.price - product.discountPrice) / product.price * 100).toInt()
                    Surface(
                        shape = CircleShape,
                        color = Red600,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "-$discountPercent%",
                            style = MaterialTheme.typography.labelSmall,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Shop Name & Union
                if (shop != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${shop.shopName} • ${shop.union}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Stone500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Product Title
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Stone900,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                // Price and Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val displayPrice = product.discountPrice ?: product.price
                        Text(
                            text = "৳${displayPrice.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Emerald700,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (product.discountPrice != null && product.discountPrice < product.price) {
                            Text(
                                text = "৳${product.price.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone500,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    Text(
                        text = product.unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = Stone500
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stock & Quick Add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.stock > 0) {
                        Text(
                            text = "স্টক: ${product.stock}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (product.stock < 10) Amber600 else Emerald700,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "স্টক আউট",
                            style = MaterialTheme.typography.labelSmall,
                            color = Red600,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { onAddToCart(product) },
                        enabled = product.stock > 0,
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = if (product.stock > 0) Emerald700 else Stone300,
                                shape = CircleShape
                            )
                            .testTag("add_cart_btn_${product.productId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "কার্টে যোগ করুন",
                            tint = White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
