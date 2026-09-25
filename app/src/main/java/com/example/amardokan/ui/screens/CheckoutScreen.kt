package com.example.amardokan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amardokan.model.BadalgachhiConstants
import com.example.amardokan.model.Order
import com.example.amardokan.ui.theme.*
import com.example.amardokan.ui.viewmodel.AppScreen
import com.example.amardokan.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cart.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val settings by viewModel.platformSettings.collectAsState()

    val deliveryCharge = if (subtotal >= settings.minFreeDeliveryAmount || subtotal == 0.0) 0.0 else settings.deliveryCharge
    val total = subtotal + deliveryCharge

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var selectedUnion by remember { mutableStateOf(BadalgachhiConstants.UNIONS[0]) }
    var paymentMethod by remember { mutableStateOf("ক্যাশ অন ডেলিভারি (COD)") }
    var notes by remember { mutableStateOf("") }

    var unionDropdownExpanded by remember { mutableStateOf(false) }
    var placedOrders by remember { mutableStateOf<List<Order>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("চেকআউট ও ডেলিভারি", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "পেছনে যান")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Stone50),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Customer Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "👤 ক্রেতার তথ্য",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("আপনার সম্পূর্ণ নাম *") },
                            placeholder = { Text("যেমন: মো: তানভীর আহমেদ") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_input")
                        )

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("মোবাইল নম্বর (সচল) *") },
                            placeholder = { Text("01XXXXXXXXX") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Emerald700)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_input")
                        )
                    }
                }
            }

            // Delivery Address in Badalgachhi Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📍 ডেলিভারির ঠিকানা (বদলগাছী উপজেলা)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )

                        // Union Dropdown
                        ExposedDropdownMenuBox(
                            expanded = unionDropdownExpanded,
                            onExpandedChange = { unionDropdownExpanded = !unionDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedUnion,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ইউনিয়ন নির্বাচন করুন *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unionDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("checkout_union_selector")
                            )

                            ExposedDropdownMenu(
                                expanded = unionDropdownExpanded,
                                onDismissRequest = { unionDropdownExpanded = false }
                            ) {
                                BadalgachhiConstants.UNIONS.forEach { union ->
                                    DropdownMenuItem(
                                        text = { Text(union) },
                                        onClick = {
                                            selectedUnion = union
                                            unionDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("বিস্তারিত ঠিকানা (গ্রাম, পাড়া, বাড়ি/দোকানের নাম) *") },
                            placeholder = { Text("যেমন: আধাইপুর পশ্চিমপাড়া, প্রাইমারি স্কুলের পাশে") },
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_address_input")
                        )

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("বিশেষ নির্দেশনা (ঐচ্ছিক)") },
                            placeholder = { Text("যেমন: বিকেল ৫টার মধ্যে ডেলিভারি দেওয়ার অনুরোধ") },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Payment Method Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💳 মূল্য পরিশোধের মাধ্যম",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )

                        val paymentMethods = listOf(
                            "ক্যাশ অন ডেলিভারি (COD)",
                            "বিকাশ (bKash)",
                            "নগদ (Nagad)"
                        )

                        paymentMethods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = method }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == method,
                                    onClick = { paymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = Emerald700)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = method,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (paymentMethod == method) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (method.contains("COD")) {
                                        Text("পণ্য হাতে পেয়ে মূল্য পরিশোধ করুন", style = MaterialTheme.typography.bodySmall, color = Stone500)
                                    } else {
                                        Text("ডেলিভারিম্যানকে ডিজিটাল পেমেন্ট করুন", style = MaterialTheme.typography.bodySmall, color = Stone500)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Order Summary & Submit Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "📋 অর্ডারের সংক্ষিপ্ত বিবরণ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )

                        cartItems.forEach { itm ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${itm.productName} (x${itm.quantity})",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                Text(
                                    text = "৳${(itm.price * itm.quantity).toInt()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Divider(color = Stone100)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("সাবটোটাল:", style = MaterialTheme.typography.bodyMedium)
                            Text("৳${subtotal.toInt()}", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ডেলিভারি চার্জ:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = if (deliveryCharge == 0.0) "ফ্রি!" else "৳${deliveryCharge.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (deliveryCharge == 0.0) Emerald700 else Stone900,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Divider(color = Stone100)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("সর্বমোট প্রদেয়:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("৳${total.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Emerald700)
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Red600,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                if (customerName.isBlank()) {
                                    errorMessage = "অনুগ্রহ করে আপনার নাম লিখুন।"
                                    return@Button
                                }
                                if (customerPhone.isBlank() || customerPhone.length < 11) {
                                    errorMessage = "অনুগ্রহ করে সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন।"
                                    return@Button
                                }
                                if (deliveryAddress.isBlank()) {
                                    errorMessage = "অনুগ্রহ করে বিস্তারিত ডেলিভারির ঠিকানা লিখুন।"
                                    return@Button
                                }

                                errorMessage = null
                                viewModel.placeOrder(
                                    customerName = customerName,
                                    customerPhone = customerPhone,
                                    deliveryAddress = deliveryAddress,
                                    union = selectedUnion,
                                    paymentMethod = paymentMethod,
                                    notes = notes.ifBlank { null }
                                ) { orders ->
                                    placedOrders = orders
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_confirm_order"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "অর্ডার নিশ্চিত করুন (৳${total.toInt()})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Success Order Dialog
    if (placedOrders != null) {
        val firstOrderId = placedOrders!!.firstOrNull()?.orderId ?: "ORD-BDL-10000"
        AlertDialog(
            onDismissRequest = {},
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald700)
                    Text("অর্ডার সফল হয়েছে!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ধন্যবাদ $customerName! আপনার অর্ডারটি গ্রহণ করা হয়েছে এবং সংশ্লিষ্ট দোকানের উদ্যোক্তাদের জানানো হয়েছে।",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Emerald50,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("আপনার অর্ডার আইডি:", style = MaterialTheme.typography.labelSmall, color = Stone500)
                            Text(
                                text = firstOrderId,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald900
                            )
                        }
                    }

                    Text(
                        text = "ডেলিভারিম্যান অতিদ্রুত বদলগাছীর ${selectedUnion} ইউনিয়নে আপনার সাথে ফোনে যোগাযোগ করবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Stone500
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onOrderPlaced(firstOrderId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("অর্ডার ট্র্যাক করুন")
                }
            }
        )
    }
}
