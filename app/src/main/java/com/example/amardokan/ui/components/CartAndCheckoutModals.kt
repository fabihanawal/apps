package com.example.amardokan.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.amardokan.model.CartItem
import com.example.amardokan.model.Constants
import com.example.amardokan.model.PlatformSettings
import com.example.amardokan.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
    cart: List<CartItem>,
    subtotal: Double,
    settings: PlatformSettings,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onDismiss: () -> Unit
) {
    val isFreeDelivery = subtotal >= settings.minFreeDeliveryAmount
    val deliveryFee = if (isFreeDelivery || subtotal == 0.0) 0.0 else settings.deliveryCharge
    val grandTotal = subtotal + deliveryFee

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Cart Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Emerald700,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "আপনার শপিং ব্যাগ (${cart.sumOf { it.quantity }})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone900
                    )
                }

                if (cart.isNotEmpty()) {
                    TextButton(onClick = onClearCart) {
                        Text("সব মুছুন", color = Red600, fontSize = 12.sp)
                    }
                }
            }

            HorizontalDivider(color = Stone200)

            if (cart.isEmpty()) {
                // Empty Cart State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        tint = Stone400,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "আপনার ব্যাগ বর্তমানে খালি রয়েছে",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Stone700
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "বদলগাছীর সেরা দোকানগুলো থেকে পছন্দের পণ্য যোগ করুন।",
                        fontSize = 13.sp,
                        color = Stone500
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("কেনাকাটা চালিয়ে যান")
                    }
                }
            } else {
                // Delivery threshold banner
                Surface(
                    color = if (isFreeDelivery) Emerald50 else Amber50,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFreeDelivery) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (isFreeDelivery) Emerald700 else Amber600,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFreeDelivery) {
                                "🎉 অভিনন্দন! আপনি ফ্রি হোম ডেলিভারি পাচ্ছেন।"
                            } else {
                                "💡 আর ৳${(settings.minFreeDeliveryAmount - subtotal).toInt()} এর কেনাকাটা করলে পাচ্ছেন ফ্রি ডেলিভারি!"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isFreeDelivery) Emerald800 else Stone800
                        )
                    }
                }

                // Cart Items List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(horizontal = 16.dp)
                ) {
                    items(cart) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.productName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Stone100)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "৳${item.price.toInt()} × ${item.quantity} = ৳${(item.price * item.quantity).toInt()}",
                                    fontSize = 12.sp,
                                    color = Emerald700,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Quantity Controls
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilledIconButton(
                                        onClick = { onUpdateQuantity(item.productId, item.quantity - 1) },
                                        shape = CircleShape,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = Stone100,
                                            contentColor = Stone800
                                        ),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "কমান",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }

                                    Text(
                                        text = item.quantity.toString(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )

                                    FilledIconButton(
                                        onClick = { onUpdateQuantity(item.productId, item.quantity + 1) },
                                        enabled = item.quantity < item.maxStock,
                                        shape = CircleShape,
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = Stone100,
                                            contentColor = Stone800
                                        ),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "বাড়ান",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Delete button
                            IconButton(onClick = { onRemoveItem(item.productId) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "মুছুন",
                                    tint = Red600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = Stone100)
                    }
                }

                // Billing Summary & Checkout CTA
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("পণ্যের মূল্য (সাবটোটাল):", fontSize = 13.sp, color = Stone600)
                        Text("৳${subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ডেলিভারি চার্জ (বদলগাছী উপজেলা):", fontSize = 13.sp, color = Stone600)
                        Text(
                            text = if (deliveryFee == 0.0) "ফ্রি (FREE)" else "৳${deliveryFee.toInt()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (deliveryFee == 0.0) Emerald700 else Stone800
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Stone200)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("সর্বমোট প্রদেয়:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Stone900)
                        Text("৳${grandTotal.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Emerald700)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onProceedToCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "অর্ডার সম্পন্ন করতে এগিয়ে যান (৳${grandTotal.toInt()})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialog(
    subtotal: Double,
    deliveryFee: Double,
    onDismiss: () -> Unit,
    onConfirmOrder: (
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        union: String,
        paymentMethod: String,
        notes: String
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var selectedUnion by remember { mutableStateOf(Constants.BADALGACHHI_UNIONS[0]) }
    var unionExpanded by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("ক্যাশ অন ডেলিভারি (COD)") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val grandTotal = subtotal + deliveryFee

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Dialog Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Emerald700)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "অর্ডার নিশ্চিতকরণ ও চেকআউট",
                            color = White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "বদলগাছীর ৮টি ইউনিয়নে দ্রুত হোম ডেলিভারি",
                            color = Emerald100,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = White)
                    }
                }

                // Form Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        if (errorMessage.isNotBlank()) {
                            Surface(
                                color = Red50,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage,
                                    color = Red600,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("আপনার পূর্ণ নাম *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Emerald700) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("মোবাইল নম্বর (১১ ডিজিট) *") },
                            placeholder = { Text("01XXXXXXXXX") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Emerald700) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        Text(
                            text = "ডেলিভারি ইউনিয়ন (বদলগাছী উপজেলা) *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone700
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { unionExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Emerald700)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(selectedUnion, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }

                            DropdownMenu(
                                expanded = unionExpanded,
                                onDismissRequest = { unionExpanded = false }
                            ) {
                                Constants.BADALGACHHI_UNIONS.forEach { union ->
                                    DropdownMenuItem(
                                        text = { Text(union) },
                                        onClick = {
                                            selectedUnion = union
                                            unionExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("সম্পূর্ণ ঠিকানা (গ্রাম, পাড়া, সড়কের নাম) *") },
                            placeholder = { Text("যেমন: আধাইপুর পশ্চিমপাড়া, বটতলা মোড়") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = Emerald700) },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    item {
                        Text(
                            text = "পেমেন্ট পদ্ধতি নির্বাচন করুন *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone700
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val methods = listOf(
                            "ক্যাশ অন ডেলিভারি (COD)",
                            "বিকাশ (bKash)",
                            "নগদ (Nagad)"
                        )

                        methods.forEach { method ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (paymentMethod == method) Emerald50 else Stone100,
                                border = if (paymentMethod == method) androidx.compose.foundation.BorderStroke(1.5.dp, Emerald700) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { paymentMethod = method }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = paymentMethod == method,
                                        onClick = { paymentMethod = method },
                                        colors = RadioButtonDefaults.colors(selectedColor = Emerald700)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = method,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (paymentMethod == method) Emerald800 else Stone800
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("ডেলিভারি সম্পর্কিত বিশেষ নির্দেশনা (ঐচ্ছিক)") },
                            placeholder = { Text("যেমন: সকাল ১০টার পর ডেলিভারি দেবেন") },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Footer with Total and Order CTA
                Surface(
                    color = Stone100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("মোট প্রদেয় বিল:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("৳${grandTotal.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Emerald700)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (customerName.isBlank()) {
                                    errorMessage = "অনুগ্রহ করে আপনার পূর্ণ নাম লিখুন।"
                                    return@Button
                                }
                                if (customerPhone.isBlank() || customerPhone.length < 11) {
                                    errorMessage = "অনুগ্রহ করে সঠিক মোবাইল নম্বর প্রদান করুন (১১ ডিজিট)।"
                                    return@Button
                                }
                                if (deliveryAddress.isBlank()) {
                                    errorMessage = "অনুগ্রহ করে আপনার সঠিক ডেলিভারি ঠিকানা লিখুন।"
                                    return@Button
                                }
                                errorMessage = ""
                                onConfirmOrder(
                                    customerName.trim(),
                                    customerPhone.trim(),
                                    deliveryAddress.trim(),
                                    selectedUnion,
                                    paymentMethod,
                                    notes.trim()
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অর্ডার নিশ্চিত করুন",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderReceiptDialog(
    orderIds: List<String>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Emerald100,
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "অর্ডার সফলভাবে গৃহীত হয়েছে!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald800
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ধন্যবাদ! আপনার অর্ডারটি সংশ্লিষ্ট দোকানদারের কাছে পাঠানো হয়েছে। খুব শীঘ্রই আপনার মোবাইল নম্বরে যোগাযোগ করে ডেলিভারি নিশ্চিত করা হবে।",
                    fontSize = 12.sp,
                    color = Stone600,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Stone100,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "আপনার অর্ডার নম্বর (Order ID):",
                            fontSize = 11.sp,
                            color = Stone600,
                            fontWeight = FontWeight.Medium
                        )
                        orderIds.forEach { id ->
                            Text(
                                text = id,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ঠিক আছে", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
