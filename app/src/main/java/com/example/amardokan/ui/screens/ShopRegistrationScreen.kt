package com.example.amardokan.ui.screens

import androidx.compose.foundation.background
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
import com.example.amardokan.model.BadalgachhiConstants
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.theme.*
import com.example.amardokan.ui.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopRegistrationScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shopName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedUnion by remember { mutableStateOf(BadalgachhiConstants.UNIONS[0]) }
    var selectedCategory by remember { mutableStateOf(BadalgachhiConstants.CATEGORIES[1]) }
    var description by remember { mutableStateOf("") }
    var logoUrl by remember { mutableStateOf("") }

    var unionDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var registeredShop by remember { mutableStateOf<Shop?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("দোকান নিবন্ধন আবেদন", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Emerald800)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "🏪 আমার দোকানে আপনার ব্যবসা শুরু করুন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            "বদলগাছী উপজেলার ৮টি ইউনিয়নের গ্রাহকদের কাছে আপনার তৈরি পণ্য বা দোকানের মালামাল পৌঁছে দিতে আজই যুক্ত হোন।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Emerald100
                        )
                    }
                }
            }

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
                            "দোকান ও স্বত্বাধিকারীর তথ্য",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald900
                        )

                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            label = { Text("দোকানের নাম *") },
                            placeholder = { Text("যেমন: পাহাড়পুর মাটির মায়া") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_shop_name")
                        )

                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("মালিকের নাম *") },
                            placeholder = { Text("যেমন: মো: আব্দুল করিম") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_owner_name")
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("মোবাইল নম্বর (বিকাশ/নগদ সহ) *") },
                            placeholder = { Text("01XXXXXXXXX") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_phone")
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("ইমেইল এড্রেস (ড্যাশবোর্ড লগইনের জন্য) *") },
                            placeholder = { Text("vendor@gmail.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("reg_email")
                        )

                        // Union Selector
                        ExposedDropdownMenuBox(
                            expanded = unionDropdownExpanded,
                            onExpandedChange = { unionDropdownExpanded = !unionDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedUnion,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ইউনিয়ন *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unionDropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = unionDropdownExpanded,
                                onDismissRequest = { unionDropdownExpanded = false }
                            ) {
                                BadalgachhiConstants.UNIONS.forEach { u ->
                                    DropdownMenuItem(text = { Text(u) }, onClick = {
                                        selectedUnion = u
                                        unionDropdownExpanded = false
                                    })
                                }
                            }
                        }

                        // Category Selector
                        ExposedDropdownMenuBox(
                            expanded = categoryDropdownExpanded,
                            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ব্যবসার ক্যাটাগরি *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = categoryDropdownExpanded,
                                onDismissRequest = { categoryDropdownExpanded = false }
                            ) {
                                BadalgachhiConstants.CATEGORIES.filter { it != "সব ক্যাটাগরি" }.forEach { c ->
                                    DropdownMenuItem(text = { Text(c) }, onClick = {
                                        selectedCategory = c
                                        categoryDropdownExpanded = false
                                    })
                                }
                            }
                        }

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("দোকানের সঠিক অবস্থান বা বাজার মোড় *") },
                            placeholder = { Text("যেমন: পাহাড়পুর প্রত্নস্থল বাজার, বৌদ্ধবিহার রোড") },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = logoUrl,
                            onValueChange = { logoUrl = it },
                            label = { Text("দোকানের লোগো বা সাইনবোর্ডের ছবি URL (ঐচ্ছিক)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("দোকান ও পণ্যের বিবরণ *") },
                            placeholder = { Text("ঐতিহ্যবাহী পোড়ামাটির ফলক, মাটির শোপিস ও কুটির শিল্প...") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (errorText != null) {
                            Text(errorText!!, color = Red600, style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = {
                                if (shopName.isBlank() || ownerName.isBlank() || phone.isBlank() || email.isBlank() || address.isBlank()) {
                                    errorText = "অনুগ্রহ করে সকল আবশ্যকীয় (*) তথ্য পূরণ করুন।"
                                    return@Button
                                }
                                errorText = null
                                viewModel.registerShop(
                                    shopName = shopName,
                                    ownerName = ownerName,
                                    phone = phone,
                                    email = email,
                                    address = address,
                                    union = selectedUnion,
                                    category = selectedCategory,
                                    description = description,
                                    logoUrl = logoUrl
                                ) { s ->
                                    registeredShop = s
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_shop_reg"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                        ) {
                            Text("আবেদন জমা দিন", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (registeredShop != null) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Amber600)
                    Text("আবেদন জমা হয়েছে!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("দোকান \"${registeredShop!!.shopName}\" এর আবেদনটি সফলভাবে সিস্টেমে যুক্ত হয়েছে।")
                    Text("অ্যাডমিন (RSTS-BD: 01755383039) যাচাই-বাছাই করে অনুমোদন দেওয়ার পর আপনার দোকানটি ক্রেতাদের কাছে দৃশ্যমান হবে এবং আপনি ড্যাশবোর্ডে পণ্য যোগ করতে পারবেন।")
                }
            },
            confirmButton = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("ঠিক আছে")
                }
            }
        )
    }
}
