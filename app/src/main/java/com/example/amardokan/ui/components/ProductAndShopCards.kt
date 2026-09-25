package com.example.amardokan.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.amardokan.model.Constants
import com.example.amardokan.model.Product
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.theme.*

@Composable
fun HeroBannerView(
    headline: String,
    subheadline: String,
    totalShops: Int,
    totalProducts: Int,
    selectedUnion: String,
    onUnionSelected: (String) -> Unit,
    onOpenRegisterShop: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Emerald800, Emerald700)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Amber500.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🇧🇩 বদলগাছী, নওগাঁর স্থানীয় অনলাইন বাজার",
                            color = Amber500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = headline,
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subheadline,
                    color = Emerald100,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats & Register Shop Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Text(
                                text = "$totalShops+",
                                color = Amber500,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "নিবন্ধিত দোকান",
                                color = White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }

                        Column {
                            Text(
                                text = "$totalProducts+",
                                color = Amber500,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "খাঁটি পণ্য",
                                color = White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = onOpenRegisterShop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Amber500,
                            contentColor = Stone900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddBusiness,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "দোকান খুলুন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                // Quick Union Selector Chips
                Text(
                    text = "বদলগাছীর ইউনিয়ন সমূহ:",
                    color = White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val allSelected = selectedUnion.isEmpty()
                    Surface(
                        color = if (allSelected) Amber500 else White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { onUnionSelected("") }
                    ) {
                        Text(
                            text = "সব ইউনিয়ন",
                            color = if (allSelected) Stone900 else White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Constants.BADALGACHHI_UNIONS.forEach { union ->
                        val isSel = selectedUnion == union
                        Surface(
                            color = if (isSel) Amber500 else White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable { onUnionSelected(union) }
                        ) {
                            Text(
                                text = union,
                                color = if (isSel) Stone900 else White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    shop: Shop?,
    onCardClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Product Image with Discount & Stock Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Stone100)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount Badge
                if (product.discountPrice != null && product.discountPrice < product.price) {
                    val discountPercent = Math.round(((product.price - product.discountPrice) / product.price) * 100)
                    Surface(
                        color = Amber500,
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "ছাড় $discountPercent%",
                            color = Stone900,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Stock Badge
                Surface(
                    color = if (product.stock > 0) Emerald800.copy(alpha = 0.85f) else Red600.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (product.stock > 0) "স্টক: ${product.stock} ${product.unit}" else "স্টক আউট",
                        color = White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Info Section
            Column(modifier = Modifier.padding(10.dp)) {
                // Shop and Union
                if (shop != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${shop.union} • ${shop.shopName}",
                            fontSize = 10.sp,
                            color = Stone600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Product Name
                Text(
                    text = product.productName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Stone900,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price Section
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "৳${product.effectivePrice.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald700
                            )
                            if (product.discountPrice != null && product.discountPrice < product.price) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "৳${product.price.toInt()}",
                                    fontSize = 11.sp,
                                    color = Stone400,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    // Add to Bag Button
                    FilledIconButton(
                        onClick = onAddToCart,
                        enabled = product.stock > 0,
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Emerald700,
                            contentColor = White,
                            disabledContainerColor = Stone200
                        ),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "ব্যাগে যোগ করুন",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShopCard(
    shop: Shop,
    onShopClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShopClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shop Logo
            AsyncImage(
                model = shop.logoUrl.ifBlank { "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=500" },
                contentDescription = shop.shopName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Stone100)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = shop.shopName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Stone900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "প্রোপাইটার: ${shop.ownerName}",
                    fontSize = 11.sp,
                    color = Stone600
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${shop.union}, বদলগাছী",
                        fontSize = 11.sp,
                        color = Emerald700,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = "•", color = Stone400, fontSize = 10.sp)
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${shop.rating} (${shop.totalReviews})",
                        fontSize = 11.sp,
                        color = Stone700,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Quick Call Button
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}"))
                    context.startActivity(intent)
                }
            ) {
                Surface(
                    color = Emerald50,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "দোকানে কল করুন",
                            tint = Emerald700,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
