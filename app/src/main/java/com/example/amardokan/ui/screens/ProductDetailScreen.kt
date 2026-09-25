package com.example.amardokan.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.amardokan.model.Product
import com.example.amardokan.model.Review
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.theme.*
import com.example.amardokan.ui.viewmodel.AppScreen
import com.example.amardokan.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: StoreViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allShops by viewModel.allShops.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()

    val shop = remember(allShops, product.shopId) {
        allShops.find { it.shopId == product.shopId }
    }

    val productReviews = remember(allReviews, product.productId) {
        allReviews.filter { it.productId == product.productId || it.shopId == product.shopId }
    }

    var quantity by remember { mutableIntStateOf(1) }
    var showReviewDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        product.productName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "পেছনে যান")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Stone900
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = White,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Stone100, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp),
                            enabled = quantity > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "কমান", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            modifier = Modifier.size(32.dp),
                            enabled = quantity < product.stock
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "বাড়ান", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Add to Cart
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(product, quantity)
                        },
                        enabled = product.stock > 0,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("detail_add_cart_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald700)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কার্টে যোগ করুন", style = MaterialTheme.typography.labelMedium)
                    }

                    // Buy Now
                    Button(
                        onClick = {
                            viewModel.addToCart(product, quantity)
                            viewModel.navigateTo(AppScreen.CHECKOUT)
                        },
                        enabled = product.stock > 0,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("detail_buy_now_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald700,
                            contentColor = White
                        )
                    ) {
                        Text("এখনই কিনুন", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Stone50),
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. High-Res Image Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Stone100)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.productName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Surface(
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        color = Emerald900.copy(alpha = 0.85f),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // 2. Title, Price, Unit & Stock Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = product.productName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Stone900
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val displayPrice = product.discountPrice ?: product.price
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "৳${displayPrice.toInt()}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Emerald700
                                )
                                if (product.discountPrice != null && product.discountPrice < product.price) {
                                    Text(
                                        text = "৳${product.price.toInt()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Stone500,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Red50
                                    ) {
                                        Text(
                                            text = "ছাড়!",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Red600,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "পরিমাণ: ${product.unit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Stone700,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Divider(color = Stone100)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (product.stock > 0) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (product.stock > 0) Emerald700 else Red600,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (product.stock > 0) "মজুদ আছে (${product.stock} টি)" else "স্টক শেষ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (product.stock > 0) Emerald800 else Red600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = "ডেলিভারি: ২৪-৪৮ ঘণ্টা",
                                style = MaterialTheme.typography.bodySmall,
                                color = Stone500
                            )
                        }
                    }
                }
            }

            // 3. Shop & Seller Details Card
            if (shop != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "🏪 বিক্রেতা ও দোকানের তথ্য",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Emerald900
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = shop.logoUrl,
                                    contentDescription = shop.shopName,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = shop.shopName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Stone900
                                    )
                                    Text(
                                        text = "প্রোপাইটার: ${shop.ownerName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Stone500
                                    )
                                    Text(
                                        text = "📍 ইউনিয়ন: ${shop.union}, বদলগাছী, নওগাঁ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Emerald700,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${shop.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .background(Emerald50, CircleShape)
                                        .size(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "কল করুন",
                                        tint = Emerald700
                                    )
                                }
                            }

                            if (shop.address.isNotBlank()) {
                                Text(
                                    text = "ঠিকানা: ${shop.address}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Stone500
                                )
                            }
                        }
                    }
                }
            }

            // 4. Description
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📝 পণ্যের বিস্তারিত বিবরণ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Stone700,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // 5. Customer Reviews Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ ক্রেতাদের রিভিউ (${productReviews.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Emerald900
                    )
                    TextButton(onClick = { showReviewDialog = true }) {
                        Text(
                            "মতামত দিন",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald700,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Reviews List
            if (productReviews.isEmpty()) {
                item {
                    Text(
                        text = "এই পণ্যে এখনও কোনো রিভিউ দেওয়া হয়নি। প্রথম মতামতটি দিন!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                }
            } else {
                items(productReviews) { review ->
                    ReviewItemCard(review = review)
                }
            }
        }
    }

    // Add Review Dialog
    if (showReviewDialog) {
        AddReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { name, rating, comment ->
                viewModel.addReview(
                    shopId = product.shopId,
                    productId = product.productId,
                    customerName = name,
                    rating = rating,
                    comment = comment
                )
                showReviewDialog = false
            }
        )
    }
}

@Composable
fun ReviewItemCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.customerName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Stone900
                )
                Row {
                    repeat(review.rating) {
                        Text("⭐", fontSize = 12.sp)
                    }
                }
            }
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall,
                color = Stone700
            )
        }
    }
}

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("আপনার মতামত ও রেটিং দিন", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("আপনার নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("রেটিং নির্বাচন করুন:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Text(
                                if (star <= rating) "⭐" else "☆",
                                fontSize = 22.sp
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("আপনার মন্তব্য") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && comment.isNotBlank()) {
                        onSubmit(name, rating, comment)
                    }
                },
                enabled = name.isNotBlank() && comment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
            ) {
                Text("জমা দিন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
