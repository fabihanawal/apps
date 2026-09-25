package com.example.amardokan.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.amardokan.model.Constants
import com.example.amardokan.model.PlatformSettings
import com.example.amardokan.model.Product
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.theme.*

@Composable
fun ShopRegistrationDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        shopName: String,
        ownerName: String,
        phone: String,
        email: String,
        address: String,
        union: String,
        category: String,
        description: String,
        logoUrl: String
    ) -> Unit
) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedUnion by remember { mutableStateOf(Constants.BADALGACHHI_UNIONS[0]) }
    var unionExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(Constants.PRODUCT_CATEGORIES[1]) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var logoUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
                // Header
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
                            text = "দোকান নিবন্ধন আবেদন",
                            color = White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "বদলগাছীর স্থানীয় উদ্যোক্তা হিসেবে যোগ দিন",
                            color = Emerald100,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = White)
                    }
                }

                // Form Fields
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        if (errorMessage.isNotBlank()) {
                            Surface(color = Red50, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(errorMessage, color = Red600, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            label = { Text("দোকান / প্রতিষ্ঠানের নাম *") },
                            placeholder = { Text("যেমন: পাহাড়পুর মাটির কুটির") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("প্রোপাইটার / স্বত্বাধিকারীর নাম *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("মোবাইল নম্বর (যোগাযোগ ও WhatsApp) *") },
                            placeholder = { Text("01XXXXXXXXX") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("ইমেইল এড্রেস (ড্যাশবোর্ড লগইনের জন্য) *") },
                            placeholder = { Text("vendor@amardokan.bd") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        Text("ইউনিয়ন নির্বাচন করুন *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Stone700)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { unionExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(selectedUnion, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(expanded = unionExpanded, onDismissRequest = { unionExpanded = false }) {
                                Constants.BADALGACHHI_UNIONS.forEach { u ->
                                    DropdownMenuItem(text = { Text(u) }, onClick = { selectedUnion = u; unionExpanded = false })
                                }
                            }
                        }
                    }

                    item {
                        Text("ব্যবসার ক্যাটাগরি *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Stone700)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { categoryExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(selectedCategory, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                                Constants.PRODUCT_CATEGORIES.filter { it != "সব ক্যাটাগরি" }.forEach { c ->
                                    DropdownMenuItem(text = { Text(c) }, onClick = { selectedCategory = c; categoryExpanded = false })
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("দোকানের সঠিক ঠিকানা (বাজার বা মোড়ের নাম) *") },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("দোকানের বিবরণ ও বিশেষত্ব") },
                            placeholder = { Text("আপনার দোকানের বিশেষ পণ্য বা সেবার পরিচয় দিন") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = logoUrl,
                            onValueChange = { logoUrl = it },
                            label = { Text("দোকানের লোগো / ছবি URL (ঐচ্ছিক)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Submit Action
                Surface(
                    color = Stone100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                            Text("বাতিল")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (shopName.isBlank() || ownerName.isBlank() || phone.isBlank() || email.isBlank() || address.isBlank()) {
                                    errorMessage = "অনুগ্রহ করে সকল আবশ্যকীয় (*) তথ্য পূরণ করুন।"
                                    return@Button
                                }
                                onSubmit(
                                    shopName.trim(),
                                    ownerName.trim(),
                                    phone.trim(),
                                    email.trim(),
                                    address.trim(),
                                    selectedUnion,
                                    selectedCategory,
                                    description.trim(),
                                    logoUrl.trim()
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("আবেদন জমা দিন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VendorLoginDialog(
    onDismiss: () -> Unit,
    onLogin: (email: String) -> Unit,
    onOpenRegister: () -> Unit
) {
    var emailInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("উদ্যোক্তা লগইন", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Stone900)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "আপনার নিবন্ধিত ইমেইল এড্রেস দিয়ে দোকানে প্রবেশ করুন।",
                    fontSize = 12.sp,
                    color = Stone600
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("দোকানের ইমেইল এড্রেস") },
                    placeholder = { Text("যেমন: paharpur.crafts@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Emerald700) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (emailInput.isNotBlank()) {
                            onLogin(emailInput.trim())
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ড্যাশবোর্ডে প্রবেশ করুন", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("নতুন দোকানদার? ", fontSize = 12.sp, color = Stone600)
                    Text(
                        text = "নতুন দোকান খুলুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald700,
                        modifier = Modifier.clickable {
                            onDismiss()
                            onOpenRegister()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onLogin: (email: String, pass: String) -> Boolean
) {
    var email by remember { mutableStateOf("rstsbd@gmail.com") }
    var password by remember { mutableStateOf("rstsbd1234") }
    var errorMsg by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Emerald700)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("সুপার অ্যাডমিন লগইন", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "প্ল্যাটফর্ম পরিচালনা ও দোকান অনুমোদনের জন্য প্রবেশ করুন।",
                    fontSize = 12.sp,
                    color = Stone600
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (errorMsg.isNotBlank()) {
                    Surface(color = Red50, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(errorMsg, color = Red600, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("অ্যাডমিন ইমেইল") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("পাসওয়ার্ড") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val ok = onLogin(email.trim(), password)
                        if (ok) {
                            onDismiss()
                        } else {
                            errorMsg = "ভুল ইমেইল বা পাসওয়ার্ড!"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("অ্যাডমিন প্যানেলে লগইন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProductFormDialog(
    initialProduct: Product?,
    shopId: String,
    onDismiss: () -> Unit,
    onSave: (
        productName: String,
        price: Double,
        discountPrice: Double?,
        stock: Int,
        description: String,
        imageUrl: String,
        category: String,
        unit: String
    ) -> Unit
) {
    var productName by remember { mutableStateOf(initialProduct?.productName ?: "") }
    var priceText by remember { mutableStateOf(initialProduct?.price?.toInt()?.toString() ?: "") }
    var discountPriceText by remember { mutableStateOf(initialProduct?.discountPrice?.toInt()?.toString() ?: "") }
    var stockText by remember { mutableStateOf(initialProduct?.stock?.toString() ?: "10") }
    var unitText by remember { mutableStateOf(initialProduct?.unit ?: "১ টি") }
    var selectedCategory by remember { mutableStateOf(initialProduct?.category ?: Constants.PRODUCT_CATEGORIES[1]) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var imageUrl by remember { mutableStateOf(initialProduct?.imageUrl ?: "") }
    var errorMessage by remember { mutableStateOf("") }

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Emerald700)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (initialProduct == null) "নতুন পণ্য যোগ করুন" else "পণ্য সম্পাদনা করুন",
                        color = White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = White) }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        if (errorMessage.isNotBlank()) {
                            Surface(color = Red50, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(errorMessage, color = Red600, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("পণ্যের নাম *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = priceText,
                                onValueChange = { priceText = it },
                                label = { Text("মূল্য (৳) *") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = discountPriceText,
                                onValueChange = { discountPriceText = it },
                                label = { Text("ছাড় মূল্য (ঐচ্ছিক)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = stockText,
                                onValueChange = { stockText = it },
                                label = { Text("স্টক পরিমাণ *") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = unitText,
                                onValueChange = { unitText = it },
                                label = { Text("একক (যেমন: ১ কেজি)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    item {
                        Text("ক্যাটাগরি *", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Stone700)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { categoryExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(selectedCategory, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                                Constants.PRODUCT_CATEGORIES.filter { it != "সব ক্যাটাগরি" }.forEach { c ->
                                    DropdownMenuItem(text = { Text(c) }, onClick = { selectedCategory = c; categoryExpanded = false })
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("পণ্যের বিস্তারিত বিবরণ") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("পণ্যের ছবি URL (ঐচ্ছিক)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Surface(color = Stone100, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                            Text("বাতিল")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val price = priceText.toDoubleOrNull()
                                val stock = stockText.toIntOrNull() ?: 1
                                val discount = discountPriceText.toDoubleOrNull()

                                if (productName.isBlank() || price == null) {
                                    errorMessage = "অনুগ্রহ করে পণ্যের নাম ও সঠিক মূল্য প্রদান করুন।"
                                    return@Button
                                }

                                onSave(
                                    productName.trim(),
                                    price,
                                    discount,
                                    stock,
                                    description.trim(),
                                    imageUrl.trim(),
                                    selectedCategory,
                                    unitText.trim()
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
