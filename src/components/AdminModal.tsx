import React, { useState, useMemo } from 'react';
import { Shop, Product, Order, OrderStatus, BADALGACHHI_UNIONS, PRODUCT_CATEGORIES } from '../types';
import { PlatformSettings } from '../services/store';
import { FirebaseArchitectureModal } from './FirebaseArchitectureModal';
import {
  ShieldCheck,
  CheckCircle2,
  XCircle,
  Clock,
  Store,
  Phone,
  Mail,
  MapPin,
  Lock,
  LogOut,
  X,
  AlertTriangle,
  TrendingUp,
  Package,
  ShoppingBag,
  Plus,
  Edit2,
  Trash2,
  Search,
  Sliders,
  Database,
  Save,
  RotateCcw,
  ExternalLink,
  Tag,
  DollarSign,
  AlertCircle,
  Eye,
  Check,
} from 'lucide-react';

interface AdminModalProps {
  isOpen: boolean;
  onClose: () => void;
  isAdminLoggedIn: boolean;
  onLoginAdmin: (email: string, pass: string) => { success: boolean; message?: string };
  onLogoutAdmin: () => void;
  shops: Shop[];
  products: Product[];
  orders: Order[];
  onApproveShop: (shopId: string) => void;
  onRejectShop: (shopId: string) => void;
  onDeleteShop: (shopId: string) => void;
  onUpdateShop?: (shopId: string, updates: Partial<Shop>) => void;
  onAddShopDirect?: (
    shop: Omit<Shop, 'shopId' | 'createdAt' | 'rating' | 'totalReviews'> & {
      status?: 'Active' | 'Pending' | 'Rejected';
    }
  ) => void;
  onAddProduct?: (product: Omit<Product, 'productId' | 'createdAt'>) => void;
  onUpdateProduct?: (productId: string, updates: Partial<Product>) => void;
  onDeleteProduct?: (productId: string) => void;
  onUpdateOrderStatus?: (orderId: string, status: OrderStatus) => void;
  onDeleteOrder?: (orderId: string) => void;
  platformSettings?: PlatformSettings;
  onUpdatePlatformSettings?: (updates: Partial<PlatformSettings>) => void;
  onSelectShopForDashboard?: (shopId: string) => void;
  onResetToDefaults?: () => void;
}

