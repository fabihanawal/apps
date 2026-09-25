package com.example.amardokan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amardokan.ui.components.OrderTimelineCard
import com.example.amardokan.ui.theme.*
import com.example.amardokan.viewmodel.MainViewModel

@Composable
fun OrderTrackerScreen(
    viewModel: MainViewModel
) {
    val trackerQuery by viewModel.trackerSearchQuery.collectAsState()
    val searchResults by viewModel.trackerResults.collectAsState()
    val isSearched by viewModel.isTrackerSearched.collectAsState()
    val allOrders by viewModel.orders.collectAsState()

    var searchInput by remember { mutableStateOf(trackerQuery) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Stone50),
        contentPadding = PaddingValues(16.dp, bottom = 90.dp)
    ) {
        // Search Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrackChanges, contentDescription = null, tint = Amber500)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "লাইভ অর্ডার ট্র্যাকার",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "আপনার অর্ডার নম্বর (Order ID) বা মোবাইল নম্বর লিখে বর্তমান অবস্থা জানুন।",
                        color = Emerald100,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            placeholder = { Text("যেমন: ORD-BDL-10291 বা 01712345678", fontSize = 12.sp, color = Stone400) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Emerald700) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = White,
                                unfocusedContainerColor = White
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.searchTracker(searchInput.trim()) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Stone900),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("খুঁজুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quick demo order suggestions
        if (!isSearched) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 সাম্প্রতিক পরীক্ষামূলক অর্ডার নম্বরসমূহ (ক্লিক করে ট্র্যাক করুন):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Stone800
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        allOrders.take(3).forEach { o ->
                            Surface(
                                color = Stone100,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable {
                                        searchInput = o.orderId
                                        viewModel.searchTracker(o.orderId)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(o.orderId, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Emerald700)
                                        Text("${o.customerName} • ${o.shopName}", fontSize = 11.sp, color = Stone600)
                                    }
                                    Text(
                                        text = o.orderStatus,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Stone700
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Search Results Section
        if (isSearched) {
            item {
                Text(
                    text = "অনুসন্ধানের ফলাফল (${searchResults.size}টি অর্ডার পাওয়া গেছে):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Stone900
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (searchResults.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = Stone400, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "কোনো অর্ডার পাওয়া যায়নি",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Stone800
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "অনুগ্রহ করে অর্ডার নম্বর বা মোবাইল নম্বর সঠিকভাবে টাইপ করেছেন কিনা নিশ্চিত করুন।",
                                fontSize = 12.sp,
                                color = Stone500
                            )
                        }
                    }
                }
            } else {
                items(searchResults) { ord ->
                    Box(modifier = Modifier.padding(bottom = 12.dp)) {
                        OrderTimelineCard(order = ord)
                    }
                }
            }
        }
    }
}
