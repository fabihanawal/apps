import React, { useState, useMemo } from 'react';
import { useAmarDokanStore } from './services/store';
import { Header } from './components/Header';
import { HeroBanner } from './components/HeroBanner';
import { ProductCard } from './components/ProductCard';
import { ProductDetailModal } from './components/ProductDetailModal';
import { CartDrawer } from './components/CartDrawer';
import { CheckoutModal } from './components/CheckoutModal';
import { OrderTrackerModal } from './components/OrderTrackerModal';
import { VendorDashboard } from './components/VendorDashboard';
import { ShopRegistrationModal } from './components/ShopRegistrationModal';
import { FirebaseArchitectureModal } from './components/FirebaseArchitectureModal';
import { AdminModal } from './components/AdminModal';
import { VendorLoginModal } from './components/VendorLoginModal';
import { Footer } from './components/Footer';
import {
  Product,
  Shop,
  BADALGACHHI_UNIONS,
  PRODUCT_CATEGORIES,
} from './types';
import {
  MapPin,
  Filter,
  Sparkles,
  Store,
  Phone,
  MessageCircle,
  ShoppingBag,
  ArrowRight,
  ShieldCheck,
  Check,
} from 'lucide-react';

export default function App() {
  const store = useAmarDokanStore();

  // Navigation view state
  const [activeView, setActiveView] = useState<'store' | 'vendor' | 'track' | 'schema'>('store');

  // Filter & Search states
  const [selectedUnion, setSelectedUnion] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<string>('সব ক্যাটাগরি');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // UI Modals state
  const [isCartOpen, setIsCartOpen] = useState<boolean>(false);
  const [isCheckoutOpen, setIsCheckoutOpen] = useState<boolean>(false);
  const [isRegisterShopOpen, setIsRegisterShopOpen] = useState<boolean>(false);
  const [isAdminModalOpen, setIsAdminModalOpen] = useState<boolean>(false);
  const [isVendorLoginOpen, setIsVendorLoginOpen] = useState<boolean>(false);
  const [detailedProduct, setDetailedProduct] = useState<Product | null>(null);

  // Cart total count
  const cartTotalItems = useMemo(
    () => store.cart.reduce((total, item) => total + item.quantity, 0),
    [store.cart]
  );

  // Shop map for rapid lookup
  const shopMap = useMemo(() => {
    const map = new Map<string, Shop>();
    store.shops.forEach((s) => map.set(s.shopId, s));
    return map;
  }, [store.shops]);

  // Filtered Products based on Union, Category, and Search Query (Only from Active shops)
  const filteredProducts = useMemo(() => {
    return store.products.filter((product) => {
      const shop = shopMap.get(product.shopId);

      // Only show products from Active shops to consumers
      if (!shop || shop.status !== 'Active') {
        return false;
      }

      // Union Filter
      if (selectedUnion && shop?.union !== selectedUnion) {
        return false;
      }

      // Category Filter
      if (selectedCategory && selectedCategory !== 'সব ক্যাটাগরি' && product.category !== selectedCategory) {
        return false;
      }

      // Search Query
      if (searchQuery.trim()) {
        const query = searchQuery.toLowerCase().trim();
        const matchesName = product.productName.toLowerCase().includes(query);
        const matchesShop = shop?.shopName.toLowerCase().includes(query);
        const matchesCategory = product.category.toLowerCase().includes(query);
        const matchesUnion = shop?.union.toLowerCase().includes(query);
        if (!matchesName && !matchesShop && !matchesCategory && !matchesUnion) {
          return false;
        }
      }

      return true;
    });
  }, [store.products, shopMap, selectedUnion, selectedCategory, searchQuery]);

  // Filtered Active Shops based on selected Union for consumers
  const filteredShops = useMemo(() => {
    const active = store.shops.filter((s) => s.status === 'Active');
    if (!selectedUnion) return active;
    return active.filter((s) => s.union === selectedUnion);
  }, [store.shops, selectedUnion]);

  return (
    <div className="min-h-screen flex flex-col bg-stone-50 selection:bg-emerald-600 selection:text-white">
      {/* Header */}
      <Header
        activeView={activeView}
        setActiveView={setActiveView}
        cartCount={cartTotalItems}
        openCart={() => setIsCartOpen(true)}
        selectedUnion={selectedUnion}
        setSelectedUnion={setSelectedUnion}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        announcementText={store.platformSettings.announcementText}
        helplinePhone={store.platformSettings.helplinePhone}
      />

      {/* Main Content Area */}
      <main className="flex-1">
        {/* VIEW 1: STOREFRONT */}
        {activeView === 'store' && (
          <div className="max-w-7xl mx-auto px-4 sm:px-6 py-6">
            {/* Hero Section with Badalgachhi Union quick selector */}
            <HeroBanner
              selectedUnion={selectedUnion}
              setSelectedUnion={setSelectedUnion}
              totalShops={store.shops.length}
              totalProducts={store.products.length}
              onRegisterShopClick={() => setIsRegisterShopOpen(true)}
              heroHeadline={store.platformSettings.heroHeadline}
              heroSubheadline={store.platformSettings.heroSubheadline}
            />

            {/* Filter Toolbar */}
            <div id="products-section" className="mb-6 space-y-4">
              {/* Category Pills Bar */}
              <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
                <span className="text-xs font-bold text-stone-500 uppercase tracking-wider shrink-0 flex items-center gap-1">
                  <Filter className="w-3.5 h-3.5" />
                  <span>ক্যাটাগরি:</span>
                </span>
                {PRODUCT_CATEGORIES.map((cat) => (
                  <button
                    key={cat}
                    onClick={() => setSelectedCategory(cat)}
                    className={`px-3.5 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all border ${
                      selectedCategory === cat
                        ? 'bg-emerald-700 text-white border-emerald-700 shadow-xs font-bold'
                        : 'bg-white text-stone-700 border-stone-200 hover:bg-stone-100'
                    }`}
                  >
                    {cat}
                  </button>
                ))}
              </div>

              {/* Active Filter Indicator */}
              {(selectedUnion || (selectedCategory && selectedCategory !== 'সব ক্যাটাগরি') || searchQuery) && (
                <div className="flex flex-wrap items-center justify-between gap-2 p-3 bg-emerald-50 rounded-2xl border border-emerald-200 text-xs text-emerald-900">
                  <div className="flex items-center gap-2">
                    <span className="font-bold">সক্রিয় ফিল্টার:</span>
                    {selectedUnion && (
                      <span className="bg-white px-2 py-0.5 rounded-md font-semibold border border-emerald-300">
                        📍 ইউনিয়ন: {selectedUnion}
                      </span>
                    )}
                    {selectedCategory !== 'সব ক্যাটাগরি' && (
                      <span className="bg-white px-2 py-0.5 rounded-md font-semibold border border-emerald-300">
                        🏷️ {selectedCategory}
                      </span>
                    )}
                    {searchQuery && (
                      <span className="bg-white px-2 py-0.5 rounded-md font-semibold border border-emerald-300">
                        🔍 "{searchQuery}"
                      </span>
                    )}
                  </div>

                  <button
                    onClick={() => {
                      setSelectedUnion('');
                      setSelectedCategory('সব ক্যাটাগরি');
                      setSearchQuery('');
                    }}
                    className="text-emerald-700 hover:text-emerald-900 font-bold underline"
                  >
                    ফিল্টার রিসেট করুন
                  </button>
                </div>
              )}
            </div>

            {/* Products Grid */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-xl font-extrabold text-stone-900">
                    {selectedUnion ? `${selectedUnion} ইউনিয়নের পণ্যসমূহ` : 'বদলগাছীর সকল পণ্য'}
                  </h2>
                  <p className="text-xs text-stone-500">
                    {filteredProducts.length} টি পণ্য পাওয়া গেছে • ক্যাশ অন ডেলিভারি সুবিধা
                  </p>
                </div>

                <button
                  onClick={() => setIsRegisterShopOpen(true)}
                  className="hidden sm:inline-flex items-center gap-1.5 text-xs font-bold text-emerald-700 hover:text-emerald-800 bg-emerald-50 px-3 py-1.5 rounded-xl border border-emerald-200"
                >
                  <Store className="w-3.5 h-3.5" />
                  <span>আপনি কি দোকানদার? এখানে যুক্ত হন</span>
                </button>
              </div>

              {filteredProducts.length === 0 ? (
                <div className="bg-white p-12 rounded-3xl border border-stone-200 text-center space-y-3">
                  <ShoppingBag className="w-12 h-12 text-stone-300 mx-auto" />
                  <h3 className="text-base font-bold text-stone-800">
                    এই ফিল্টারে কোনো পণ্য পাওয়া যায়নি
                  </h3>
                  <p className="text-xs text-stone-500 max-w-sm mx-auto">
                    অন্য কোনো ইউনিয়ন বা ক্যাটাগরি সিলেক্ট করুন, অথবা সব পণ্য দেখতে ফিল্টার রিসেট করুন।
                  </p>
                  <button
                    onClick={() => {
                      setSelectedUnion('');
                      setSelectedCategory('সব ক্যাটাগরি');
                      setSearchQuery('');
                    }}
                    className="px-4 py-2 bg-emerald-600 text-white rounded-xl text-xs font-bold shadow-xs hover:bg-emerald-700"
                  >
                    সব পণ্য প্রদর্শন করুন
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-5">
                  {filteredProducts.map((product) => (
                    <ProductCard
                      key={product.productId}
                      product={product}
                      shop={shopMap.get(product.shopId)}
                      onAddToCart={(prod) => store.addToCart(prod, 1)}
                      onViewDetails={(prod) => setDetailedProduct(prod)}
                    />
                  ))}
                </div>
              )}
            </div>

            {/* Badalgachhi Local Shops Directory Section */}
            <div className="mt-16 space-y-6">
              <div className="flex items-center justify-between border-b border-stone-200 pb-3">
                <div>
                  <h3 className="text-lg font-bold text-stone-900">
                    {selectedUnion ? `${selectedUnion} এর নিবন্ধিত দোকানসমূহ` : 'বদলগাছীর শীর্ষস্থানীয় দোকানসমূহ'}
                  </h3>
                  <p className="text-xs text-stone-500">
                    সরাসরি দোকান মালিকের সাথে যোগাযোগ ও পণ্যের নির্ভরযোগ্যতা
                  </p>
                </div>
                <button
                  onClick={() => setActiveView('vendor')}
                  className="text-xs font-bold text-emerald-700 hover:underline"
                >
                  দোকান পরিচালনা &rarr;
                </button>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {filteredShops.map((shop) => (
                  <div
                    key={shop.shopId}
                    className="bg-white p-5 rounded-2xl border border-stone-200 hover:border-emerald-400 shadow-xs transition-all flex flex-col justify-between space-y-3"
                  >
                    <div className="flex items-start gap-3.5">
                      <div className="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0 font-bold overflow-hidden border border-emerald-200">
                        {shop.logoUrl ? (
                          <img src={shop.logoUrl} alt={shop.shopName} className="w-full h-full object-cover" />
                        ) : (
                          <Store className="w-6 h-6" />
                        )}
                      </div>
                      <div className="min-w-0 flex-1">
                        <div className="flex items-center justify-between">
                          <span className="text-[10px] font-bold text-emerald-800 bg-emerald-50 px-2 py-0.5 rounded">
                            {shop.category}
                          </span>
                          <span className="text-[11px] text-stone-500 flex items-center gap-1 font-semibold">
                            ⭐ {shop.rating || 5.0} ({shop.totalReviews || 1})
                          </span>
                        </div>
                        <h4 className="text-sm font-bold text-stone-900 truncate mt-1">
                          {shop.shopName}
                        </h4>
                        <p className="text-xs text-stone-500 mt-0.5">
                          প্রোপাইটর: {shop.ownerName}
                        </p>
                      </div>
                    </div>

                    <div className="pt-3 border-t border-stone-100 flex items-center justify-between text-xs">
                      <span className="flex items-center gap-1 text-stone-600 font-medium">
                        <MapPin className="w-3.5 h-3.5 text-emerald-600" />
                        <span>{shop.union}, বদলগাছী</span>
                      </span>

                      <a
                        href={`https://wa.me/88${shop.phone.replace(/[^0-9]/g, '')}`}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1 text-emerald-700 font-bold hover:underline"
                      >
                        <MessageCircle className="w-3.5 h-3.5" />
                        <span>{shop.phone}</span>
                      </a>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* VIEW 2: VENDOR DASHBOARD */}
        {activeView === 'vendor' && (
          <VendorDashboard
            shops={store.shops}
            activeShopId={store.activeShopId}
            setActiveShopId={store.setActiveShopId}
            products={store.products}
            orders={store.orders}
            marketingPosts={store.marketingPosts}
            reviews={store.reviews}
            onAddProduct={store.addProduct}
            onUpdateProduct={store.updateProduct}
            onDeleteProduct={store.deleteProduct}
            onUpdateOrderStatus={store.updateOrderStatus}
            onSaveMarketingPost={store.saveMarketingPost}
            onOpenRegisterShop={() => setIsRegisterShopOpen(true)}
            loggedInVendorEmail={store.loggedInVendorEmail}
            onOpenVendorLogin={() => setIsVendorLoginOpen(true)}
            onLogoutVendor={store.logoutVendor}
          />
        )}

        {/* VIEW 3: ORDER TRACKER */}
        {activeView === 'track' && (
          <OrderTrackerModal orders={store.orders} />
        )}

        {/* VIEW 4: FIREBASE ARCHITECTURE & SCHEMA */}
        {activeView === 'schema' && (
          <FirebaseArchitectureModal />
        )}
      </main>

      {/* Slide-over Cart Drawer */}
      <CartDrawer
        isOpen={isCartOpen}
        onClose={() => setIsCartOpen(false)}
        cart={store.cart}
        onUpdateQuantity={store.updateCartQuantity}
        onRemoveItem={store.removeFromCart}
        onProceedToCheckout={() => setIsCheckoutOpen(true)}
      />

      {/* Checkout Modal */}
      <CheckoutModal
        isOpen={isCheckoutOpen}
        onClose={() => setIsCheckoutOpen(false)}
        cart={store.cart}
        onPlaceOrder={store.placeOrder}
      />

      {/* Product Detail & Customer Review Modal */}
      <ProductDetailModal
        product={detailedProduct}
        shop={detailedProduct ? shopMap.get(detailedProduct.shopId) : undefined}
        reviews={store.reviews}
        onClose={() => setDetailedProduct(null)}
        onAddToCart={(prod, qty) => store.addToCart(prod, qty)}
        onAddReview={store.addReview}
      />

      {/* Shop Registration Modal */}
      <ShopRegistrationModal
        isOpen={isRegisterShopOpen}
        onClose={() => setIsRegisterShopOpen(false)}
        onRegisterShop={store.registerShop}
        onOpenVendorLogin={(prefillEmail) => {
          setIsVendorLoginOpen(true);
        }}
      />

      {/* Admin Panel Modal (Triggered ONLY by 3 taps on red heart in footer + password login) */}
      <AdminModal
        isOpen={isAdminModalOpen}
        onClose={() => {
          store.logoutAdmin();
          setIsAdminModalOpen(false);
        }}
        isAdminLoggedIn={store.isAdminLoggedIn}
        onLoginAdmin={store.loginAdmin}
        onLogoutAdmin={store.logoutAdmin}
        shops={store.shops}
        products={store.products}
        orders={store.orders}
        onApproveShop={store.approveShop}
        onRejectShop={store.rejectShop}
        onDeleteShop={store.deleteShop}
        onUpdateShop={store.updateShop}
        onAddShopDirect={store.addShopDirectly}
        onAddProduct={store.addProduct}
        onUpdateProduct={store.updateProduct}
        onDeleteProduct={store.deleteProduct}
        onUpdateOrderStatus={store.updateOrderStatus}
        onDeleteOrder={store.deleteOrder}
        platformSettings={store.platformSettings}
        onUpdatePlatformSettings={store.updatePlatformSettings}
        onResetToDefaults={store.resetToDefaults}
        onSelectShopForDashboard={(shopId) => {
          store.setActiveShopId(shopId);
          setActiveView('vendor');
        }}
      />

      {/* Vendor Login Modal (Login via registered email) */}
      <VendorLoginModal
        isOpen={isVendorLoginOpen}
        onClose={() => setIsVendorLoginOpen(false)}
        onLoginByEmail={store.loginVendorByEmail}
        onOpenRegistration={() => setIsRegisterShopOpen(true)}
        onSuccessLogin={(shop) => {
          setActiveView('vendor');
        }}
        shops={store.shops}
      />

      {/* Footer - Clean, Customer-Centric; 3 clicks on red heart opens Admin login */}
      <Footer
        onOpenAdmin={() => {
          store.logoutAdmin(); // Always require submitting email and password
          setIsAdminModalOpen(true);
        }}
        helplinePhone={store.platformSettings.helplinePhone}
        whatsappPhone={store.platformSettings.whatsappNumber}
      />
    </div>
  );
}
