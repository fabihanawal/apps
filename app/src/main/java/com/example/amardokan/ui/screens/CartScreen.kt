package com.example.amardokan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.amardokan.model.CartItem
import com.example.amardokan.ui.theme.*
import com.example.amardokan.ui.viewmodel.AppScreen
import com.example.amardokan.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: StoreViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cart.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val settings by viewModel.platformSettings.collectAsState()

    val deliveryCharge = if (subtotal >= settings.minFreeDeliveryAmount || subtotal == 0.0) 0.0 else settings.deliveryCharge
    val total = subtotal + deliveryCharge

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "শপিং কার্ট (${cartItems.sumOf { it.quantity }})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("সব মুছুন", color = Red600, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    color = White,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("সাবটোটাল:", style = MaterialTheme.typography.bodyMedium, color = Stone700)
                            Text("৳${subtotal.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ডেলিভারি চার্জ (বদলগাছী):", style = MaterialTheme.typography.bodyMedium, color = Stone700)
                            if (deliveryCharge == 0.0) {
                                Text("ফ্রি ডেলিভারি!", style = MaterialTheme.typography.bodyMedium, color = Emerald700, fontWeight = FontWeight.Bold)
                            } else {
                                Text("৳${deliveryCharge.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(color = Stone100)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("সর্বমোট প্রদেয়:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Stone900)
                            Text("৳${total.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Emerald700)
                        }

                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("checkout_proceed_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Text(
                                "অর্ডারের জন্য এগিয়ে যান",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Stone50),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        tint = Stone300,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "আপনার কার্ট একদম খালি!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                    Text(
                        "পাহাড়পুরের মাটির শিল্প, নওগাঁর ক্ষীরমোহন মিষ্টি কিংবা খাঁটি সরিষার তেল অর্ডার করতে দোকান ঘুরে দেখুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNavigateToStore,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("empty_cart_shop_btn")
                    ) {
                        Text("কেনাকাটা শুরু করুন")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Stone50),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Free delivery hint
                item {
                    if (subtotal < settings.minFreeDeliveryAmount) {
                        val needed = (settings.minFreeDeliveryAmount - subtotal).toInt()
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Amber100
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Amber600, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "আর মাত্র ৳$needed টাকার পণ্য কিনলেই ফ্রি ডেলিভারি!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Stone900,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                items(cartItems, key = { it.productId }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrement = { viewModel.updateCartQuantity(item.productId, item.quantity + 1) },
                        onDecrement = { viewModel.updateCartQuantity(item.productId, item.quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.productId) }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_${item.productId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.productName,
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Stone900,
                    maxLines = 2
                )
                Text(
                    text = "৳${item.price.toInt()} / একক",
                    style = MaterialTheme.typography.bodySmall,
                    color = Emerald700,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Stone100, RoundedCornerShape(8.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(onClick = onDecrement, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = "কমান", modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        IconButton(onClick = onIncrement, modifier = Modifier.size(26.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "বাড়ান", modifier = Modifier.size(14.dp))
                        }
                    }

                    Text(
                        text = "মোট: ৳${(item.price * item.quantity).toInt()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Stone900
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন", tint = Red600)
            }
        }
    }
}
