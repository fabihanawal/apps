package com.example.amardokan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amardokan.model.Shop
import com.example.amardokan.ui.components.*
import com.example.amardokan.ui.screens.*
import com.example.amardokan.ui.theme.AmarDokanTheme
import com.example.amardokan.ui.theme.Amber500
import com.example.amardokan.ui.theme.Emerald700
import com.example.amardokan.ui.theme.Stone900
import com.example.amardokan.viewmodel.MainViewModel
import com.example.amardokan.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as AmarDokanApp
        MainViewModelFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AmarDokanTheme {
                AmarDokanMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmarDokanMainApp(viewModel: MainViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val settings by viewModel.platformSettings.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedUnion by viewModel.selectedUnion.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartCount by viewModel.cartItemCount.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()
    val shops by viewModel.shops.collectAsState()
    val reviews by viewModel.reviews.collectAsState()

    // Dialog states
    val isCartSheetOpen by viewModel.isCartSheetOpen.collectAsState()
    val isCheckoutOpen by viewModel.isCheckoutDialogOpen.collectAsState()
    val isRegisterShopOpen by viewModel.isShopRegistrationOpen.collectAsState()
    val isVendorLoginOpen by viewModel.isVendorLoginOpen.collectAsState()
    val detailedProduct by viewModel.detailedProduct.collectAsState()
    val orderSuccessIds by viewModel.orderSuccessIds.collectAsState()

    // User feedback
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AmarDokanHeader(
                announcementText = settings.announcementText,
                helplinePhone = settings.helplinePhone,
                searchQuery = searchQuery,
                onSearchChange = { viewModel.setSearchQuery(it) },
                selectedUnion = selectedUnion,
                onUnionSelected = { viewModel.setSelectedUnion(it) },
                cartItemCount = cartCount,
                onOpenCart = { viewModel.setCartSheetOpen(true) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "দোকানসমূহ") },
                    label = { Text("দোকানসমূহ", fontSize = 11.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald700,
                        selectedTextColor = Emerald700,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "উদ্যোক্তা") },
                    label = { Text("উদ্যোক্তা", fontSize = 11.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald700,
                        selectedTextColor = Emerald700,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "ট্র্যাকিং") },
                    label = { Text("ট্র্যাকিং", fontSize = 11.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald700,
                        selectedTextColor = Emerald700,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )

                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.setActiveTab(3) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "অ্যাডমিন") },
                    label = { Text("অ্যাডমিন", fontSize = 11.sp, fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald700,
                        selectedTextColor = Emerald700,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        },
        floatingActionButton = {
            if (activeTab == 0 && cartCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.setCartSheetOpen(true) },
                    containerColor = Amber500,
                    contentColor = Stone900,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$cartCount আইটেম • ৳${cartSubtotal.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> StorefrontScreen(
                    viewModel = viewModel,
                    onProductClick = { viewModel.setDetailedProduct(it) },
                    onShopClick = { shop ->
                        viewModel.setSelectedUnion(shop.union)
                    }
                )
                1 -> VendorDashboardScreen(
                    viewModel = viewModel,
                    onOpenRegisterShop = { viewModel.setShopRegistrationOpen(true) }
                )
                2 -> OrderTrackerScreen(
                    viewModel = viewModel
                )
                3 -> AdminScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // Cart Bottom Sheet
    if (isCartSheetOpen) {
        CartBottomSheet(
            cart = cart,
            subtotal = cartSubtotal,
            settings = settings,
            onUpdateQuantity = { id, q -> viewModel.updateCartQuantity(id, q) },
            onRemoveItem = { id -> viewModel.removeFromCart(id) },
            onClearCart = { viewModel.clearCart() },
            onProceedToCheckout = { viewModel.setCheckoutDialogOpen(true) },
            onDismiss = { viewModel.setCartSheetOpen(false) }
        )
    }

    // Checkout Dialog
    if (isCheckoutOpen) {
        val isFreeDelivery = cartSubtotal >= settings.minFreeDeliveryAmount
        val fee = if (isFreeDelivery) 0.0 else settings.deliveryCharge

        CheckoutDialog(
            subtotal = cartSubtotal,
            deliveryFee = fee,
            onDismiss = { viewModel.setCheckoutDialogOpen(false) },
            onConfirmOrder = { name, phone, address, union, payment, notes ->
                viewModel.placeOrder(name, phone, address, union, payment, notes)
            }
        )
    }

    // Order Success Dialog
    orderSuccessIds?.let { ids ->
        OrderReceiptDialog(
            orderIds = ids,
            onDismiss = { viewModel.clearOrderSuccess() }
        )
    }

    // Product Detail Dialog
    detailedProduct?.let { prod ->
        val productShop = shops.find { it.shopId == prod.shopId }
        val productReviews = reviews.filter { it.shopId == prod.shopId }

        ProductDetailDialog(
            product = prod,
            shop = productShop,
            reviews = productReviews,
            onAddToCart = { p, q -> viewModel.addToCart(p, q) },
            onAddReview = { rating, comment, name ->
                viewModel.addReview(prod.shopId, prod.productId, name, rating, comment)
            },
            onDismiss = { viewModel.setDetailedProduct(null) }
        )
    }

    // Shop Registration Dialog
    if (isRegisterShopOpen) {
        ShopRegistrationDialog(
            onDismiss = { viewModel.setShopRegistrationOpen(false) },
            onSubmit = { sName, oName, phone, email, addr, union, cat, desc, logo ->
                viewModel.registerShop(
                    shopName = sName,
                    ownerName = oName,
                    phone = phone,
                    email = email,
                    address = addr,
                    union = union,
                    category = cat,
                    description = desc,
                    logoUrl = logo
                )
            }
        )
    }

    // Vendor Login Dialog
    if (isVendorLoginOpen) {
        VendorLoginDialog(
            onDismiss = { viewModel.setVendorLoginOpen(false) },
            onLogin = { email ->
                viewModel.loginVendorByEmail(email) { res ->
                    when (res) {
                        is com.example.amardokan.repository.VendorLoginResult.Success -> {
                            viewModel.showUserMessage(res.message)
                            viewModel.setActiveTab(1)
                        }
                        is com.example.amardokan.repository.VendorLoginResult.Pending -> {
                            viewModel.showUserMessage(res.message)
                        }
                        is com.example.amardokan.repository.VendorLoginResult.Rejected -> {
                            viewModel.showUserMessage(res.message)
                        }
                        is com.example.amardokan.repository.VendorLoginResult.NotFound -> {
                            viewModel.showUserMessage(res.message)
                        }
                    }
                }
            },
            onOpenRegister = {
                viewModel.setShopRegistrationOpen(true)
            }
        )
    }
}
