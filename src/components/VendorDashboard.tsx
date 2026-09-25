import React, { useState } from 'react';
import { Shop, Product, Order, MarketingPost, Review, OrderStatus, PRODUCT_CATEGORIES, BADALGACHHI_UNIONS } from '../types';
import {
  Store,
  Package,
  ShoppingBag,
  Sparkles,
  Plus,
  Pencil,
  Trash2,
  Copy,
  Share2,
  CheckCircle,
  Clock,
  Phone,
  DollarSign,
  TrendingUp,
  MapPin,
  ExternalLink,
  MessageCircle,
  AlertCircle,
  Tag,
  Star,
  LogIn,
  LogOut,
  Mail,
  UserCheck,
} from 'lucide-react';

interface VendorDashboardProps {
  shops: Shop[];
  activeShopId: string;
  setActiveShopId: (shopId: string) => void;
  products: Product[];
  orders: Order[];
  marketingPosts: MarketingPost[];
  reviews: Review[];
  onAddProduct: (prod: Omit<Product, 'productId' | 'createdAt'>) => void;
  onUpdateProduct: (productId: string, updates: Partial<Product>) => void;
  onDeleteProduct: (productId: string) => void;
  onUpdateOrderStatus: (orderId: string, status: OrderStatus) => void;
  onSaveMarketingPost: (post: {
    shopId: string;
    shopName: string;
    productName: string;
    generatedText: string;
    imageUrl?: string;
  }) => void;
  onOpenRegisterShop: () => void;
  loggedInVendorEmail?: string | null;
  onOpenVendorLogin?: () => void;
  onLogoutVendor?: () => void;
}

