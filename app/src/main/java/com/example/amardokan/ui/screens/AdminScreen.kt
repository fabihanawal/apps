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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amardokan.model.Order
import com.example.amardokan.model.PlatformSettings
import com.example.amardokan.model.Product
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.theme.*
import com.example.amardokan.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: MainViewModel
) {
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val shops by viewModel.shops.collectAsState()
    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val platformSettings by viewModel.platformSettings.collectAsState()

    var adminEmail by remember { mutableStateOf("rstsbd@gmail.com") }
    var adminPassword by remember { mutableStateOf("rstsbd1234") }
    var adminTab by remember { mutableStateOf(0) }
    val adminTabs = listOf("দোকান অনুমোদন", "পণ্য তালিকা", "অর্ডারসমূহ", "প্ল্যাটফর্ম CMS", "ডেটাবেজ")

    // CMS Settings form state
    var editAnnouncement by remember(platformSettings) { mutableStateOf(platformSettings.announcementText) }
    var editHeadline by remember(platformSettings) { mutableStateOf(platformSettings.heroHeadline) }
    var editSubheadline by remember(platformSettings) { mutableStateOf(platformSettings.heroSubheadline) }
    var editPhone by remember(platformSettings) { mutableStateOf(platformSettings.helplinePhone) }
    var editDeliveryFee by remember(platformSettings) { mutableStateOf(platformSettings.deliveryCharge.toInt().toString()) }
    var editFreeMin by remember(platformSettings) { mutableStateOf(platformSettings.minFreeDeliveryAmount.toInt().toString()) }

    if (!isAdminLoggedIn) {
        // Admin Login Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Stone50)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Emerald700, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "সুপার অ্যাডমিন পোর্টাল",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone900
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "প্ল্যাটফর্ম নিয়ন্ত্রণ, দোকান অনুমোদন এবং সেটিংস পরিবর্তনের জন্য প্রবেশ করুন।",
                        fontSize = 12.sp,
                        color = Stone600
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Amber50,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ডিফল্ট লগইন: rstsbd@gmail.com / rstsbd1234",
                            color = Amber600,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = adminEmail,
                        onValueChange = { adminEmail = it },
                        label = { Text("অ্যাডমিন ইমেইল") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("পাসওয়ার্ড") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            viewModel.loginAdmin(adminEmail.trim(), adminPassword)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("লগইন করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        // Authenticated Admin Dashboard
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Stone50),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Stone900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "সুপার অ্যাডমিন কনসোল",
                                    color = White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "RSTS-BD • রফিকুল ইসলাম (01755383039)",
                                    color = Stone400,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(onClick = { viewModel.logoutAdmin() }) {
                                Icon(Icons.Default.Logout, contentDescription = "লগআউট", tint = Red600)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Stone800,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("মোট দোকান", fontSize = 10.sp, color = Stone400)
                                    Text("${shops.size}টি", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Amber500)
                                }
                            }
                            Surface(
                                color = Stone800,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("মোট পণ্য", fontSize = 10.sp, color = Stone400)
                                    Text("${products.size}টি", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                                }
                            }
                            Surface(
                                color = Stone800,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("মোট অর্ডার", fontSize = 10.sp, color = Stone400)
                                    Text("${orders.size}টি", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                                }
                            }
                        }
                    }
                }
            }

            // Tabs
            item {
                PrimaryScrollableTabRow(
                    selectedTabIndex = adminTab,
                    containerColor = White,
                    contentColor = Emerald700,
                    edgePadding = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    adminTabs.forEachIndexed { idx, title ->
                        Tab(
                            selected = adminTab == idx,
                            onClick = { adminTab = idx },
                            text = { Text(title, fontSize = 12.sp, fontWeight = if (adminTab == idx) FontWeight.Bold else FontWeight.Medium) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // TAB 1: SHOPS APPROVAL & MANAGEMENT
            if (adminTab == 0) {
                val pendingShops = shops.filter { it.status == "Pending" }
                val activeShops = shops.filter { it.status != "Pending" }

                item {
                    Text(
                        text = "আবেদনকৃত নতুন দোকান (${pendingShops.size}টি অপেক্ষমাণ):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone800,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                if (pendingShops.isEmpty()) {
                    item {
                        Surface(
                            color = Stone100,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "কোনো নতুন অপেক্ষমাণ আবেদন নেই।",
                                fontSize = 12.sp,
                                color = Stone600,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    items(pendingShops) { s ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Amber50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(s.shopName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("প্রোপাইটার: ${s.ownerName} • ফোন: ${s.phone}", fontSize = 12.sp)
                                Text("ইউনিয়ন: ${s.union} • ঠিকানা: ${s.address}", fontSize = 11.sp, color = Stone600)
                                Text("ক্যাটাগরি: ${s.category}", fontSize = 11.sp, color = Stone600)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.rejectShop(s.shopId) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("বাতিল (Reject)", color = Red600, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.approveShop(s.shopId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("অনুমোদন (Approve)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "অনুমোদিত ও সক্রিয় দোকানসমূহ (${activeShops.size}টি):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone800,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(activeShops) { s ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(s.shopName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${s.union} • ${s.phone}", fontSize = 11.sp, color = Stone600)
                                Text("স্ট্যাটাস: ${s.status}", fontSize = 10.sp, color = if (s.status == "Active") Emerald700 else Red600)
                            }
                            IconButton(onClick = { viewModel.deleteShop(s.shopId) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন", tint = Red600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // TAB 2: PRODUCTS
            if (adminTab == 1) {
                item {
                    Text(
                        text = "প্ল্যাটফর্মের সকল পণ্য (${products.size}টি):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(products) { p ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("৳${p.effectivePrice.toInt()} • স্টক: ${p.stock} • ক্যাটাগরি: ${p.category}", fontSize = 11.sp, color = Stone600)
                            }
                            IconButton(onClick = { viewModel.deleteProduct(p.productId) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন", tint = Red600, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // TAB 3: ORDERS
            if (adminTab == 2) {
                item {
                    Text(
                        text = "সকল অর্ডার (${orders.size}টি):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                items(orders) { ord ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("#${ord.orderId} (${ord.orderStatus})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("৳${ord.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = Emerald700)
                            }
                            Text("গ্রাহক: ${ord.customerName} (${ord.customerPhone})", fontSize = 11.sp)
                            Text("দোকান: ${ord.shopName} • ইউনিয়ন: ${ord.union}", fontSize = 11.sp, color = Stone600)

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { viewModel.deleteOrder(ord.orderId) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "অর্ডার মুছুন", tint = Red600, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // TAB 4: PLATFORM CMS
            if (adminTab == 3) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("প্ল্যাটফর্ম CMS ও ব্যানার সেটিংস", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = editAnnouncement,
                                onValueChange = { editAnnouncement = it },
                                label = { Text("টপ নোটিশ / ঘোষণা বার্তা") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editHeadline,
                                onValueChange = { editHeadline = it },
                                label = { Text("হিরো ব্যানার হেডলাইন") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editSubheadline,
                                onValueChange = { editSubheadline = it },
                                label = { Text("হিরো ব্যানার সাব-হেডলাইন") },
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("হেল্পলাইন মোবাইল নম্বর") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = editDeliveryFee,
                                    onValueChange = { editDeliveryFee = it },
                                    label = { Text("ডেলিভারি চার্জ (৳)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                OutlinedTextField(
                                    value = editFreeMin,
                                    onValueChange = { editFreeMin = it },
                                    label = { Text("ফ্রি ডেলিভারি ন্যূনতম (৳)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val fee = editDeliveryFee.toDoubleOrNull() ?: 30.0
                                    val minFree = editFreeMin.toDoubleOrNull() ?: 500.0
                                    viewModel.updatePlatformSettings(
                                        platformSettings.copy(
                                            announcementText = editAnnouncement.trim(),
                                            heroHeadline = editHeadline.trim(),
                                            heroSubheadline = editSubheadline.trim(),
                                            helplinePhone = editPhone.trim(),
                                            deliveryCharge = fee,
                                            minFreeDeliveryAmount = minFree
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("সেটিংস সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // TAB 5: DATABASE & RESTORE
            if (adminTab == 4) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("সিস্টেম ডেটাবেজ ও রিস্টোর", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "লোকাল SQLite / Room ডেটাবেজের তথ্যসমূহ সম্পূর্ণ ফ্রেশ প্রাথমিক অবস্থায় ফিরিয়ে নিতে পারবেন।",
                                fontSize = 12.sp,
                                color = Stone600
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                color = Stone100,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("• মোট দোকান: ${shops.size}টি", fontSize = 12.sp)
                                    Text("• মোট পণ্য: ${products.size}টি", fontSize = 12.sp)
                                    Text("• মোট অর্ডার: ${orders.size}টি", fontSize = 12.sp)
                                    Text("• ডেটাবেজ ইঞ্জিন: Android Room 2.6.1 (KSP)", fontSize = 12.sp, color = Emerald700)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.resetToDefaults() },
                                colors = ButtonDefaults.buttonColors(containerColor = Amber600),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("প্রাথমিক ডেটায় রিস্টোর করুন", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