export const AdminModal: React.FC<AdminModalProps> = ({
  isOpen,
  onClose,
  isAdminLoggedIn,
  onLoginAdmin,
  onLogoutAdmin,
  shops,
  products,
  orders,
  onApproveShop,
  onRejectShop,
  onDeleteShop,
  onUpdateShop,
  onAddShopDirect,
  onAddProduct,
  onUpdateProduct,
  onDeleteProduct,
  onUpdateOrderStatus,
  onDeleteOrder,
  platformSettings,
  onUpdatePlatformSettings,
  onSelectShopForDashboard,
  onResetToDefaults,
}) => {
  // Login form state
  const [adminEmail, setAdminEmail] = useState('');
  const [adminPassword, setAdminPassword] = useState('');
  const [loginError, setLoginError] = useState('');
  const [activeTab, setActiveTab] = useState<'shops' | 'products' | 'orders' | 'settings' | 'database'>('shops');
  const [actionSuccessMsg, setActionSuccessMsg] = useState('');

  // Reset login fields whenever modal opens in logged-out state
  React.useEffect(() => {
    if (isOpen && !isAdminLoggedIn) {
      setAdminEmail('');
      setAdminPassword('');
      setLoginError('');
    }
  }, [isOpen, isAdminLoggedIn]);

  // Shops sub-tab & search
  const [shopFilter, setShopFilter] = useState<'all' | 'pending' | 'active' | 'rejected'>('all');
  const [shopSearch, setShopSearch] = useState('');
  const [editingShop, setEditingShop] = useState<Shop | null>(null);
  const [isAddShopOpen, setIsAddShopOpen] = useState(false);

  // New shop direct form state
  const [newShopForm, setNewShopForm] = useState({
    shopName: '',
    ownerName: '',
    phone: '',
    email: '',
    union: BADALGACHHI_UNIONS[0] as string,
    address: '',
    category: 'মিষ্টি, দই ও বেকারি',
    description: '',
    logoUrl: '',
    status: 'Active' as 'Active' | 'Pending',
  });

  // Product Master sub-state
  const [productSearch, setProductSearch] = useState('');
  const [productShopFilter, setProductShopFilter] = useState('');
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [isAddProductOpen, setIsAddProductOpen] = useState(false);

  // New Product form state
  const [newProductForm, setNewProductForm] = useState({
    shopId: shops[0]?.shopId || '',
    productName: '',
    category: 'মিষ্টি, দই ও বেকারি',
    price: 150,
    discountPrice: 0,
    unit: 'কেজি',
    stock: 25,
    description: '',
    imageUrl: 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=500&auto=format&fit=crop&q=60',
  });

  // Orders sub-state
  const [orderSearch, setOrderSearch] = useState('');
  const [orderStatusFilter, setOrderStatusFilter] = useState<string>('all');

  // Platform settings form state
  const [settingsForm, setSettingsForm] = useState({
    announcementText: platformSettings?.announcementText || 'বদলগাছী উপজেলার ৮টি ইউনিয়নে দ্রুততম হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা!',
    heroHeadline: platformSettings?.heroHeadline || 'ঘরের কাছে সেরা পণ্য, আমার দোকান এ সরাসরি অর্ডার!',
    heroSubheadline: platformSettings?.heroSubheadline || 'ঐতিহাসিক পাহাড়পুর থেকে শুরু করে কোলা, বালুভরা ও সদর ইউনিয়নের বিশ্বস্ত উদ্যোক্তাদের তৈরি খাঁটি মিষ্টি, হস্তশিল্প, তাজা কৃষিপণ্য ও গ্রোসারি।',
    helplinePhone: platformSettings?.helplinePhone || '01755383039',
    whatsappNumber: platformSettings?.whatsappNumber || '01755383039',
    deliveryCharge: platformSettings?.deliveryCharge || 30,
    minFreeDeliveryAmount: platformSettings?.minFreeDeliveryAmount || 500,
    commissionPercent: platformSettings?.commissionPercent || 5,
  });

  // Keep settings form in sync when platformSettings changes
  React.useEffect(() => {
    if (platformSettings) {
      setSettingsForm({
        announcementText: platformSettings.announcementText,
        heroHeadline: platformSettings.heroHeadline,
        heroSubheadline: platformSettings.heroSubheadline,
        helplinePhone: platformSettings.helplinePhone,
        whatsappNumber: platformSettings.whatsappNumber,
        deliveryCharge: platformSettings.deliveryCharge,
        minFreeDeliveryAmount: platformSettings.minFreeDeliveryAmount,
        commissionPercent: platformSettings.commissionPercent,
      });
    }
  }, [platformSettings]);

  // Create a shop map for instant name lookup (Hooks must run unconditionally)
  const shopMap = useMemo(() => new Map(shops.map((s) => [s.shopId, s])), [shops]);

  // Filtered shops
  const filteredShops = useMemo(() => {
    return shops.filter((s) => {
      if (shopFilter !== 'all' && s.status.toLowerCase() !== shopFilter) return false;
      if (shopSearch) {
        const q = shopSearch.toLowerCase();
        return (
          s.shopName.toLowerCase().includes(q) ||
          s.ownerName.toLowerCase().includes(q) ||
          s.phone.includes(q) ||
          s.email.toLowerCase().includes(q) ||
          s.union.toLowerCase().includes(q)
        );
      }
      return true;
    });
  }, [shops, shopFilter, shopSearch]);

  // Filtered products
  const filteredProducts = useMemo(() => {
    return products.filter((p) => {
      if (productShopFilter && p.shopId !== productShopFilter) return false;
      if (productSearch) {
        const q = productSearch.toLowerCase();
        const shop = shopMap.get(p.shopId);
        return (
          p.productName.toLowerCase().includes(q) ||
          p.category.toLowerCase().includes(q) ||
          (shop?.shopName || '').toLowerCase().includes(q)
        );
      }
      return true;
    });
  }, [products, productShopFilter, productSearch, shopMap]);

  // Filtered orders
  const filteredOrders = useMemo(() => {
    return orders.filter((o) => {
      if (orderStatusFilter !== 'all' && o.orderStatus !== orderStatusFilter) return false;
      if (orderSearch) {
        const q = orderSearch.toLowerCase();
        return (
          o.orderId.toLowerCase().includes(q) ||
          o.customerName.toLowerCase().includes(q) ||
          o.customerPhone.includes(q) ||
          (o.shopName || '').toLowerCase().includes(q)
        );
      }
      return true;
    });
  }, [orders, orderStatusFilter, orderSearch]);

  const pendingCount = shops.filter((s) => s.status === 'Pending').length;
  const activeCount = shops.filter((s) => s.status === 'Active').length;

  const totalPlatformEarnings = orders
    .filter((o) => o.orderStatus !== 'Cancelled')
    .reduce((sum, o) => sum + (o.platformCommission || 0), 0);

  const totalSalesVolume = orders
    .filter((o) => o.orderStatus !== 'Cancelled')
    .reduce((sum, o) => sum + o.totalAmount, 0);

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setLoginError('');
    const res = onLoginAdmin(adminEmail, adminPassword);
    if (!res.success) {
      setLoginError(res.message || 'ভুল ইমেইল বা পাসওয়ার্ড!');
    }
  };

  const showSuccess = (msg: string) => {
    setActionSuccessMsg(msg);
    setTimeout(() => setActionSuccessMsg(''), 3500);
  };

  // Approve pending shop
  const handleApprove = (shop: Shop) => {
    onApproveShop(shop.shopId);
    showSuccess(`"${shop.shopName}" অনুমোদিত হয়েছে! এখন দোকানদার ${shop.email} দিয়ে লগইন করতে পারবেন।`);
  };

  // Reject pending shop
  const handleReject = (shop: Shop) => {
    if (confirm(`আপনি কি নিশ্চিতভাবে "${shop.shopName}" এর আবেদনটি বাতিল করতে চান?`)) {
      onRejectShop(shop.shopId);
      showSuccess(`"${shop.shopName}" এর আবেদন বাতিল করা হয়েছে।`);
    }
  };

  // Delete shop
  const handleDeleteShop = (shop: Shop) => {
    if (confirm(`সাবধান! "${shop.shopName}" দোকানটি স্থায়ীভাবে মুছে ফেলতে চান?`)) {
      onDeleteShop(shop.shopId);
      showSuccess(`দোকান "${shop.shopName}" স্থায়ীভাবে মুছে ফেলা হয়েছে।`);
    }
  };

  // Save edited shop
  const handleSaveShopEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingShop || !onUpdateShop) return;
    onUpdateShop(editingShop.shopId, editingShop);
    setEditingShop(null);
    showSuccess(`"${editingShop.shopName}" এর তথ্য সফলভাবে আপডেট করা হয়েছে।`);
  };

  // Add shop directly
  const handleAddShopDirect = (e: React.FormEvent) => {
    e.preventDefault();
    if (!onAddShopDirect) return;
    onAddShopDirect({
      ...newShopForm,
      upazila: 'বদলগাছী',
      district: 'নওগাঁ',
    });
    setIsAddShopOpen(false);
    setNewShopForm({
      shopName: '',
      ownerName: '',
      phone: '',
      email: '',
      union: BADALGACHHI_UNIONS[0],
      address: '',
      category: 'মিষ্টি, দই ও বেকারি',
      description: '',
      logoUrl: '',
      status: 'Active',
    });
    showSuccess(`নতুন দোকান "${newShopForm.shopName}" সফলভাবে প্ল্যাটফর্মে যুক্ত করা হয়েছে!`);
  };

  // Save edited product
  const handleSaveProductEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingProduct || !onUpdateProduct) return;
    onUpdateProduct(editingProduct.productId, editingProduct);
    setEditingProduct(null);
    showSuccess(`"${editingProduct.productName}" এর তথ্য আপডেট সম্পন্ন হয়েছে।`);
  };

  // Add product directly
  const handleAddProductDirect = (e: React.FormEvent) => {
    e.preventDefault();
    if (!onAddProduct) return;
    const targetShop = shops.find((s) => s.shopId === newProductForm.shopId) || shops[0];
    onAddProduct({
      shopId: targetShop.shopId,
      productName: newProductForm.productName,
      category: newProductForm.category,
      price: newProductForm.price,
      discountPrice: newProductForm.discountPrice > 0 ? newProductForm.discountPrice : undefined,
      unit: newProductForm.unit,
      stock: newProductForm.stock,
      description: newProductForm.description,
      imageUrl: newProductForm.imageUrl,
    });
    setIsAddProductOpen(false);
    setNewProductForm({
      shopId: shops[0]?.shopId || '',
      productName: '',
      category: 'মিষ্টি, দই ও বেকারি',
      price: 150,
      discountPrice: 0,
      unit: 'কেজি',
      stock: 25,
      description: '',
      imageUrl: 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=500&auto=format&fit=crop&q=60',
    });
    showSuccess('নতুন পণ্য সফলভাবে ইনভেন্টরিতে আপলোড হয়েছে!');
  };

  // Delete product
  const handleDeleteProduct = (prod: Product) => {
    if (confirm(`আপনি কি "${prod.productName}" পণ্যটি মুছে ফেলতে চান?`)) {
      if (onDeleteProduct) onDeleteProduct(prod.productId);
      showSuccess(`পণ্য "${prod.productName}" মুছে ফেলা হয়েছে।`);
    }
  };

  // Save platform settings
  const handleSaveSettings = (e: React.FormEvent) => {
    e.preventDefault();
    if (onUpdatePlatformSettings) {
      onUpdatePlatformSettings(settingsForm);
      showSuccess('প্ল্যাটফর্ম সেটিংস ও সেকশন সফলভাবে সংরক্ষণ করা হয়েছে!');
    }
  };

  // Check isOpen only after all hooks have executed unconditionally
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/75 backdrop-blur-xs flex items-center justify-center p-2 sm:p-4">
      <div className="bg-white w-full max-w-6xl rounded-3xl shadow-2xl overflow-hidden border border-stone-200 relative my-4 animate-in fade-in zoom-in-95 duration-200 max-h-[94vh] flex flex-col">
        {/* Modal Top Header */}
        <div className="p-4 sm:p-5 border-b border-stone-800 flex items-center justify-between bg-stone-900 text-white shrink-0">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-rose-600 text-white shadow-md">
              <ShieldCheck className="w-5 h-5 sm:w-6 sm:h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-base sm:text-lg font-extrabold text-white tracking-wide">
                  অ্যাডমিন মাস্টার প্যানেল (RSTS-BD)
                </h3>
                <span className="bg-rose-500/30 text-rose-300 border border-rose-500/40 text-[10px] font-bold px-2 py-0.5 rounded-full uppercase">
                  Master Control
                </span>
              </div>
              <p className="text-xs text-stone-400">
                আমার দোকান • বদলগাছী, নওগাঁ | সম্পূর্ণ সিস্টেম পরিচালনা, সম্পাদন ও সংযোজন
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {isAdminLoggedIn && (
              <button
                onClick={onLogoutAdmin}
                className="px-3 py-1.5 rounded-xl bg-stone-800 hover:bg-rose-950/80 hover:text-rose-300 text-stone-300 text-xs font-semibold flex items-center gap-1.5 transition-colors border border-stone-700"
              >
                <LogOut className="w-3.5 h-3.5 text-rose-400" />
                <span className="hidden sm:inline">লগআউট</span>
              </button>
            )}
            <button
              onClick={onClose}
              className="p-1.5 rounded-xl hover:bg-stone-800 text-stone-400 hover:text-white transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Action success banner */}
        {actionSuccessMsg && (
          <div className="bg-emerald-600 text-white text-xs px-4 py-2.5 flex items-center gap-2 animate-in fade-in">
            <CheckCircle2 className="w-4 h-4 shrink-0" />
            <span className="font-semibold">{actionSuccessMsg}</span>
          </div>
        )}

        {/* Modal Body */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 bg-stone-50">
          {!isAdminLoggedIn ? (
            /* Login Form */
            <div className="max-w-md mx-auto py-8 space-y-6">
              <div className="text-center space-y-2">
                <div className="w-14 h-14 rounded-3xl bg-rose-100 text-rose-600 flex items-center justify-center mx-auto shadow-inner">
                  <Lock className="w-7 h-7" />
                </div>
                <h4 className="text-lg font-bold text-stone-900">
                  সুপার অ্যাডমিন সিকিউরিটি ভেরিফিকেশন
                </h4>
                <p className="text-xs text-stone-500">
                  বদলগাছী প্ল্যাটফর্মের সকল দোকান, পণ্য, অর্ডার ও কনফিগারেশন সম্পাদনা করতে অনুমোদিত ক্রেডেনশিয়াল দিন।
                </p>
              </div>

              {loginError && (
                <div className="p-3 rounded-2xl bg-rose-50 border border-rose-200 text-rose-900 text-xs flex items-center gap-2">
                  <AlertTriangle className="w-4 h-4 text-rose-600 shrink-0" />
                  <span>{loginError}</span>
                </div>
              )}

              <form onSubmit={handleLogin} className="space-y-4 bg-white p-6 rounded-3xl border border-stone-200 shadow-sm">
                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    অ্যাডমিন ইমেইল
                  </label>
                  <div className="relative">
                    <input
                      type="email"
                      required
                      placeholder="rstsbd@gmail.com"
                      value={adminEmail}
                      onChange={(e) => setAdminEmail(e.target.value)}
                      className="w-full pl-9 pr-3 py-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500 font-mono"
                    />
                    <Mail className="w-4 h-4 text-stone-400 absolute left-3 top-3" />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    অ্যাডমিন পাসওয়ার্ড
                  </label>
                  <div className="relative">
                    <input
                      type="password"
                      required
                      placeholder="পাসওয়ার্ড লিখুন (যেমন: rstsbd1234)"
                      value={adminPassword}
                      onChange={(e) => setAdminPassword(e.target.value)}
                      className="w-full pl-9 pr-3 py-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500 font-mono"
                    />
                    <Lock className="w-4 h-4 text-stone-400 absolute left-3 top-3" />
                  </div>
                </div>

                <button
                  type="submit"
                  className="w-full py-2.5 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-xl transition-colors shadow-md flex items-center justify-center gap-2 cursor-pointer"
                >
                  <ShieldCheck className="w-4 h-4" />
                  <span>অ্যাডমিন প্যানেলে প্রবেশ করুন</span>
                </button>
              </form>

              <div className="p-3.5 rounded-2xl bg-stone-100 border border-stone-200 text-xs text-stone-600 flex flex-col sm:flex-row items-center justify-between gap-2">
                <div>
                  <span className="font-semibold text-stone-800">🔐 অ্যাডমিন লগইন তথ্য:</span>{' '}
                  <span className="font-mono text-stone-900 bg-stone-200/80 px-1 py-0.5 rounded">rstsbd@gmail.com</span> /{' '}
                  <span className="font-mono text-stone-900 bg-stone-200/80 px-1 py-0.5 rounded">rstsbd1234</span>
                </div>
                <button
                  type="button"
                  onClick={() => {
                    setAdminEmail('rstsbd@gmail.com');
                    setAdminPassword('rstsbd1234');
                  }}
                  className="px-2.5 py-1 text-[11px] font-bold text-rose-700 bg-rose-50 hover:bg-rose-100 border border-rose-200 rounded-lg transition-colors cursor-pointer shrink-0"
                >
                  ডেমো তথ্য বসান
                </button>
              </div>
            </div>
          ) : (
            /* Logged-In Super Admin Dashboard */
            <div className="space-y-6">
              {/* Top Stats Overview */}
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 sm:gap-4">
                <div className="p-4 bg-white rounded-2xl border border-stone-200 shadow-xs">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-medium text-stone-500">পেন্ডিং আবেদন</span>
                    <Clock className="w-4 h-4 text-amber-500" />
                  </div>
                  <p className="text-2xl font-black text-amber-600 mt-1">{pendingCount}</p>
                  <span className="text-[10px] text-stone-400">অনুমোদনের অপেক্ষায়</span>
                </div>

                <div className="p-4 bg-white rounded-2xl border border-stone-200 shadow-xs">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-medium text-stone-500">সক্রিয় দোকান</span>
                    <Store className="w-4 h-4 text-emerald-600" />
                  </div>
                  <p className="text-2xl font-black text-emerald-700 mt-1">{activeCount}</p>
                  <span className="text-[10px] text-stone-400">৮টি ইউনিয়নে চালু</span>
                </div>

                <div className="p-4 bg-white rounded-2xl border border-stone-200 shadow-xs">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-medium text-stone-500">মোট পণ্য তালিকা</span>
                    <Package className="w-4 h-4 text-teal-600" />
                  </div>
                  <p className="text-2xl font-black text-stone-900 mt-1">{products.length}</p>
                  <span className="text-[10px] text-stone-400">ইনভেন্টরি আইটেম</span>
                </div>

                <div className="p-4 bg-white rounded-2xl border border-stone-200 shadow-xs">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-medium text-stone-500">প্ল্যাটফর্ম কমিশন (৫%)</span>
                    <TrendingUp className="w-4 h-4 text-rose-600" />
                  </div>
                  <p className="text-2xl font-black text-rose-600 mt-1">৳{totalPlatformEarnings.toFixed(0)}</p>
                  <span className="text-[10px] text-stone-400">বিক্রয়: ৳{totalSalesVolume.toFixed(0)}</span>
                </div>
              </div>

              {/* Navigation Tabs */}
              <div className="flex flex-wrap items-center gap-1 sm:gap-2 p-1.5 bg-stone-200/70 rounded-2xl text-xs font-bold border border-stone-300">
                <button
                  onClick={() => setActiveTab('shops')}
                  className={`px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
                    activeTab === 'shops'
                      ? 'bg-white text-rose-700 shadow-xs font-extrabold'
                      : 'text-stone-600 hover:text-stone-900'
                  }`}
                >
                  <Store className="w-4 h-4" />
                  <span>দোকান পরিচালনা</span>
                  {pendingCount > 0 && (
                    <span className="px-1.5 py-0.2 bg-amber-500 text-white rounded-full text-[10px]">
                      {pendingCount}
                    </span>
                  )}
                </button>

                <button
                  onClick={() => setActiveTab('products')}
                  className={`px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
                    activeTab === 'products'
                      ? 'bg-white text-rose-700 shadow-xs font-extrabold'
                      : 'text-stone-600 hover:text-stone-900'
                  }`}
                >
                  <Package className="w-4 h-4" />
                  <span>পণ্য মাস্টার ও আপলোড ({products.length})</span>
                </button>

                <button
                  onClick={() => setActiveTab('orders')}
                  className={`px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
                    activeTab === 'orders'
                      ? 'bg-white text-rose-700 shadow-xs font-extrabold'
                      : 'text-stone-600 hover:text-stone-900'
                  }`}
                >
                  <ShoppingBag className="w-4 h-4" />
                  <span>অর্ডার ও কমিশন ({orders.length})</span>
                </button>

                <button
                  onClick={() => setActiveTab('settings')}
                  className={`px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
                    activeTab === 'settings'
                      ? 'bg-white text-rose-700 shadow-xs font-extrabold'
                      : 'text-stone-600 hover:text-stone-900'
                  }`}
                >
                  <Sliders className="w-4 h-4" />
                  <span>সেকশন ও সেটিংস সম্পাদনা</span>
                </button>

                <button
                  onClick={() => setActiveTab('database')}
                  className={`px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
                    activeTab === 'database'
                      ? 'bg-white text-rose-700 shadow-xs font-extrabold'
                      : 'text-stone-600 hover:text-stone-900'
                  }`}
                >
                  <Database className="w-4 h-4 text-emerald-600" />
                  <span>ডাটাবেজ ও স্কিমা</span>
                </button>
              </div>

              {/* TAB 1: SHOPS MANAGEMENT */}
              {activeTab === 'shops' && (
                <div className="space-y-4">
                  {/* Action Bar */}
                  <div className="bg-white p-4 rounded-2xl border border-stone-200 flex flex-wrap items-center justify-between gap-3 shadow-xs">
                    <div className="flex flex-wrap items-center gap-2">
                      <div className="relative">
                        <input
                          type="text"
                          value={shopSearch}
                          onChange={(e) => setShopSearch(e.target.value)}
                          placeholder="দোকান বা মালিকের নাম খুঁজুন..."
                          className="pl-8 pr-3 py-1.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500 w-56"
                        />
                        <Search className="w-3.5 h-3.5 text-stone-400 absolute left-2.5 top-2" />
                      </div>

                      {/* Status Filter buttons */}
                      <div className="flex items-center gap-1 bg-stone-100 p-1 rounded-xl text-xs font-medium">
                        {(['all', 'pending', 'active', 'rejected'] as const).map((st) => (
                          <button
                            key={st}
                            onClick={() => setShopFilter(st)}
                            className={`px-2.5 py-1 rounded-lg transition-colors capitalize ${
                              shopFilter === st
                                ? 'bg-white text-rose-700 font-bold shadow-xs'
                                : 'text-stone-600 hover:text-stone-900'
                            }`}
                          >
                            {st === 'all'
                              ? 'সকল'
                              : st === 'pending'
                              ? `অপেক্ষমাণ (${pendingCount})`
                              : st === 'active'
                              ? `সক্রিয় (${activeCount})`
                              : 'বাতিল'}
                          </button>
                        ))}
                      </div>
                    </div>

                    <button
                      onClick={() => setIsAddShopOpen(true)}
                      className="px-3 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 transition-colors shadow-xs"
                    >
                      <Plus className="w-4 h-4" />
                      <span>নতুন দোকান সংযোজন</span>
                    </button>
                  </div>

                  {/* Add Shop Modal */}
                  {isAddShopOpen && (
                    <div className="bg-white p-5 rounded-2xl border-2 border-emerald-500 shadow-md animate-in fade-in">
                      <div className="flex items-center justify-between mb-3 border-b pb-2">
                        <h4 className="text-sm font-bold text-stone-900 flex items-center gap-2">
                          <Plus className="w-4 h-4 text-emerald-600" />
                          <span>এডমিন কর্তৃক সরাসরি নতুন দোকান সংযোজন</span>
                        </h4>
                        <button
                          onClick={() => setIsAddShopOpen(false)}
                          className="text-stone-400 hover:text-stone-600"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </div>

                      <form onSubmit={handleAddShopDirect} className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">দোকানের নাম *</label>
                          <input
                            type="text"
                            required
                            value={newShopForm.shopName}
                            onChange={(e) => setNewShopForm({ ...newShopForm, shopName: e.target.value })}
                            placeholder="যেমন: বদলগাছী মিষ্টান্ন ভান্ডার"
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">মালিকের নাম *</label>
                          <input
                            type="text"
                            required
                            value={newShopForm.ownerName}
                            onChange={(e) => setNewShopForm({ ...newShopForm, ownerName: e.target.value })}
                            placeholder="যেমন: মো: রফিকুল ইসলাম"
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">মোবাইল নম্বর *</label>
                          <input
                            type="tel"
                            required
                            value={newShopForm.phone}
                            onChange={(e) => setNewShopForm({ ...newShopForm, phone: e.target.value })}
                            placeholder="017XXXXXXXX"
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ইমেইল (লগইন আইডি) *</label>
                          <input
                            type="email"
                            required
                            value={newShopForm.email}
                            onChange={(e) => setNewShopForm({ ...newShopForm, email: e.target.value })}
                            placeholder="vendor@amardokan.bd"
                            className="w-full p-2 bg-stone-50 border rounded-xl font-mono"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ইউনিয়ন *</label>
                          <select
                            value={newShopForm.union}
                            onChange={(e) => setNewShopForm({ ...newShopForm, union: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          >
                            {BADALGACHHI_UNIONS.map((u) => (
                              <option key={u} value={u}>
                                {u}
                              </option>
                            ))}
                          </select>
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ক্যাটাগরি *</label>
                          <select
                            value={newShopForm.category}
                            onChange={(e) => setNewShopForm({ ...newShopForm, category: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          >
                            {PRODUCT_CATEGORIES.filter((c) => c !== 'সব ক্যাটাগরি').map((c) => (
                              <option key={c} value={c}>
                                {c}
                              </option>
                            ))}
                          </select>
                        </div>

                        <div className="sm:col-span-2">
                          <label className="block font-semibold text-stone-700 mb-1">দোকানের ঠিকানা</label>
                          <input
                            type="text"
                            value={newShopForm.address}
                            onChange={(e) => setNewShopForm({ ...newShopForm, address: e.target.value })}
                            placeholder="বাজার রোড, বদলগাছী"
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">লোগো / ছবি URL</label>
                          <input
                            type="url"
                            value={newShopForm.logoUrl}
                            onChange={(e) => setNewShopForm({ ...newShopForm, logoUrl: e.target.value })}
                            placeholder="https://..."
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div className="sm:col-span-3 flex justify-end gap-2 pt-2 border-t">
                          <button
                            type="button"
                            onClick={() => setIsAddShopOpen(false)}
                            className="px-3 py-1.5 bg-stone-200 hover:bg-stone-300 rounded-xl font-semibold text-stone-700"
                          >
                            বাতিল
                          </button>
                          <button
                            type="submit"
                            className="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold flex items-center gap-1.5 shadow-xs"
                          >
                            <Check className="w-4 h-4" />
                            <span>দোকান সংরক্ষণ করুন</span>
                          </button>
                        </div>
                      </form>
                    </div>
                  )}

                  {/* Edit Shop Modal */}
                  {editingShop && (
                    <div className="bg-white p-5 rounded-2xl border-2 border-rose-500 shadow-md animate-in fade-in">
                      <div className="flex items-center justify-between mb-3 border-b pb-2">
                        <h4 className="text-sm font-bold text-stone-900 flex items-center gap-2">
                          <Edit2 className="w-4 h-4 text-rose-600" />
                          <span>দোকানের তথ্য সম্পাদনা: {editingShop.shopName}</span>
                        </h4>
                        <button
                          onClick={() => setEditingShop(null)}
                          className="text-stone-400 hover:text-stone-600"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </div>

                      <form onSubmit={handleSaveShopEdit} className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">দোকানের নাম</label>
                          <input
                            type="text"
                            required
                            value={editingShop.shopName}
                            onChange={(e) => setEditingShop({ ...editingShop, shopName: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">মালিকের নাম</label>
                          <input
                            type="text"
                            required
                            value={editingShop.ownerName}
                            onChange={(e) => setEditingShop({ ...editingShop, ownerName: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">মোবাইল নম্বর</label>
                          <input
                            type="tel"
                            required
                            value={editingShop.phone}
                            onChange={(e) => setEditingShop({ ...editingShop, phone: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ইমেইল (লগইন)</label>
                          <input
                            type="email"
                            required
                            value={editingShop.email}
                            onChange={(e) => setEditingShop({ ...editingShop, email: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-mono"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ইউনিয়ন</label>
                          <select
                            value={editingShop.union}
                            onChange={(e) => setEditingShop({ ...editingShop, union: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          >
                            {BADALGACHHI_UNIONS.map((u) => (
                              <option key={u} value={u}>
                                {u}
                              </option>
                            ))}
                          </select>
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">স্ট্যাটাস</label>
                          <select
                            value={editingShop.status}
                            onChange={(e) =>
                              setEditingShop({
                                ...editingShop,
                                status: e.target.value as 'Active' | 'Pending' | 'Rejected',
                              })
                            }
                            className="w-full p-2 bg-stone-50 border rounded-xl font-bold text-stone-800"
                          >
                            <option value="Active">সক্রিয় (Active)</option>
                            <option value="Pending">অপেক্ষমাণ (Pending)</option>
                            <option value="Rejected">বাতিল (Rejected)</option>
                          </select>
                        </div>

                        <div className="sm:col-span-2">
                          <label className="block font-semibold text-stone-700 mb-1">ঠিকানা</label>
                          <input
                            type="text"
                            value={editingShop.address || ''}
                            onChange={(e) => setEditingShop({ ...editingShop, address: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ক্যাটাগরি</label>
                          <select
                            value={editingShop.category}
                            onChange={(e) => setEditingShop({ ...editingShop, category: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          >
                            {PRODUCT_CATEGORIES.filter((c) => c !== 'সব ক্যাটাগরি').map((c) => (
                              <option key={c} value={c}>
                                {c}
                              </option>
                            ))}
                          </select>
                        </div>

                        <div className="sm:col-span-3 flex justify-end gap-2 pt-2 border-t">
                          <button
                            type="button"
                            onClick={() => setEditingShop(null)}
                            className="px-3 py-1.5 bg-stone-200 hover:bg-stone-300 rounded-xl font-semibold text-stone-700"
                          >
                            বাতিল
                          </button>
                          <button
                            type="submit"
                            className="px-4 py-1.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl font-bold flex items-center gap-1.5 shadow-xs"
                          >
                            <Save className="w-4 h-4" />
                            <span>আপডেট সংরক্ষণ করুন</span>
                          </button>
                        </div>
                      </form>
                    </div>
                  )}

                  {/* Shops List Cards */}
                  <div className="space-y-3">
                    {filteredShops.length === 0 ? (
                      <div className="p-8 text-center text-stone-500 bg-white rounded-2xl border">
                        কোনো দোকান পাওয়া যায়নি।
                      </div>
                    ) : (
                      filteredShops.map((shop) => (
                        <div
                          key={shop.shopId}
                          className={`p-4 rounded-2xl border transition-all ${
                            shop.status === 'Pending'
                              ? 'bg-amber-50/70 border-amber-300 shadow-xs'
                              : shop.status === 'Active'
                              ? 'bg-white border-stone-200 shadow-xs'
                              : 'bg-rose-50/50 border-rose-200 opacity-75'
                          }`}
                        >
                          <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
                            <div className="space-y-1">
                              <div className="flex flex-wrap items-center gap-2">
                                <h4 className="text-sm font-bold text-stone-900">{shop.shopName}</h4>
                                <span
                                  className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                                    shop.status === 'Active'
                                      ? 'bg-emerald-100 text-emerald-800'
                                      : shop.status === 'Pending'
                                      ? 'bg-amber-100 text-amber-900 border border-amber-300'
                                      : 'bg-rose-100 text-rose-800'
                                  }`}
                                >
                                  {shop.status === 'Active'
                                    ? 'সক্রিয়'
                                    : shop.status === 'Pending'
                                    ? '⏳ অনুমোদনের অপেক্ষায়'
                                    : 'বাতিলকৃত'}
                                </span>
                                <span className="text-[11px] bg-stone-100 text-stone-600 px-2 py-0.5 rounded-md">
                                  {shop.category}
                                </span>
                              </div>

                              <div className="flex flex-wrap items-center gap-3 text-xs text-stone-500">
                                <span className="flex items-center gap-1 font-medium text-stone-700">
                                  <span>প্রোপাইটর: {shop.ownerName}</span>
                                </span>
                                <span>•</span>
                                <span className="flex items-center gap-1 font-mono text-emerald-700">
                                  <Phone className="w-3 h-3" />
                                  <span>{shop.phone}</span>
                                </span>
                                <span>•</span>
                                <span className="flex items-center gap-1 font-mono text-stone-600">
                                  <Mail className="w-3 h-3" />
                                  <span>{shop.email}</span>
                                </span>
                                <span>•</span>
                                <span className="flex items-center gap-1 text-stone-600">
                                  <MapPin className="w-3 h-3 text-emerald-600" />
                                  <span>{shop.union}, বদলগাছী</span>
                                </span>
                              </div>
                            </div>

                            {/* Action Buttons */}
                            <div className="flex flex-wrap items-center gap-1.5 shrink-0 pt-2 md:pt-0">
                              {shop.status === 'Pending' && (
                                <>
                                  <button
                                    onClick={() => handleApprove(shop)}
                                    className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold flex items-center gap-1 transition-colors shadow-xs"
                                  >
                                    <CheckCircle2 className="w-3.5 h-3.5" />
                                    <span>অনুমোদন করুন</span>
                                  </button>
                                  <button
                                    onClick={() => handleReject(shop)}
                                    className="px-2.5 py-1.5 bg-rose-100 hover:bg-rose-200 text-rose-800 rounded-xl text-xs font-bold flex items-center gap-1 transition-colors"
                                  >
                                    <XCircle className="w-3.5 h-3.5" />
                                    <span>বাতিল</span>
                                  </button>
                                </>
                              )}

                              <button
                                onClick={() => setEditingShop(shop)}
                                className="px-2.5 py-1.5 bg-stone-100 hover:bg-stone-200 text-stone-700 rounded-xl text-xs font-semibold flex items-center gap-1 transition-colors"
                                title="দোকানের তথ্য সম্পাদনা করুন"
                              >
                                <Edit2 className="w-3.5 h-3.5" />
                                <span>সম্পাদনা</span>
                              </button>

                              {onSelectShopForDashboard && (
                                <button
                                  onClick={() => {
                                    onSelectShopForDashboard(shop.shopId);
                                    onClose();
                                  }}
                                  className="px-2.5 py-1.5 bg-stone-900 hover:bg-stone-800 text-white rounded-xl text-xs font-semibold flex items-center gap-1 transition-colors"
                                  title="দোকানের ড্যাশবোর্ডে প্রবেশ করুন"
                                >
                                  <ExternalLink className="w-3.5 h-3.5 text-emerald-400" />
                                  <span className="hidden sm:inline">ড্যাশবোর্ড</span>
                                </button>
                              )}

                              <button
                                onClick={() => handleDeleteShop(shop)}
                                className="p-1.5 text-stone-400 hover:text-rose-600 hover:bg-rose-50 rounded-xl transition-colors"
                                title="দোকান স্থায়ীভাবে মুছে ফেলুন"
                              >
                                <Trash2 className="w-4 h-4" />
                              </button>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}

              {/* TAB 2: PRODUCTS MASTER & UPLOAD */}
              {activeTab === 'products' && (
                <div className="space-y-4">
                  {/* Action Bar */}
                  <div className="bg-white p-4 rounded-2xl border border-stone-200 flex flex-wrap items-center justify-between gap-3 shadow-xs">
                    <div className="flex flex-wrap items-center gap-2">
                      <div className="relative">
                        <input
                          type="text"
                          value={productSearch}
                          onChange={(e) => setProductSearch(e.target.value)}
                          placeholder="পণ্য বা ক্যাটাগরি খুঁজুন..."
                          className="pl-8 pr-3 py-1.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500 w-56"
                        />
                        <Search className="w-3.5 h-3.5 text-stone-400 absolute left-2.5 top-2" />
                      </div>

                      {/* Filter by Shop */}
                      <select
                        value={productShopFilter}
                        onChange={(e) => setProductShopFilter(e.target.value)}
                        className="py-1.5 px-3 text-xs bg-stone-50 border border-stone-300 rounded-xl font-medium text-stone-700"
                      >
                        <option value="">সকল দোকান ({products.length} পণ্য)</option>
                        {shops.map((s) => (
                          <option key={s.shopId} value={s.shopId}>
                            🏪 {s.shopName}
                          </option>
                        ))}
                      </select>
                    </div>

                    <button
                      onClick={() => setIsAddProductOpen(true)}
                      className="px-3 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 transition-colors shadow-xs"
                    >
                      <Plus className="w-4 h-4" />
                      <span>নতুন পণ্য আপলোড</span>
                    </button>
                  </div>

                  {/* Add Product Form */}
                  {isAddProductOpen && (
                    <div className="bg-white p-5 rounded-2xl border-2 border-emerald-500 shadow-md animate-in fade-in">
                      <div className="flex items-center justify-between mb-3 border-b pb-2">
                        <h4 className="text-sm font-bold text-stone-900 flex items-center gap-2">
                          <Plus className="w-4 h-4 text-emerald-600" />
                          <span>এডমিন মাস্টার ইনভেন্টরি: নতুন পণ্য আপলোড</span>
                        </h4>
                        <button
                          onClick={() => setIsAddProductOpen(false)}
                          className="text-stone-400 hover:text-stone-600"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </div>

                      <form onSubmit={handleAddProductDirect} className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">কোন দোকানের পণ্য? *</label>
                          <select
                            required
                            value={newProductForm.shopId}
                            onChange={(e) => setNewProductForm({ ...newProductForm, shopId: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-semibold"
                          >
                            {shops.map((s) => (
                              <option key={s.shopId} value={s.shopId}>
                                🏪 {s.shopName} ({s.union})
                              </option>
                            ))}
                          </select>
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">পণ্যের নাম *</label>
                          <input
                            type="text"
                            required
                            value={newProductForm.productName}
                            onChange={(e) => setNewProductForm({ ...newProductForm, productName: e.target.value })}
                            placeholder="যেমন: পোড়ামাটির নকশী ফুলদানি"
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ক্যাটাগরি *</label>
                          <select
                            value={newProductForm.category}
                            onChange={(e) => setNewProductForm({ ...newProductForm, category: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          >
                            {PRODUCT_CATEGORIES.filter((c) => c !== 'সব ক্যাটাগরি').map((c) => (
                              <option key={c} value={c}>
                                {c}
                              </option>
                            ))}
                          </select>
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">মূল্য (টাকা) *</label>
                          <input
                            type="number"
                            required
                            min="1"
                            value={newProductForm.price}
                            onChange={(e) => setNewProductForm({ ...newProductForm, price: Number(e.target.value) })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-bold"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ডিসকাউন্ট মূল্য (ঐচ্ছিক)</label>
                          <input
                            type="number"
                            min="0"
                            value={newProductForm.discountPrice}
                            onChange={(e) => setNewProductForm({ ...newProductForm, discountPrice: Number(e.target.value) })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">পরিমাপ ইউনিট ও স্টক *</label>
                          <div className="flex gap-2">
                            <input
                              type="text"
                              required
                              value={newProductForm.unit}
                              onChange={(e) => setNewProductForm({ ...newProductForm, unit: e.target.value })}
                              placeholder="কেজি/পিস/প্যাকেট"
                              className="w-1/2 p-2 bg-stone-50 border rounded-xl"
                            />
                            <input
                              type="number"
                              required
                              min="0"
                              value={newProductForm.stock}
                              onChange={(e) => setNewProductForm({ ...newProductForm, stock: Number(e.target.value) })}
                              placeholder="স্টক"
                              className="w-1/2 p-2 bg-stone-50 border rounded-xl"
                            />
                          </div>
                        </div>

                        <div className="sm:col-span-2">
                          <label className="block font-semibold text-stone-700 mb-1">পণ্যের ছবির লিংক (URL)</label>
                          <input
                            type="url"
                            value={newProductForm.imageUrl}
                            onChange={(e) => setNewProductForm({ ...newProductForm, imageUrl: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-mono"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">বিবরণ (Description)</label>
                          <input
                            type="text"
                            value={newProductForm.description}
                            onChange={(e) => setNewProductForm({ ...newProductForm, description: e.target.value })}
                            placeholder="খাঁটি ও তাজা..."
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div className="sm:col-span-3 flex justify-end gap-2 pt-2 border-t">
                          <button
                            type="button"
                            onClick={() => setIsAddProductOpen(false)}
                            className="px-3 py-1.5 bg-stone-200 hover:bg-stone-300 rounded-xl font-semibold text-stone-700"
                          >
                            বাতিল
                          </button>
                          <button
                            type="submit"
                            className="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold flex items-center gap-1.5 shadow-xs"
                          >
                            <Check className="w-4 h-4" />
                            <span>পণ্য আপলোড সম্পন্ন করুন</span>
                          </button>
                        </div>
                      </form>
                    </div>
                  )}

                  {/* Edit Product Form */}
                  {editingProduct && (
                    <div className="bg-white p-5 rounded-2xl border-2 border-rose-500 shadow-md animate-in fade-in">
                      <div className="flex items-center justify-between mb-3 border-b pb-2">
                        <h4 className="text-sm font-bold text-stone-900 flex items-center gap-2">
                          <Edit2 className="w-4 h-4 text-rose-600" />
                          <span>পণ্য সম্পাদনা: {editingProduct.productName}</span>
                        </h4>
                        <button
                          onClick={() => setEditingProduct(null)}
                          className="text-stone-400 hover:text-stone-600"
                        >
                          <X className="w-4 h-4" />
                        </button>
                      </div>

                      <form onSubmit={handleSaveProductEdit} className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">পণ্যের নাম</label>
                          <input
                            type="text"
                            required
                            value={editingProduct.productName}
                            onChange={(e) => setEditingProduct({ ...editingProduct, productName: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-bold"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ক্যাটাগরি</label>
                          <select
                            value={editingProduct.category}
                            onChange={(e) => setEditingProduct({ ...editingProduct, category: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          >
                            {PRODUCT_CATEGORIES.filter((c) => c !== 'সব ক্যাটাগরি').map((c) => (
                              <option key={c} value={c}>
                                {c}
                              </option>
                            ))}
                          </select>
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">মূল্য (টাকা)</label>
                          <input
                            type="number"
                            required
                            value={editingProduct.price}
                            onChange={(e) => setEditingProduct({ ...editingProduct, price: Number(e.target.value) })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-bold text-stone-900"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">ডিসকাউন্ট মূল্য (টাকা)</label>
                          <input
                            type="number"
                            value={editingProduct.discountPrice || ''}
                            onChange={(e) =>
                              setEditingProduct({
                                ...editingProduct,
                                discountPrice: e.target.value ? Number(e.target.value) : undefined,
                              })
                            }
                            placeholder="ডিসকাউন্ট না থাকলে ফাঁকা রাখুন"
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">স্টক সংখ্যা</label>
                          <input
                            type="number"
                            required
                            value={editingProduct.stock}
                            onChange={(e) => setEditingProduct({ ...editingProduct, stock: Number(e.target.value) })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-bold"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">পরিমাপের ইউনিট</label>
                          <input
                            type="text"
                            required
                            value={editingProduct.unit}
                            onChange={(e) => setEditingProduct({ ...editingProduct, unit: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div className="sm:col-span-2">
                          <label className="block font-semibold text-stone-700 mb-1">ছবির URL</label>
                          <input
                            type="url"
                            value={editingProduct.imageUrl}
                            onChange={(e) => setEditingProduct({ ...editingProduct, imageUrl: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl font-mono text-[11px]"
                          />
                        </div>

                        <div>
                          <label className="block font-semibold text-stone-700 mb-1">বিবরণ</label>
                          <input
                            type="text"
                            value={editingProduct.description || ''}
                            onChange={(e) => setEditingProduct({ ...editingProduct, description: e.target.value })}
                            className="w-full p-2 bg-stone-50 border rounded-xl"
                          />
                        </div>

                        <div className="sm:col-span-3 flex justify-end gap-2 pt-2 border-t">
                          <button
                            type="button"
                            onClick={() => setEditingProduct(null)}
                            className="px-3 py-1.5 bg-stone-200 hover:bg-stone-300 rounded-xl font-semibold text-stone-700"
                          >
                            বাতিল
                          </button>
                          <button
                            type="submit"
                            className="px-4 py-1.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl font-bold flex items-center gap-1.5 shadow-xs"
                          >
                            <Save className="w-4 h-4" />
                            <span>পণ্য আপডেট সংরক্ষণ</span>
                          </button>
                        </div>
                      </form>
                    </div>
                  )}

                  {/* Products Master Table */}
                  <div className="bg-white rounded-2xl border border-stone-200 overflow-hidden shadow-xs">
                    <div className="overflow-x-auto">
                      <table className="w-full text-left text-xs">
                        <thead className="bg-stone-100 text-stone-600 font-bold border-b">
                          <tr>
                            <th className="p-3">পণ্য</th>
                            <th className="p-3">দোকান</th>
                            <th className="p-3">ক্যাটাগরি</th>
                            <th className="p-3">মূল্য</th>
                            <th className="p-3">স্টক</th>
                            <th className="p-3 text-right">অ্যাকশন</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-stone-100 text-stone-700">
                          {filteredProducts.length === 0 ? (
                            <tr>
                              <td colSpan={6} className="p-6 text-center text-stone-400">
                                কোনো পণ্য পাওয়া যায়নি।
                              </td>
                            </tr>
                          ) : (
                            filteredProducts.map((prod) => (
                              <tr key={prod.productId} className="hover:bg-stone-50 transition-colors">
                                <td className="p-3 font-semibold text-stone-900 flex items-center gap-2.5">
                                  <img
                                    src={prod.imageUrl}
                                    alt={prod.productName}
                                    className="w-8 h-8 rounded-lg object-cover shrink-0 border"
                                  />
                                  <span className="truncate max-w-[180px]">{prod.productName}</span>
                                </td>
                                <td className="p-3 text-stone-600 truncate max-w-[140px]">
                                  {shopMap.get(prod.shopId)?.shopName || 'স্থানীয় দোকান'}
                                </td>
                                <td className="p-3">
                                  <span className="bg-stone-100 text-stone-600 px-2 py-0.5 rounded-md text-[10px]">
                                    {prod.category}
                                  </span>
                                </td>
                                <td className="p-3 font-bold text-emerald-700">
                                  ৳{prod.discountPrice ?? prod.price}
                                  {prod.discountPrice && (
                                    <span className="text-stone-400 line-through ml-1.5 font-normal text-[10px]">
                                      ৳{prod.price}
                                    </span>
                                  )}
                                </td>
                                <td className="p-3">
                                  <span
                                    className={`px-2 py-0.5 rounded-full font-bold text-[10px] ${
                                      prod.stock > 5
                                        ? 'bg-emerald-50 text-emerald-700'
                                        : prod.stock > 0
                                        ? 'bg-amber-50 text-amber-700'
                                        : 'bg-rose-50 text-rose-700'
                                    }`}
                                  >
                                    {prod.stock} {prod.unit}
                                  </span>
                                </td>
                                <td className="p-3 text-right whitespace-nowrap">
                                  <button
                                    onClick={() => setEditingProduct(prod)}
                                    className="p-1.5 text-stone-600 hover:text-emerald-700 hover:bg-stone-100 rounded-lg mr-1 transition-colors"
                                    title="সম্পাদনা করুন"
                                  >
                                    <Edit2 className="w-3.5 h-3.5" />
                                  </button>
                                  <button
                                    onClick={() => handleDeleteProduct(prod)}
                                    className="p-1.5 text-stone-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                                    title="মুছে ফেলুন"
                                  >
                                    <Trash2 className="w-3.5 h-3.5" />
                                  </button>
                                </td>
                              </tr>
                            ))
                          )}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}

              {/* TAB 3: ORDERS & COMMISSION */}
              {activeTab === 'orders' && (
                <div className="space-y-4">
                  {/* Action Bar */}
                  <div className="bg-white p-4 rounded-2xl border border-stone-200 flex flex-wrap items-center justify-between gap-3 shadow-xs">
                    <div className="flex flex-wrap items-center gap-2">
                      <div className="relative">
                        <input
                          type="text"
                          value={orderSearch}
                          onChange={(e) => setOrderSearch(e.target.value)}
                          placeholder="অর্ডার আইডি, গ্রাহক বা ফোন..."
                          className="pl-8 pr-3 py-1.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500 w-56"
                        />
                        <Search className="w-3.5 h-3.5 text-stone-400 absolute left-2.5 top-2" />
                      </div>

                      <select
                        value={orderStatusFilter}
                        onChange={(e) => setOrderStatusFilter(e.target.value)}
                        className="py-1.5 px-3 text-xs bg-stone-50 border border-stone-300 rounded-xl font-medium text-stone-700"
                      >
                        <option value="all">সকল স্ট্যাটাস</option>
                        <option value="Pending">Pending (অপেক্ষমাণ)</option>
                        <option value="Confirmed">Confirmed (নিশ্চিত)</option>
                        <option value="Packed">Packed (প্যাকেটজাত)</option>
                        <option value="Shipped">Shipped (কুরিয়ারে)</option>
                        <option value="Delivered">Delivered (বিতরণ সম্পন্ন)</option>
                        <option value="Cancelled">Cancelled (বাতিল)</option>
                      </select>
                    </div>

                    <div className="text-xs text-stone-500 font-medium">
                      মোট অর্ডার: <strong className="text-stone-900">{orders.length}</strong>
                    </div>
                  </div>

                  {/* Orders Table */}
                  <div className="space-y-3">
                    {filteredOrders.length === 0 ? (
                      <div className="p-8 text-center text-stone-500 bg-white rounded-2xl border">
                        কোনো অর্ডার পাওয়া যায়নি।
                      </div>
                    ) : (
                      filteredOrders.map((ord) => (
                        <div key={ord.orderId} className="bg-white p-4 rounded-2xl border border-stone-200 shadow-xs space-y-3">
                          <div className="flex flex-wrap items-center justify-between gap-2 border-b pb-2">
                            <div className="flex items-center gap-2">
                              <span className="font-mono font-bold text-xs text-stone-900">
                                #{ord.orderId}
                              </span>
                              <span className="text-xs text-stone-500">
                                🏪 {ord.shopName}
                              </span>
                              <span className="text-[11px] text-stone-400">
                                {new Date(ord.timestamp).toLocaleDateString('bn-BD', {
                                  day: 'numeric',
                                  month: 'short',
                                  hour: 'numeric',
                                  minute: 'numeric',
                                })}
                              </span>
                            </div>

                            <div className="flex items-center gap-2">
                              <select
                                value={ord.orderStatus}
                                onChange={(e) => {
                                  if (onUpdateOrderStatus) {
                                    onUpdateOrderStatus(ord.orderId, e.target.value as OrderStatus);
                                    showSuccess(`অর্ডার #${ord.orderId} এর স্ট্যাটাস '${e.target.value}' করা হয়েছে।`);
                                  }
                                }}
                                className={`text-xs font-bold px-2 py-1 rounded-xl border ${
                                  ord.orderStatus === 'Delivered'
                                    ? 'bg-emerald-50 text-emerald-800 border-emerald-300'
                                    : ord.orderStatus === 'Cancelled'
                                    ? 'bg-rose-50 text-rose-800 border-rose-300'
                                    : 'bg-amber-50 text-amber-900 border-amber-300'
                                }`}
                              >
                                <option value="Pending">Pending (অপেক্ষমাণ)</option>
                                <option value="Confirmed">Confirmed (নিশ্চিত)</option>
                                <option value="Packed">Packed (প্যাকেটজাত)</option>
                                <option value="Shipped">Shipped (কুরিয়ারে)</option>
                                <option value="Delivered">Delivered (বিতরণ সম্পন্ন)</option>
                                <option value="Cancelled">Cancelled (বাতিল)</option>
                              </select>

                              {onDeleteOrder && (
                                <button
                                  onClick={() => {
                                    if (confirm(`অর্ডার #${ord.orderId} মুছে ফেলতে চান?`)) {
                                      onDeleteOrder(ord.orderId);
                                      showSuccess(`অর্ডার #${ord.orderId} মুছে ফেলা হয়েছে।`);
                                    }
                                  }}
                                  className="p-1.5 text-stone-400 hover:text-rose-600 rounded-lg hover:bg-rose-50 transition-colors"
                                  title="অর্ডার ডিলিট করুন"
                                >
                                  <Trash2 className="w-3.5 h-3.5" />
                                </button>
                              )}
                            </div>
                          </div>

                          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-xs">
                            <div>
                              <span className="text-stone-400 block text-[10px]">গ্রাহকের বিবরণ:</span>
                              <p className="font-bold text-stone-800">{ord.customerName}</p>
                              <p className="font-mono text-emerald-700">{ord.customerPhone}</p>
                              <p className="text-stone-600 mt-0.5">{ord.deliveryAddress}</p>
                            </div>

                            <div>
                              <span className="text-stone-400 block text-[10px]">অর্ডারকৃত আইটেম:</span>
                              <div className="space-y-1 mt-0.5">
                                {ord.items.map((it, idx) => (
                                  <div key={idx} className="flex items-center justify-between text-[11px]">
                                    <span className="truncate max-w-[160px] text-stone-700">
                                      {it.productName} × {it.quantity}
                                    </span>
                                    <span className="font-bold text-stone-900">৳{it.price * it.quantity}</span>
                                  </div>
                                ))}
                              </div>
                            </div>

                            <div className="bg-stone-50 p-3 rounded-xl border border-stone-200 text-right space-y-1">
                              <div className="flex justify-between">
                                <span className="text-stone-500">মোট বিল:</span>
                                <span className="font-black text-sm text-stone-900">৳{ord.totalAmount}</span>
                              </div>
                              <div className="flex justify-between text-[11px] text-rose-600">
                                <span>প্ল্যাটফর্ম কমিশন (৫%):</span>
                                <span className="font-bold">৳{(ord.platformCommission || 0).toFixed(1)}</span>
                              </div>
                              <div className="flex justify-between text-[11px] text-emerald-700 border-t pt-1 font-semibold">
                                <span>দোকানদারের পাওনা:</span>
                                <span>৳{(ord.totalAmount - (ord.platformCommission || 0)).toFixed(1)}</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}

              {/* TAB 4: PLATFORM SECTIONS & CMS SETTINGS */}
              {activeTab === 'settings' && (
                <div className="bg-white p-6 rounded-2xl border border-stone-200 shadow-xs space-y-6">
                  <div className="flex items-center justify-between border-b pb-3">
                    <div>
                      <h4 className="text-sm font-bold text-stone-900 flex items-center gap-2">
                        <Sliders className="w-4 h-4 text-rose-600" />
                        <span>প্ল্যাটফর্মের সকল সেকশন, ব্যানার ও সেটিংস সম্পাদনা</span>
                      </h4>
                      <p className="text-xs text-stone-500">
                        এখানে যা পরিবর্তন করবেন তা সরাসরি হোমপেজ, ব্যানার ও ফুটারে কার্যকর হবে।
                      </p>
                    </div>

                    {onResetToDefaults && (
                      <button
                        onClick={() => {
                          if (confirm('আপনি কি সকল ডেটা ও সেটিংস ডিফল্ট অবস্থায় রিসেট করতে চান?')) {
                            onResetToDefaults();
                            showSuccess('সিস্টেম ফ্যাক্টরি ডিফল্টে সফলভাবে রিসেট করা হয়েছে!');
                          }
                        }}
                        className="px-3 py-1.5 bg-stone-100 hover:bg-stone-200 text-stone-700 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors"
                      >
                        <RotateCcw className="w-3.5 h-3.5 text-stone-500" />
                        <span>ডিফল্ট রিসেট</span>
                      </button>
                    )}
                  </div>

                  <form onSubmit={handleSaveSettings} className="space-y-4 text-xs">
                    {/* Announcement text */}
                    <div>
                      <label className="block font-bold text-stone-800 mb-1">
                        শীর্ষ ঘোষণা ব্যানার বার্তা (Top Micro Bar Notice)
                      </label>
                      <input
                        type="text"
                        value={settingsForm.announcementText}
                        onChange={(e) => setSettingsForm({ ...settingsForm, announcementText: e.target.value })}
                        className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500"
                      />
                    </div>

                    {/* Hero Headline */}
                    <div>
                      <label className="block font-bold text-stone-800 mb-1">
                        হোমপেজ প্রধান শিরোনাম (Hero Main Headline)
                      </label>
                      <input
                        type="text"
                        value={settingsForm.heroHeadline}
                        onChange={(e) => setSettingsForm({ ...settingsForm, heroHeadline: e.target.value })}
                        className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500 font-bold"
                      />
                    </div>

                    {/* Hero Subheadline */}
                    <div>
                      <label className="block font-bold text-stone-800 mb-1">
                        হোমপেজ সাব-শিরোনাম ও ভূমিকা বার্তা (Hero Sub-Headline)
                      </label>
                      <textarea
                        rows={2}
                        value={settingsForm.heroSubheadline}
                        onChange={(e) => setSettingsForm({ ...settingsForm, heroSubheadline: e.target.value })}
                        className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-rose-500"
                      />
                    </div>

                    {/* Numbers & fees */}
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                      <div>
                        <label className="block font-bold text-stone-800 mb-1">
                          হেল্পলাইন ফোন নম্বর
                        </label>
                        <input
                          type="tel"
                          value={settingsForm.helplinePhone}
                          onChange={(e) => setSettingsForm({ ...settingsForm, helplinePhone: e.target.value })}
                          className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl font-mono font-bold"
                        />
                      </div>

                      <div>
                        <label className="block font-bold text-stone-800 mb-1">
                          হোয়াটসঅ্যাপ নম্বর
                        </label>
                        <input
                          type="tel"
                          value={settingsForm.whatsappNumber}
                          onChange={(e) => setSettingsForm({ ...settingsForm, whatsappNumber: e.target.value })}
                          className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl font-mono font-bold"
                        />
                      </div>

                      <div>
                        <label className="block font-bold text-stone-800 mb-1">
                          প্ল্যাটফর্ম কমিশন হার (%)
                        </label>
                        <input
                          type="number"
                          min="0"
                          max="20"
                          value={settingsForm.commissionPercent}
                          onChange={(e) => setSettingsForm({ ...settingsForm, commissionPercent: Number(e.target.value) })}
                          className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl font-mono font-bold text-rose-600"
                        />
                      </div>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      <div>
                        <label className="block font-bold text-stone-800 mb-1">
                          স্ট্যান্ডার্ড ডেলিভারি চার্জ (টাকা)
                        </label>
                        <input
                          type="number"
                          min="0"
                          value={settingsForm.deliveryCharge}
                          onChange={(e) => setSettingsForm({ ...settingsForm, deliveryCharge: Number(e.target.value) })}
                          className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl font-bold"
                        />
                      </div>

                      <div>
                        <label className="block font-bold text-stone-800 mb-1">
                          ফ্রি ডেলিভারির ন্যূনতম অর্ডার পরিমাণ (টাকা)
                        </label>
                        <input
                          type="number"
                          min="0"
                          value={settingsForm.minFreeDeliveryAmount}
                          onChange={(e) => setSettingsForm({ ...settingsForm, minFreeDeliveryAmount: Number(e.target.value) })}
                          className="w-full p-2.5 bg-stone-50 border border-stone-300 rounded-xl font-bold"
                        />
                      </div>
                    </div>

                    <div className="pt-3 border-t flex justify-end">
                      <button
                        type="submit"
                        className="px-5 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl text-xs font-bold flex items-center gap-2 shadow-md transition-transform active:scale-95"
                      >
                        <Save className="w-4 h-4" />
                        <span>প্ল্যাটফর্ম সেটিংস সংরক্ষণ করুন</span>
                      </button>
                    </div>
                  </form>
                </div>
              )}

              {/* TAB 5: DATABASE & FIREBASE SCHEMA */}
              {activeTab === 'database' && (
                <div className="bg-white p-4 rounded-2xl border border-stone-200 shadow-xs space-y-4">
                  <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-200 text-emerald-900 text-xs flex items-center gap-2">
                    <Database className="w-4 h-4 text-emerald-600 shrink-0" />
                    <span>
                      ফায়ারবেস স্কিমা, সিকিউরিটি রুলস এবং ফ্লাটার সার্ভিস কোড এখন গ্রাহকদের হোমপেজ থেকে সরিয়ে সম্পূর্ণভাবে অ্যাডমিন প্যানেলে সংরক্ষিত করা হয়েছে।
                    </span>
                  </div>
                  <FirebaseArchitectureModal />
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