export const VendorDashboard: React.FC<VendorDashboardProps> = ({
  shops,
  activeShopId,
  setActiveShopId,
  products,
  orders,
  marketingPosts,
  reviews,
  onAddProduct,
  onUpdateProduct,
  onDeleteProduct,
  onUpdateOrderStatus,
  onSaveMarketingPost,
  onOpenRegisterShop,
  loggedInVendorEmail,
  onOpenVendorLogin,
  onLogoutVendor,
}) => {
  const currentShop = shops.find((s) => s.shopId === activeShopId) || shops[0];
  const [activeTab, setActiveTab] = useState<'products' | 'orders' | 'marketing' | 'posts' | 'reviews'>('products');

  // Product Add / Edit Modal State
  const [isProductModalOpen, setIsProductModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [productForm, setProductForm] = useState({
    productName: '',
    price: '',
    discountPrice: '',
    stock: '',
    unit: '',
    category: PRODUCT_CATEGORIES[1],
    description: '',
    imageUrl: '',
  });

  // AI Marketing Generator State
  const [selectedProductId, setSelectedProductId] = useState<string>('');
  const [marketingTone, setMarketingTone] = useState('আকর্ষণীয় ও ধামাকা অফার');
  const [customPrompt, setCustomPrompt] = useState('');
  const [isGeneratingAI, setIsGeneratingAI] = useState(false);
  const [generatedAIText, setGeneratedAIText] = useState('');
  const [copySuccess, setCopySuccess] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);

  // Filtered data for active shop
  const shopProducts = products.filter((p) => p.shopId === currentShop?.shopId);
  const shopOrders = orders.filter((o) => o.shopId === currentShop?.shopId);
  const shopMarketingPosts = marketingPosts.filter((m) => m.shopId === currentShop?.shopId);
  const shopReviews = reviews.filter((r) => r.shopId === currentShop?.shopId);

  // Financial calculations
  const totalGrossSales = shopOrders
    .filter((o) => o.orderStatus !== 'Cancelled')
    .reduce((sum, o) => sum + o.totalAmount, 0);

  const totalPlatformCommission = shopOrders
    .filter((o) => o.orderStatus !== 'Cancelled')
    .reduce((sum, o) => sum + o.platformCommission, 0);

  const netSellerPayout = totalGrossSales - totalPlatformCommission;
  const pendingOrdersCount = shopOrders.filter((o) => o.orderStatus === 'Pending').length;

  // Open modal for new product
  const handleOpenAddProduct = () => {
    setEditingProduct(null);
    setProductForm({
      productName: '',
      price: '',
      discountPrice: '',
      stock: '10',
      unit: '১ টি',
      category: PRODUCT_CATEGORIES[1],
      description: '',
      imageUrl: 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=700&auto=format&fit=crop&q=80',
    });
    setIsProductModalOpen(true);
  };

  // Open modal for editing product
  const handleOpenEditProduct = (prod: Product) => {
    setEditingProduct(prod);
    setProductForm({
      productName: prod.productName,
      price: prod.price.toString(),
      discountPrice: prod.discountPrice ? prod.discountPrice.toString() : '',
      stock: prod.stock.toString(),
      unit: prod.unit || '১ টি',
      category: prod.category as any,
      description: prod.description,
      imageUrl: prod.imageUrl,
    });
    setIsProductModalOpen(true);
  };

  // Submit product form
  const handleProductSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!productForm.productName.trim() || !productForm.price) return;

    const payload = {
      shopId: currentShop.shopId,
      productName: productForm.productName.trim(),
      price: Number(productForm.price),
      discountPrice: productForm.discountPrice ? Number(productForm.discountPrice) : undefined,
      stock: Number(productForm.stock) || 0,
      unit: productForm.unit.trim(),
      category: productForm.category,
      description: productForm.description.trim(),
      imageUrl: productForm.imageUrl.trim() || 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=700&auto=format&fit=crop&q=80',
    };

    if (editingProduct) {
      onUpdateProduct(editingProduct.productId, payload);
    } else {
      onAddProduct(payload);
    }

    setIsProductModalOpen(false);
  };

  // AI Content Generator Execution
  const handleGenerateAIMarketing = async () => {
    const product = shopProducts.find((p) => p.productId === selectedProductId) || shopProducts[0];
    if (!product) return;

    setIsGeneratingAI(true);
    setGeneratedAIText('');
    setCopySuccess(false);
    setSaveSuccess(false);

    try {
      const response = await fetch('/api/marketing/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          productName: product.productName,
          price: product.price,
          discountPrice: product.discountPrice,
          shopName: currentShop.shopName,
          unionName: currentShop.union,
          upazila: currentShop.upazila,
          district: currentShop.district,
          category: product.category,
          description: product.description,
          tone: marketingTone,
          customInstructions: customPrompt,
          phone: currentShop.phone || '01755383039',
        }),
      });

      const data = await response.json();
      if (data.generatedText) {
        setGeneratedAIText(data.generatedText);
      } else {
        throw new Error(data.error || 'Failed to generate');
      }
    } catch (err) {
      console.error('AI Generation Error:', err);
      // Fallback
      setGeneratedAIText(
        `🔥 বদলগাছীবাসীর জন্য স্পেশাল অফার! ✨\n\n"${currentShop.shopName}" এ পাওয়া যাচ্ছে প্রিমিয়াম কোয়ালিটির ${product.productName}!\n\n🏷️ অফার মূল্য: মাত্র ৳${product.discountPrice || product.price}!\n📦 আপনার ইউনিয়নে দ্রুত ডেলিভারি সুবিধা।\n📍 ${currentShop.union}, বদলগাছী, নওগাঁ।\n\n👉 অর্ডার করতে কল/WhatsApp করুন: ${currentShop.phone}\n🌐 "আমার দোকান" প্ল্যাটফর্মে কেনাকাটা করুন নিরাপদ ও সহজে।\n\n#বদলগাছী #নওগাঁ #আমারদোকান #RSTS_BD`
      );
    } finally {
      setIsGeneratingAI(false);
    }
  };

  const handleCopyAIText = () => {
    navigator.clipboard.writeText(generatedAIText);
    setCopySuccess(true);
    setTimeout(() => setCopySuccess(false), 2500);
  };

  const handleSaveAIPost = () => {
    const product = shopProducts.find((p) => p.productId === selectedProductId) || shopProducts[0];
    onSaveMarketingPost({
      shopId: currentShop.shopId,
      shopName: currentShop.shopName,
      productName: product?.productName || 'পণ্য',
      generatedText: generatedAIText,
      imageUrl: product?.imageUrl,
    });
    setSaveSuccess(true);
    setTimeout(() => setSaveSuccess(false), 2500);
  };

  const handleShareFacebook = () => {
    const shareUrl = encodeURIComponent(window.location.href);
    window.open(`https://www.facebook.com/sharer/sharer.php?u=${shareUrl}`, '_blank');
  };

  const handleShareWhatsApp = () => {
    const encoded = encodeURIComponent(generatedAIText);
    window.open(`https://wa.me/?text=${encoded}`, '_blank');
  };

  return (
    <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 space-y-6">
      {/* Vendor Account & Login Status Bar */}
      <div className="bg-stone-900 text-white px-4 py-3 rounded-2xl flex flex-wrap items-center justify-between gap-3 shadow-sm border border-stone-800">
        <div className="flex items-center gap-2.5">
          <div className="p-1.5 rounded-lg bg-emerald-600 text-white">
            <UserCheck className="w-4 h-4" />
          </div>
          <div className="text-xs">
            {loggedInVendorEmail ? (
              <p>
                লগইনকৃত দোকানদার: <strong className="text-emerald-400 font-mono">{loggedInVendorEmail}</strong>
                <span className="text-stone-400 ml-2">({currentShop.shopName})</span>
              </p>
            ) : (
              <p className="text-stone-300">
                দোকানদার ড্যাশবোর্ড • আপনার নিবন্ধিত ইমেইল দিয়ে লগইন করে পণ্য আপলোড ও বিক্রয় পরিচালনা করুন
              </p>
            )}
          </div>
        </div>

        <div className="flex items-center gap-2">
          {loggedInVendorEmail ? (
            <button
              onClick={onLogoutVendor}
              className="px-3 py-1.5 bg-stone-800 hover:bg-stone-700 text-stone-300 hover:text-white rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors border border-stone-700"
            >
              <LogOut className="w-3.5 h-3.5 text-rose-400" />
              <span>লগআউট</span>
            </button>
          ) : (
            onOpenVendorLogin && (
              <button
                onClick={onOpenVendorLogin}
                className="px-3.5 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 transition-colors shadow-xs"
              >
                <LogIn className="w-3.5 h-3.5" />
                <span>ইমেইল দিয়ে দোকানদার লগইন</span>
              </button>
            )
          )}
        </div>
      </div>

      {/* Pending Approval Notice if Shop is Pending */}
      {currentShop.status === 'Pending' && (
        <div className="p-4 bg-amber-50 border-2 border-amber-300 rounded-2xl text-amber-900 flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-amber-600 shrink-0 mt-0.5" />
          <div className="text-xs space-y-1">
            <h4 className="font-bold text-sm text-amber-950">
              ⏳ দোকানটি অ্যাডমিন অনুমোদনের অপেক্ষমাণ (Pending Approval)
            </h4>
            <p>
              আপনার আবেদনটি যাচাই করা হচ্ছে। অ্যাডমিন (RSTS-BD: 01755383039) কনফার্ম করা মাত্রই আপনার দোকান ও পণ্যসমূহ গ্রাহকদের কাছে প্রদর্শিত হবে।
            </p>
          </div>
        </div>
      )}

      {/* Top Header & Mini-Shop Switcher */}
      <div className="bg-white p-5 sm:p-6 rounded-3xl border border-stone-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-2xl bg-emerald-700 text-white flex items-center justify-center shrink-0 shadow-md">
            <Store className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl font-extrabold text-stone-900">
                {currentShop.shopName}
              </h1>
              <span
                className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                  currentShop.status === 'Active'
                    ? 'bg-emerald-100 text-emerald-800'
                    : 'bg-amber-100 text-amber-900 border border-amber-300'
                }`}
              >
                {currentShop.status === 'Active' ? 'সক্রিয় দোকান' : 'অপেক্ষমাণ (Pending)'}
              </span>
            </div>
            <p className="text-xs text-stone-500 flex flex-wrap items-center gap-2 mt-0.5">
              <span>প্রোপাইটর: {currentShop.ownerName}</span>
              <span>•</span>
              <span className="flex items-center gap-0.5">
                <MapPin className="w-3 h-3 text-emerald-600" />
                {currentShop.union}, বদলগাছী
              </span>
              <span>•</span>
              <span>📞 {currentShop.phone}</span>
              <span>•</span>
              <span className="font-mono text-stone-600">✉️ {currentShop.email}</span>
            </p>
          </div>
        </div>

        {/* Shop Switcher Dropdown & Register Button */}
        <div className="flex items-center gap-2">
          <div className="relative">
            <select
              value={activeShopId}
              onChange={(e) => setActiveShopId(e.target.value)}
              className="p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-bold text-stone-800"
            >
              {shops.map((s) => (
                <option key={s.shopId} value={s.shopId}>
                  🏪 {s.shopName} ({s.union}) {s.status === 'Pending' ? '⏳' : ''}
                </option>
              ))}
            </select>
          </div>

          <button
            onClick={onOpenRegisterShop}
            className="px-3 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center gap-1.5 shadow-xs whitespace-nowrap"
          >
            <Plus className="w-4 h-4" />
            <span>নতুন দোকান আবেদন</span>
          </button>
        </div>
      </div>

      {/* Financial & Operational KPI Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
        <div className="bg-white p-4 sm:p-5 rounded-2xl border border-stone-200 shadow-xs space-y-1">
          <div className="flex items-center justify-between text-stone-500 text-xs">
            <span>মোট বিক্রি (Gross Sales)</span>
            <DollarSign className="w-4 h-4 text-emerald-600" />
          </div>
          <p className="text-xl sm:text-2xl font-black text-stone-900">
            ৳{totalGrossSales}
          </p>
          <p className="text-[11px] text-emerald-600 font-semibold">
            {shopOrders.length} টি অর্ডারের মোট বিল
          </p>
        </div>

        <div className="bg-white p-4 sm:p-5 rounded-2xl border border-stone-200 shadow-xs space-y-1">
          <div className="flex items-center justify-between text-stone-500 text-xs">
            <span>দোকানদারের নিট আয় (Net Payout)</span>
            <TrendingUp className="w-4 h-4 text-teal-600" />
          </div>
          <p className="text-xl sm:text-2xl font-black text-teal-700">
            ৳{netSellerPayout}
          </p>
          <p className="text-[11px] text-stone-400">কমিশন বাদে আপনার প্রাপ্য</p>
        </div>

        <div className="bg-white p-4 sm:p-5 rounded-2xl border border-stone-200 shadow-xs space-y-1">
          <div className="flex items-center justify-between text-stone-500 text-xs">
            <span>প্ল্যাটফর্ম ফি (৫% RSTS-BD)</span>
            <Tag className="w-4 h-4 text-amber-600" />
          </div>
          <p className="text-xl sm:text-2xl font-black text-amber-700">
            ৳{totalPlatformCommission}
          </p>
          <p className="text-[11px] text-amber-800 font-semibold">
            সফল বিক্রির উপর কমিশন
          </p>
        </div>

        <div className="bg-white p-4 sm:p-5 rounded-2xl border border-stone-200 shadow-xs space-y-1">
          <div className="flex items-center justify-between text-stone-500 text-xs">
            <span>নতুন অর্ডার (Pending)</span>
            <ShoppingBag className="w-4 h-4 text-rose-600" />
          </div>
          <p className="text-xl sm:text-2xl font-black text-rose-600">
            {pendingOrdersCount} টি
          </p>
          <p className="text-[11px] text-stone-400">প্যাক ও কনফার্ম করতে হবে</p>
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="flex flex-wrap items-center gap-2 border-b border-stone-200 pb-2">
        <button
          onClick={() => setActiveTab('products')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            activeTab === 'products'
              ? 'bg-emerald-600 text-white shadow-xs'
              : 'bg-white text-stone-600 hover:bg-stone-100 border border-stone-200'
          }`}
        >
          <Package className="w-4 h-4" />
          <span>পণ্য তালিকা ({shopProducts.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('orders')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            activeTab === 'orders'
              ? 'bg-emerald-600 text-white shadow-xs'
              : 'bg-white text-stone-600 hover:bg-stone-100 border border-stone-200'
          }`}
        >
          <ShoppingBag className="w-4 h-4" />
          <span>অর্ডার ও ডেলিভারি ({shopOrders.length})</span>
          {pendingOrdersCount > 0 && (
            <span className="bg-rose-500 text-white text-[10px] px-1.5 py-0.2 rounded-full font-black">
              {pendingOrdersCount}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveTab('marketing')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            activeTab === 'marketing'
              ? 'bg-gradient-to-r from-amber-600 to-amber-700 text-white shadow-xs'
              : 'bg-amber-50 text-amber-900 hover:bg-amber-100 border border-amber-200'
          }`}
        >
          <Sparkles className="w-4 h-4 text-amber-300" />
          <span>AI সোশ্যাল মিডিয়া পোস্ট জেনারেটর</span>
        </button>

        <button
          onClick={() => setActiveTab('posts')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            activeTab === 'posts'
              ? 'bg-emerald-600 text-white shadow-xs'
              : 'bg-white text-stone-600 hover:bg-stone-100 border border-stone-200'
          }`}
        >
          <span>সংরক্ষিত পোস্ট ({shopMarketingPosts.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('reviews')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            activeTab === 'reviews'
              ? 'bg-emerald-600 text-white shadow-xs'
              : 'bg-white text-stone-600 hover:bg-stone-100 border border-stone-200'
          }`}
        >
          <Star className="w-4 h-4 text-amber-500 fill-amber-500" />
          <span>গ্রাহক রিভিউ ({shopReviews.length})</span>
        </button>
      </div>

      {/* TAB 1: PRODUCT MANAGEMENT */}
      {activeTab === 'products' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-stone-900">
                আপনার দোকানের পণ্যসমূহ
              </h2>
              <p className="text-xs text-stone-500">
                মূল্য, স্টক ও পণ্যের ছবি আপডেট করুন
              </p>
            </div>
            <button
              onClick={handleOpenAddProduct}
              className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center gap-1.5 shadow-xs"
            >
              <Plus className="w-4 h-4" />
              <span>নতুন পণ্য যোগ করুন</span>
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {shopProducts.map((prod) => (
              <div
                key={prod.productId}
                className="bg-white rounded-2xl border border-stone-200 p-4 flex flex-col justify-between space-y-3 shadow-xs"
              >
                <div className="flex items-start gap-3">
                  <img
                    src={prod.imageUrl}
                    alt={prod.productName}
                    className="w-20 h-20 rounded-xl object-cover bg-stone-100 shrink-0 border border-stone-200"
                  />
                  <div className="flex-1 min-w-0">
                    <span className="text-[10px] font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded">
                      {prod.category}
                    </span>
                    <h4 className="text-xs font-bold text-stone-900 truncate mt-1">
                      {prod.productName}
                    </h4>
                    <div className="text-xs font-extrabold text-stone-900 mt-1">
                      ৳{prod.discountPrice || prod.price}
                      {prod.discountPrice && (
                        <span className="text-[10px] text-stone-400 line-through ml-1.5">
                          ৳{prod.price}
                        </span>
                      )}
                    </div>
                    <div className="text-[11px] text-stone-500 mt-0.5">
                      স্টক: <strong className="text-stone-800">{prod.stock}</strong> টি ({prod.unit || '১টি'})
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-2 border-t border-stone-100 text-xs">
                  <button
                    onClick={() => {
                      setSelectedProductId(prod.productId);
                      setActiveTab('marketing');
                    }}
                    className="text-amber-600 hover:text-amber-800 font-bold flex items-center gap-1 text-[11px]"
                  >
                    <Sparkles className="w-3.5 h-3.5" />
                    <span>AI পোস্ট লিখুন</span>
                  </button>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => handleOpenEditProduct(prod)}
                      className="p-1.5 text-stone-600 hover:text-emerald-700 rounded-lg hover:bg-stone-100"
                      title="সম্পাদনা করুন"
                    >
                      <Pencil className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onDeleteProduct(prod.productId)}
                      className="p-1.5 text-rose-500 hover:text-rose-700 rounded-lg hover:bg-rose-50"
                      title="মুছে ফেলুন"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB 2: ORDER MANAGEMENT */}
      {activeTab === 'orders' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-base font-bold text-stone-900">
              অর্ডার ও ডেলিভারি ব্যবস্থাপনা
            </h2>
            <p className="text-xs text-stone-500">
              নতুন অর্ডার আসলে প্যাক করুন এবং স্ট্যাটাস পরিবর্তন করুন
            </p>
          </div>

          {shopOrders.length === 0 ? (
            <div className="bg-white p-8 rounded-3xl border border-stone-200 text-center text-stone-500">
              <ShoppingBag className="w-12 h-12 mx-auto text-stone-300 mb-2" />
              <p className="text-sm font-semibold">এখনও কোনো অর্ডার আসেনি</p>
              <p className="text-xs text-stone-400 mt-1">
                গ্রাহক আপনার পণ্য অর্ডার করলে এখানে দেখতে পাবেন।
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {shopOrders.map((ord) => (
                <div
                  key={ord.orderId}
                  className="bg-white p-5 rounded-2xl border border-stone-200 shadow-xs space-y-4"
                >
                  <div className="flex flex-wrap items-center justify-between gap-2 border-b border-stone-100 pb-3">
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-extrabold text-stone-900 text-sm">
                          {ord.orderId}
                        </span>
                        <span className="text-xs font-semibold text-stone-500">
                          ({new Date(ord.timestamp).toLocaleDateString('bn-BD')})
                        </span>
                      </div>
                      <p className="text-xs text-stone-600 mt-0.5">
                        গ্রাহক: <strong>{ord.customerName}</strong> (📞{' '}
                        <a href={`tel:${ord.customerPhone}`} className="text-emerald-700 underline">
                          {ord.customerPhone}
                        </a>
                        )
                      </p>
                    </div>

                    {/* Status badge & changer */}
                    <div className="flex items-center gap-2">
                      <span className="text-xs text-stone-500">স্ট্যাটাস:</span>
                      <select
                        value={ord.orderStatus}
                        onChange={(e) => onUpdateOrderStatus(ord.orderId, e.target.value as OrderStatus)}
                        className={`text-xs font-bold p-1.5 rounded-lg border focus:ring-2 focus:ring-emerald-500 ${
                          ord.orderStatus === 'Pending'
                            ? 'bg-amber-50 text-amber-800 border-amber-300'
                            : ord.orderStatus === 'Delivered'
                            ? 'bg-emerald-50 text-emerald-800 border-emerald-300'
                            : ord.orderStatus === 'Cancelled'
                            ? 'bg-rose-50 text-rose-800 border-rose-300'
                            : 'bg-teal-50 text-teal-800 border-teal-300'
                        }`}
                      >
                        <option value="Pending">অপেক্ষমাণ (Pending)</option>
                        <option value="Confirmed">নিশ্চিত (Confirmed)</option>
                        <option value="Packed">প্যাক সম্পন্ন (Packed)</option>
                        <option value="Shipped">ডেলিভারিতে (Shipped)</option>
                        <option value="Delivered">ডেলিভার্ড (Delivered)</option>
                        <option value="Cancelled">বাতিল (Cancelled)</option>
                      </select>
                    </div>
                  </div>

                  {/* Items & Address */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                    <div className="space-y-1">
                      <span className="font-bold text-stone-700">অর্ডারের পণ্য:</span>
                      <ul className="space-y-1 text-stone-600">
                        {ord.items.map((it) => (
                          <li key={it.productId} className="flex justify-between bg-stone-50 p-2 rounded-lg">
                            <span>
                              {it.productName} (x{it.quantity})
                            </span>
                            <span className="font-bold text-stone-900">
                              ৳{it.price * it.quantity}
                            </span>
                          </li>
                        ))}
                      </ul>
                    </div>

                    <div className="space-y-1.5 bg-stone-50 p-3 rounded-xl border border-stone-200">
                      <p>
                        <strong>ডেলিভারি ঠিকানা:</strong> {ord.deliveryAddress}, {ord.union}, বদলগাছী
                      </p>
                      <p>
                        <strong>পেমেন্ট পদ্ধতি:</strong> {ord.paymentMethod}
                      </p>
                      {ord.notes && <p className="text-stone-500 italic">নোট: {ord.notes}</p>}
                      <div className="pt-2 border-t border-stone-200 flex justify-between font-bold text-stone-800">
                        <span>মোট বিল: ৳{ord.totalAmount}</span>
                        <span className="text-emerald-700">
                          আপনার প্রাপ্য (কমিশন ৫% বাদে): ৳{ord.totalAmount - ord.platformCommission}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 3: AUTOMATED MARKETING CONTENT GENERATOR */}
      {activeTab === 'marketing' && (
        <div className="bg-white p-6 rounded-3xl border border-stone-200 shadow-sm space-y-6">
          <div className="flex items-start justify-between gap-3">
            <div>
              <div className="flex items-center gap-2">
                <div className="p-2 rounded-xl bg-amber-100 text-amber-800">
                  <Sparkles className="w-5 h-5 text-amber-600" />
                </div>
                <h2 className="text-lg font-bold text-stone-900">
                  স্বয়ংক্রিয় এআই ফেসবুক মার্কেটিং পোস্ট জেনারেটর
                </h2>
              </div>
              <p className="text-xs text-stone-500 mt-1">
                গুগল জেমিনাই (Gemini 3.8 Flash) দ্বারা চালিত। বদলগাছীর ক্রেতাদের আকৃষ্ট করার মত সেরা বাংলা ফেসবুক পোস্ট তৈরি করুন।
              </p>
            </div>
          </div>

          {/* Form */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-stone-700 mb-1">
                পণ্য নির্বাচন করুন <span className="text-rose-500">*</span>
              </label>
              <select
                value={selectedProductId}
                onChange={(e) => setSelectedProductId(e.target.value)}
                className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-medium"
              >
                <option value="">-- আপনার পণ্য বাছাই করুন --</option>
                {shopProducts.map((p) => (
                  <option key={p.productId} value={p.productId}>
                    {p.productName} (৳{p.discountPrice || p.price})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-stone-700 mb-1">
                পোস্টের টোন ও ধরন
              </label>
              <select
                value={marketingTone}
                onChange={(e) => setMarketingTone(e.target.value)}
                className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-medium"
              >
                <option value="আকর্ষণীয় ও ধামাকা অফার">🔥 আকর্ষণীয় ও ধামাকা অফার (Hot Deal)</option>
                <option value="টাটকা ও আসল স্থানীয় ঐতিহ্য">🌿 খাঁটি ও টাটকা স্থানীয় পণ্য (Local & Authentic)</option>
                <option value="নতুন আগমন ও সীমিত স্টক">✨ নতুন স্টক আগমন (New Arrival)</option>
                <option value="উৎসবের বিশেষ ছাড়">🎉 উৎসব/ঈদের বিশেষ ছাড় (Festive Offer)</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-stone-700 mb-1">
              অতিরিক্ত কোনো বিশেষ বার্তা বা অফার (ঐচ্ছিক)
            </label>
            <input
              type="text"
              value={customPrompt}
              onChange={(e) => setCustomPrompt(e.target.value)}
              placeholder="যেমন: ২ কেজি নিলে ডেলিভারি চার্জ ফ্রি, অথবা শুক্রবারের স্পেশাল ডিসকাউন্ট..."
              className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          <button
            onClick={handleGenerateAIMarketing}
            disabled={isGeneratingAI || (!selectedProductId && shopProducts.length === 0)}
            className="w-full py-3 bg-gradient-to-r from-emerald-600 via-teal-600 to-emerald-700 hover:from-emerald-700 hover:to-emerald-800 text-white font-bold text-xs rounded-xl shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-50"
          >
            <Sparkles className="w-4 h-4" />
            <span>
              {isGeneratingAI ? 'পোস্ট তৈরি হচ্ছে (Gemini AI)...' : 'ফেসবুক মার্কেটিং পোস্ট তৈরি করুন'}
            </span>
          </button>

          {/* Generated Result */}
          {generatedAIText && (
            <div className="mt-4 p-5 rounded-2xl bg-amber-50/50 border border-amber-200 space-y-4 animate-in fade-in duration-300">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-amber-900 flex items-center gap-1.5">
                  <CheckCircle className="w-4 h-4 text-emerald-600" />
                  জেনারেট হওয়া ফেসবুক পোস্ট:
                </span>
                <div className="flex items-center gap-2">
                  <button
                    onClick={handleCopyAIText}
                    className="px-3 py-1.5 bg-white border border-stone-200 hover:bg-stone-50 text-stone-700 rounded-lg text-xs font-bold transition-colors flex items-center gap-1"
                  >
                    <Copy className="w-3.5 h-3.5" />
                    <span>{copySuccess ? 'কপি হয়েছে! ✓' : 'কপি করুন'}</span>
                  </button>
                  <button
                    onClick={handleSaveAIPost}
                    className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-bold transition-colors flex items-center gap-1"
                  >
                    <span>{saveSuccess ? 'সংরক্ষিত! ✓' : 'সেভ করুন'}</span>
                  </button>
                </div>
              </div>

              <div className="p-4 bg-white rounded-xl border border-stone-200 text-xs text-stone-800 whitespace-pre-wrap font-sans leading-relaxed shadow-xs">
                {generatedAIText}
              </div>

              {/* Share actions */}
              <div className="flex flex-wrap items-center justify-between gap-3 pt-2">
                <span className="text-xs text-stone-500">এক ক্লিকেই শেয়ার করুন:</span>
                <div className="flex items-center gap-2">
                  <button
                    onClick={handleShareFacebook}
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center gap-1.5 shadow-xs"
                  >
                    <Share2 className="w-3.5 h-3.5" />
                    <span>ফেসবুকে শেয়ার</span>
                  </button>
                  <button
                    onClick={handleShareWhatsApp}
                    className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center gap-1.5 shadow-xs"
                  >
                    <MessageCircle className="w-3.5 h-3.5" />
                    <span>WhatsApp-এ শেয়ার</span>
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 4: SAVED MARKETING POSTS */}
      {activeTab === 'posts' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-base font-bold text-stone-900">
              সংরক্ষিত ফেসবুক পোস্টসমূহ
            </h2>
            <p className="text-xs text-stone-500">
              পূর্বে জেনারেট করা পোস্টগুলো কপি করে ফেসবুক পেজ বা গ্রুপে প্রকাশ করুন
            </p>
          </div>

          {shopMarketingPosts.length === 0 ? (
            <div className="bg-white p-8 rounded-3xl border border-stone-200 text-center text-stone-500">
              <Sparkles className="w-10 h-10 mx-auto text-amber-400 mb-2" />
              <p className="text-sm font-semibold">কোনো সংরক্ষিত পোস্ট নেই</p>
              <button
                onClick={() => setActiveTab('marketing')}
                className="mt-3 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl"
              >
                এআই দিয়ে পোস্ট তৈরি করুন
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {shopMarketingPosts.map((post) => (
                <div
                  key={post.postId}
                  className="bg-white p-4 rounded-2xl border border-stone-200 shadow-xs flex flex-col justify-between space-y-3"
                >
                  <div className="space-y-2">
                    <div className="flex items-center justify-between text-xs text-stone-400 border-b border-stone-100 pb-2">
                      <span className="font-bold text-emerald-800">
                        পণ্য: {post.productName}
                      </span>
                      <span>
                        {new Date(post.createdAt).toLocaleDateString('bn-BD')}
                      </span>
                    </div>
                    <p className="text-xs text-stone-800 whitespace-pre-wrap line-clamp-6 leading-relaxed bg-stone-50 p-3 rounded-xl">
                      {post.generatedText}
                    </p>
                  </div>

                  <div className="flex items-center justify-end gap-2 pt-2 border-t border-stone-100">
                    <button
                      onClick={() => {
                        navigator.clipboard.writeText(post.generatedText);
                        alert('পোস্ট ক্লিপবোর্ডে কপি করা হয়েছে!');
                      }}
                      className="px-3 py-1.5 bg-stone-100 hover:bg-stone-200 text-stone-800 text-xs font-bold rounded-lg transition-colors flex items-center gap-1"
                    >
                      <Copy className="w-3.5 h-3.5" />
                      <span>কপি করুন</span>
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 5: REVIEWS */}
      {activeTab === 'reviews' && (
        <div className="space-y-4">
          <div>
            <h2 className="text-base font-bold text-stone-900">
              গ্রাহকের মতামত ও রিভিউ
            </h2>
            <p className="text-xs text-stone-500">
              বদলগাছীর ক্রেতাদের আস্থা ও মূল্যায়নের বিবরণ
            </p>
          </div>

          {shopReviews.length === 0 ? (
            <div className="bg-white p-8 rounded-3xl border border-stone-200 text-center text-stone-500">
              <Star className="w-10 h-10 mx-auto text-stone-300 mb-2" />
              <p className="text-sm font-semibold">এখনও কোনো রিভিউ যুক্ত হয়নি</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {shopReviews.map((rev) => (
                <div
                  key={rev.reviewId}
                  className="bg-white p-4 rounded-2xl border border-stone-200 shadow-xs space-y-2"
                >
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-stone-900">
                      {rev.customerName}
                    </span>
                    <div className="flex text-amber-500">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-3.5 h-3.5 ${
                            i < rev.rating ? 'fill-amber-500' : 'text-stone-300'
                          }`}
                        />
                      ))}
                    </div>
                  </div>
                  <p className="text-xs text-stone-700 bg-stone-50 p-2.5 rounded-xl">
                    "{rev.comment}"
                  </p>
                  <p className="text-[10px] text-stone-400 text-right">
                    {new Date(rev.createdAt).toLocaleDateString('bn-BD')}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Product Add / Edit Modal */}
      {isProductModalOpen && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-black/60 backdrop-blur-xs flex items-center justify-center p-3 sm:p-4">
          <div className="bg-white w-full max-w-lg rounded-3xl shadow-2xl p-6 border border-stone-200 relative animate-in fade-in zoom-in-95 duration-200 max-h-[90vh] overflow-y-auto">
            <h3 className="text-base font-bold text-stone-900 mb-1">
              {editingProduct ? 'পণ্য সম্পাদনা করুন' : 'নতুন পণ্য যোগ করুন'}
            </h3>
            <p className="text-xs text-stone-500 mb-4">
              দোকান: {currentShop.shopName}
            </p>

            <form onSubmit={handleProductSubmit} className="space-y-3.5">
              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  পণ্যের নাম <span className="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={productForm.productName}
                  onChange={(e) => setProductForm({ ...productForm, productName: e.target.value })}
                  placeholder="যেমন: নওগাঁর সুগন্ধি কাটারিভোগ চাল"
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    বিক্রয় মূল্য (৳) <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="number"
                    required
                    value={productForm.price}
                    onChange={(e) => setProductForm({ ...productForm, price: e.target.value })}
                    placeholder="যেমন: 500"
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    ডিসকাউন্ট অফার মূল্য (৳, ঐচ্ছিক)
                  </label>
                  <input
                    type="number"
                    value={productForm.discountPrice}
                    onChange={(e) => setProductForm({ ...productForm, discountPrice: e.target.value })}
                    placeholder="যেমন: 450"
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    স্টক পরিমাণ
                  </label>
                  <input
                    type="number"
                    value={productForm.stock}
                    onChange={(e) => setProductForm({ ...productForm, stock: e.target.value })}
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    একক / পরিমাণ
                  </label>
                  <input
                    type="text"
                    value={productForm.unit}
                    onChange={(e) => setProductForm({ ...productForm, unit: e.target.value })}
                    placeholder="যেমন: ১ কেজি, ১ সেট"
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  ক্যাটাগরি
                </label>
                <select
                  value={productForm.category}
                  onChange={(e) => setProductForm({ ...productForm, category: e.target.value as any })}
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                >
                  {PRODUCT_CATEGORIES.filter((c) => c !== 'সব ক্যাটাগরি').map((cat) => (
                    <option key={cat} value={cat}>
                      {cat}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  ছবির লিংক (URL)
                </label>
                <input
                  type="url"
                  value={productForm.imageUrl}
                  onChange={(e) => setProductForm({ ...productForm, imageUrl: e.target.value })}
                  placeholder="https://images.unsplash.com/..."
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  পণ্যের বিস্তারিত বিবরণ
                </label>
                <textarea
                  rows={3}
                  value={productForm.description}
                  onChange={(e) => setProductForm({ ...productForm, description: e.target.value })}
                  placeholder="পণ্যটির গুণাগুণ ও বিশেষত্ব লিখুন..."
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-3">
                <button
                  type="button"
                  onClick={() => setIsProductModalOpen(false)}
                  className="px-4 py-2 bg-stone-100 hover:bg-stone-200 text-stone-700 rounded-xl text-xs font-bold"
                >
                  বাতিল
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold"
                >
                  {editingProduct ? 'পরিবর্তন সংরক্ষণ করুন' : 'পণ্য যোগ করুন'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
