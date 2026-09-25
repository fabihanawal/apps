package com.example.amardokan.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.amardokan.model.Order
import com.example.amardokan.model.Product
import com.example.amardokan.model.Review
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderTimelineCard(order: Order) {
    val steps = listOf("Pending", "Confirmed", "Packed", "Shipped", "Delivered")
    val stepLabels = listOf(
        "অর্ডার জমা",
        "গৃহীত",
        "প্যাকিং সম্পন্ন",
        "ডেলিভারির পথে",
        "ডেলিভার্ড"
    )

    val currentStepIndex = steps.indexOf(order.orderStatus)
    val isCancelled = order.orderStatus == "Cancelled"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: ID and Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "অর্ডার #${order.orderId}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                    val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(Date(order.timestamp))
                    Text(text = dateFormatted, fontSize = 11.sp, color = Stone500)
                }

                Surface(
                    color = when (order.orderStatus) {
                        "Delivered" -> Emerald100
                        "Cancelled" -> Red50
                        "Pending" -> Amber50
                        else -> Blue50
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.orderStatus,
                        color = when (order.orderStatus) {
                            "Delivered" -> Emerald800
                            "Cancelled" -> Red600
                            "Pending" -> Amber600
                            else -> Blue600
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual 5-step Timeline
            if (isCancelled) {
                Surface(
                    color = Red50,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, tint = Red600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "এই অর্ডারটি বাতিল করা হয়েছে।",
                            color = Red600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    steps.forEachIndexed { index, step ->
                        val isDone = index <= currentStepIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDone) Emerald700 else Stone200,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isDone) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 11.sp,
                                            color = Stone600,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stepLabels[index],
                                fontSize = 9.sp,
                                fontWeight = if (index == currentStepIndex) FontWeight.Bold else FontWeight.Normal,
                                color = if (index == currentStepIndex) Emerald800 else Stone500,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Stone200)
            Spacer(modifier = Modifier.height(10.dp))

            // Order details
            Text(
                text = "দোকানের নাম: ${order.shopName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Stone800
            )
            Text(
                text = "গ্রাহক: ${order.customerName} (${order.customerPhone})",
                fontSize = 12.sp,
                color = Stone600
            )
            Text(
                text = "ঠিকানা: ${order.deliveryAddress}, ইউনিয়ন: ${order.union}",
                fontSize = 12.sp,
                color = Stone600
            )
            Text(
                text = "পেমেন্ট: ${order.paymentMethod}",
                fontSize = 12.sp,
                color = Stone600
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Items breakdown
            val items = order.getItems()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Stone50, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                items.forEach { it ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• ${it.productName} × ${it.quantity}",
                            fontSize = 12.sp,
                            color = Stone700,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "৳${(it.price * it.quantity).toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = Stone200)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("মোট প্রদেয় বিল:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("৳${order.totalAmount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Emerald700)
                }
            }
        }
    }
}

@Composable
fun ProductDetailDialog(
    product: Product,
    shop: Shop?,
    reviews: List<Review>,
    onAddToCart: (Product, Int) -> Unit,
    onAddReview: (rating: Int, comment: String, customerName: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedQuantity by remember { mutableStateOf(1) }

    // Review form state
    var reviewRating by remember { mutableStateOf(5) }
    var reviewerName by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var reviewSubmitted by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "পণ্যের বিবরণ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }

                HorizontalDivider(color = Stone200)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    // Large Image
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Stone100)
                        ) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.productName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (product.discountPrice != null && product.discountPrice < product.price) {
                                val discountPercent = Math.round(((product.price - product.discountPrice) / product.price) * 100)
                                Surface(
                                    color = Amber500,
                                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "ছাড় $discountPercent%",
                                        color = Stone900,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Product title and price
                    item {
                        Text(
                            text = product.productName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone900
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "৳${product.effectivePrice.toInt()}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald700
                            )
                            if (product.discountPrice != null && product.discountPrice < product.price) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "৳${product.price.toInt()}",
                                    fontSize = 14.sp,
                                    color = Stone400,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Surface(
                                color = Stone100,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "একক: ${product.unit}",
                                    fontSize = 11.sp,
                                    color = Stone700,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = if (product.stock > 0) Emerald50 else Red50,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (product.stock > 0) "✅ স্টকে আছে: ${product.stock} ${product.unit}" else "❌ স্টক খালি",
                                color = if (product.stock > 0) Emerald800 else Red600,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Shop Card info
                    item {
                        if (shop != null) {
                            Surface(
                                color = Stone100,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Emerald700)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(shop.shopName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("ইউনিয়ন: ${shop.union}, বদলগাছী", fontSize = 11.sp, color = Stone600)
                                    }
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}"))
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = "দোকানে কল করুন", tint = Emerald700)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Description
                    item {
                        Text(
                            text = "পণ্যের বিবরণ:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone800
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = product.description.ifBlank { "বদলগাছীর স্থানীয় উদ্যোক্তা দ্বারা সরবরাহকৃত খাঁটি ও নির্ভরযোগ্য পণ্য।" },
                            fontSize = 13.sp,
                            color = Stone700,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Stone200)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Reviews section
                    item {
                        Text(
                            text = "গ্রাহক মতামত ও রিভিউ (${reviews.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone800
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (reviews.isEmpty()) {
                            Text(
                                text = "এখনও কোনো রিভিউ যুক্ত হয়নি। আপনার মতামত জানান!",
                                fontSize = 12.sp,
                                color = Stone500
                            )
                        } else {
                            reviews.forEach { rev ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Stone50),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(rev.customerName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Row {
                                                repeat(rev.rating) {
                                                    Icon(
                                                        Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = Amber500,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(rev.comment, fontSize = 12.sp, color = Stone700)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Write review section
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Emerald50),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "✍️ রিভিউ লিখুন",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald800
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Rating Star Picker
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("রেটিং:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    for (i in 1..5) {
                                        IconButton(
                                            onClick = { reviewRating = i },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (i <= reviewRating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                                contentDescription = "$i Star",
                                                tint = Amber500,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = reviewerName,
                                    onValueChange = { reviewerName = it },
                                    label = { Text("আপনার নাম") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = reviewComment,
                                    onValueChange = { reviewComment = it },
                                    label = { Text("আপনার মূল্যবান মন্তব্য") },
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (reviewerName.isNotBlank() && reviewComment.isNotBlank()) {
                                            onAddReview(reviewRating, reviewComment.trim(), reviewerName.trim())
                                            reviewComment = ""
                                            reviewerName = ""
                                            reviewSubmitted = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("জমা দিন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Bottom Add to Bag and Quantity Controller
                Surface(
                    color = Stone100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Quantity selector
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (selectedQuantity > 1) selectedQuantity-- },
                                shape = CircleShape,
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = White),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = selectedQuantity.toString(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )
                            FilledIconButton(
                                onClick = { if (selectedQuantity < product.stock) selectedQuantity++ },
                                enabled = selectedQuantity < product.stock,
                                shape = CircleShape,
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = White),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Add to Bag CTA
                        Button(
                            onClick = {
                                onAddToCart(product, selectedQuantity)
                                onDismiss()
                            },
                            enabled = product.stock > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ব্যাগে নিন (৳${(product.effectivePrice * selectedQuantity).toInt()})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
