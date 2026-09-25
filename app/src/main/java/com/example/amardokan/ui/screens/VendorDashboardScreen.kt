package com.example.amardokan.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.amardokan.model.MarketingPost
import com.example.amardokan.model.Order
import com.example.amardokan.model.Product
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.components.ProductFormDialog
import com.example.amardokan.ui.theme.*
import com.example.amardokan.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    viewModel: MainViewModel,
    onOpenRegisterShop: () -> Unit
) {
    val context = LocalContext.current
    val shops by viewModel.shops.collectAsState()
    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val marketingPosts by viewModel.marketingPosts.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val activeShopId by viewModel.activeShopId.collectAsState()
    val loggedInVendorEmail by viewModel.loggedInVendorEmail.collectAsState()

    // Find current active shop
    val currentShop = remember(shops, activeShopId) {
        shops.find { it.shopId == activeShopId } ?: shops.firstOrNull() ?: Shop(
            shopId = "default",
            shopName = "আমার দোকান বিক্রেতা",
            ownerName = "উদ্যোক্তা",
            phone = "01755383039",
            email = "vendor@amardokan.bd",
            union = "বদলগাছী সদর",
            category = "মুদি"
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("পণ্যসমূহ", "অর্ডারসমূহ", "AI মার্কেটিং", "পোস্টসমূহ", "রিভিউসমূহ")

    // Modals
    var isProductModalOpen by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var shopSwitcherOpen by remember { mutableStateOf(false) }

    // AI Marketing state
    val shopProducts = remember(products, currentShop) {
        products.filter { it.shopId == currentShop.shopId }
    }
    var selectedMarketingProductId by remember(shopProducts) {
        mutableStateOf(shopProducts.firstOrNull()?.productId ?: "")
    }
    var marketingTone by remember { mutableStateOf("আকর্ষণীয় ও ধামাকা অফার") }
    var customInstruction by remember { mutableStateOf("") }
    var generatedCopy by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val shopOrders = remember(orders, currentShop) {
        orders.filter { it.shopId == currentShop.shopId }
    }
    val shopPosts = remember(marketingPosts, currentShop) {
        marketingPosts.filter { it.shopId == currentShop.shopId }
    }
    val shopReviews = remember(reviews, currentShop) {
        reviews.filter { it.shopId == currentShop.shopId }
    }

    // Financials
    val grossSales = shopOrders.filter { it.orderStatus != "Cancelled" }.sumOf { it.totalAmount }
    val commission = shopOrders.filter { it.orderStatus != "Cancelled" }.sumOf { it.platformCommission }
    val netPayout = grossSales - commission
    val pendingCount = shopOrders.count { it.orderStatus == "Pending" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Stone50),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Shop Selector & Status Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = White,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Store, contentDescription = null, tint = Emerald700)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentShop.shopName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "📍 ${currentShop.union}, বদলগাছী • ${currentShop.ownerName}",
                                    fontSize = 11.sp,
                                    color = Emerald100
                                )
                            }
                        }

                        // Switch Shop or Login Button
                        Box {
                            TextButton(
                                onClick = { shopSwitcherOpen = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = Amber500)
                            ) {
                                Text("দোকান পরিবর্তন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }

                            DropdownMenu(
                                expanded = shopSwitcherOpen,
                                onDismissRequest = { shopSwitcherOpen = false }
                            ) {
                                Text(
                                    text = "দোকান নির্বাচন করুন:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                                shops.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.shopName} (${s.union})") },
                                        onClick = {
                                            viewModel.setActiveShopId(s.shopId)
                                            shopSwitcherOpen = false
                                        },
                                        trailingIcon = {
                                            if (s.shopId == currentShop.shopId) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Emerald700)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Badge & Auth Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = when (currentShop.status) {
                                "Active" -> Emerald500.copy(alpha = 0.25f)
                                "Pending" -> Amber500.copy(alpha = 0.25f)
                                else -> Red600.copy(alpha = 0.25f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "স্ট্যাটাস: ${currentShop.status} ${if (currentShop.status == "Active") "✓" else ""}",
                                color = when (currentShop.status) {
                                    "Active" -> White
                                    "Pending" -> Amber500
                                    else -> Red50
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (loggedInVendorEmail != null) {
                            TextButton(
                                onClick = { viewModel.logoutVendor() },
                                colors = ButtonDefaults.textButtonColors(contentColor = White.copy(alpha = 0.8f))
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("লগ আউট", fontSize = 11.sp)
                            }
                        } else {
                            TextButton(
                                onClick = { viewModel.setVendorLoginOpen(true) },
                                colors = ButtonDefaults.textButtonColors(contentColor = Amber500)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ইমেইল লগইন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Financial Overview Cards (Gross, Commission, Net Payout, Pending)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Net Payout
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("নিট আয় (পাওনা)", fontSize = 10.sp, color = Stone600)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "৳${netPayout.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Emerald700
                        )
                    }
                }

                // Gross Sales
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("মোট বিক্রি", fontSize = 10.sp, color = Stone600)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "৳${grossSales.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone800
                        )
                    }
                }

                // Platform Commission (5%)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("কমিশন (৫%)", fontSize = 10.sp, color = Stone600)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "৳${commission.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber600
                        )
                    }
                }

                // Pending Orders
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("অপেক্ষমাণ", fontSize = 10.sp, color = Stone600)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "$pendingCount টি",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingCount > 0) Amber600 else Stone800
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Tab Navigation
        item {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = White,
                contentColor = Emerald700,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // TAB 1: PRODUCTS
        if (selectedTab == 0) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "মোট পণ্য (${shopProducts.size}টি)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            editingProduct = null
                            isProductModalOpen = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন পণ্য", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (shopProducts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = Stone400, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("এখনও কোনো পণ্য যুক্ত করেননি", fontWeight = FontWeight.Bold, color = Stone700)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("আপনার প্রথম পণ্যটি যোগ করে বিক্রি শুরু করুন।", fontSize = 12.sp, color = Stone500)
                    }
                }
            } else {
                items(shopProducts) { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = prod.imageUrl,
                                contentDescription = prod.productName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Stone100)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                Text("৳${prod.effectivePrice.toInt()} (${prod.unit}) • স্টক: ${prod.stock}", fontSize = 11.sp, color = Emerald700)
                                Text("ক্যাটাগরি: ${prod.category}", fontSize = 10.sp, color = Stone500)
                            }
                            IconButton(onClick = {
                                editingProduct = prod
                                isProductModalOpen = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "সম্পাদনা", tint = Stone700, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { viewModel.deleteProduct(prod.productId) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন", tint = Red600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: ORDERS
        if (selectedTab == 1) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("প্রাপ্ত অর্ডারসমূহ (${shopOrders.size}টি)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (shopOrders.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Stone400, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("এখনও কোনো অর্ডার পাওয়া যায়নি", fontWeight = FontWeight.Bold, color = Stone700)
                    }
                }
            } else {
                items(shopOrders) { ord ->
                    var statusMenuOpen by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#${ord.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                                // Status Dropdown
                                Box {
                                    Surface(
                                        color = when (ord.orderStatus) {
                                            "Delivered" -> Emerald100
                                            "Cancelled" -> Red50
                                            "Pending" -> Amber50
                                            else -> Blue50
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.clickable { statusMenuOpen = true }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = ord.orderStatus,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (ord.orderStatus) {
                                                    "Delivered" -> Emerald800
                                                    "Cancelled" -> Red600
                                                    "Pending" -> Amber600
                                                    else -> Blue600
                                                }
                                            )
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = statusMenuOpen,
                                        onDismissRequest = { statusMenuOpen = false }
                                    ) {
                                        listOf("Pending", "Confirmed", "Packed", "Shipped", "Delivered", "Cancelled").forEach { s ->
                                            DropdownMenuItem(
                                                text = { Text(s) },
                                                onClick = {
                                                    viewModel.updateOrderStatus(ord.orderId, s)
                                                    statusMenuOpen = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("গ্রাহক: ${ord.customerName} (${ord.customerPhone})", fontSize = 12.sp)
                            Text("ঠিকানা: ${ord.deliveryAddress}, ইউনিয়ন: ${ord.union}", fontSize = 12.sp, color = Stone600)
                            Text("বিল: ৳${ord.totalAmount.toInt()} (কমিশন: ৳${ord.platformCommission.toInt()})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Emerald700)

                            Spacer(modifier = Modifier.height(6.dp))

                            // Items summary
                            val items = ord.getItems()
                            Text(
                                text = "আইটেম: " + items.joinToString { "${it.productName} (x${it.quantity})" },
                                fontSize = 11.sp,
                                color = Stone700
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Call Customer Action
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${ord.customerPhone}"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("গ্রাহককে কল দিন", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // TAB 3: AI MARKETING
        if (selectedTab == 2) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Amber500)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI সোশ্যাল মিডিয়া পোস্ট জেনারেটর",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Stone900
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ফেসবুক ও হোয়াটসঅ্যাপে প্রচারের জন্য আকর্ষণীয় ক্যাপশন ও অফার পোস্ট তৈরি করুন।",
                            fontSize = 11.sp,
                            color = Stone600
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Select product
                        Text("পণ্য নির্বাচন করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        var prodDropdownOpen by remember { mutableStateOf(false) }
                        val selectedProd = shopProducts.find { it.productId == selectedMarketingProductId } ?: shopProducts.firstOrNull()

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { prodDropdownOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(selectedProd?.productName ?: "কোনো পণ্য পাওয়া যায়নি", fontSize = 13.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }

                            DropdownMenu(
                                expanded = prodDropdownOpen,
                                onDismissRequest = { prodDropdownOpen = false }
                            ) {
                                shopProducts.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.productName) },
                                        onClick = {
                                            selectedMarketingProductId = p.productId
                                            prodDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tone Selector
                        Text("পোস্টের ধরণ / টোন:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        val tones = listOf("আকর্ষণীয় ও ধামাকা অফার", "ঐতিহ্যবাহী ও খাঁটি মান", "দ্রুততম ডেলিভারি", "সীমিত স্টক অফার")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            tones.take(2).forEach { t ->
                                FilterChip(
                                    selected = marketingTone == t,
                                    onClick = { marketingTone = t },
                                    label = { Text(t, fontSize = 10.sp) }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            tones.takeLast(2).forEach { t ->
                                FilterChip(
                                    selected = marketingTone == t,
                                    onClick = { marketingTone = t },
                                    label = { Text(t, fontSize = 10.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = customInstruction,
                            onValueChange = { customInstruction = it },
                            label = { Text("বিশেষ নির্দেশনা (ঐচ্ছিক)") },
                            placeholder = { Text("যেমন: পাহাড়ি মধু ফ্রি, ফ্রি ডেলিভারি ইত্যাদি") },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (selectedProd != null) {
                                    isGenerating = true
                                    viewModel.generateMarketingCopy(
                                        selectedProd,
                                        currentShop,
                                        marketingTone,
                                        customInstruction
                                    ) { copy ->
                                        generatedCopy = copy
                                        isGenerating = false
                                    }
                                }
                            },
                            enabled = selectedProd != null && !isGenerating,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isGenerating) "জেনারেট হচ্ছে..." else "পোস্ট তৈরি করুন", fontWeight = FontWeight.Bold)
                        }

                        // Generated Copy preview
                        if (generatedCopy.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Stone200)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("পোস্টের প্রিভিউ:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Emerald800)
                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                color = Stone100,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = generatedCopy,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = Stone800,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Amar Dokan Post", generatedCopy)
                                        clipboard.setPrimaryClip(clip)
                                        viewModel.showUserMessage("ক্যাপশন কপি করা হয়েছে!")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("কপি করুন", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        if (selectedProd != null) {
                                            viewModel.saveMarketingPost(
                                                currentShop.shopId,
                                                currentShop.shopName,
                                                selectedProd.productName,
                                                generatedCopy,
                                                selectedProd.imageUrl
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("পোস্ট সেভ করুন", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 4: SAVED POSTS
        if (selectedTab == 3) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("সংরক্ষিত পোস্টসমূহ (${shopPosts.size}টি)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (shopPosts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, tint = Stone400, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("এখনও কোনো পোস্ট সেভ করা হয়নি", fontWeight = FontWeight.Bold, color = Stone700)
                    }
                }
            } else {
                items(shopPosts) { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(p.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                IconButton(onClick = { viewModel.deleteMarketingPost(p.postId) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন", tint = Red600, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(p.generatedText, fontSize = 12.sp, color = Stone700, lineHeight = 17.sp)

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Amar Dokan Post", p.generatedText)
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showUserMessage("ক্যাপশন কপি করা হয়েছে!")
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("কপি করুন", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // TAB 5: REVIEWS
        if (selectedTab == 4) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("দোকানের রিভিউসমূহ (${shopReviews.size}টি)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (shopReviews.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.StarOutline, contentDescription = null, tint = Stone400, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("এখনও কোনো রিভিউ যুক্ত হয়নি", fontWeight = FontWeight.Bold, color = Stone700)
                    }
                }
            } else {
                items(shopReviews) { rev ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(rev.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row {
                                    repeat(rev.rating) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Amber500, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rev.comment, fontSize = 12.sp, color = Stone700)
                        }
                    }
                }
            }
        }
    }

    // Product Add/Edit Dialog
    if (isProductModalOpen) {
        ProductFormDialog(
            initialProduct = editingProduct,
            shopId = currentShop.shopId,
            onDismiss = { isProductModalOpen = false },
            onSave = { name, price, discount, stock, desc, img, cat, unit ->
                if (editingProduct == null) {
                    viewModel.addProduct(
                        currentShop.shopId,
                        name,
                        price,
                        discount,
                        stock,
                        desc,
                        img,
                        cat,
                        unit
                    )
                } else {
                    viewModel.updateProduct(
                        editingProduct!!.copy(
                            productName = name,
                            price = price,
                            discountPrice = discount,
                            stock = stock,
                            description = desc,
                            imageUrl = img.ifBlank { editingProduct!!.imageUrl },
                            category = cat,
                            unit = unit
                        )
                    )
                }
            }
        )
    }
}
