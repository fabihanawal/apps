import { useState, useEffect } from 'react';
import { Shop, Product, Order, MarketingPost, Review, OrderItem, OrderStatus } from '../types';
import {
  INITIAL_SHOPS,
  INITIAL_PRODUCTS,
  INITIAL_ORDERS,
  INITIAL_MARKETING_POSTS,
  INITIAL_REVIEWS,
} from '../data/mockData';

const STORAGE_KEYS = {
  SHOPS: 'amar_dokan_shops_v1',
  PRODUCTS: 'amar_dokan_products_v1',
  ORDERS: 'amar_dokan_orders_v1',
  MARKETING_POSTS: 'amar_dokan_marketing_posts_v1',
  REVIEWS: 'amar_dokan_reviews_v1',
  CART: 'amar_dokan_cart_v1',
  ACTIVE_SHOP_ID: 'amar_dokan_active_shop_id_v1',
  VENDOR_EMAIL: 'amar_dokan_vendor_email_v1',
  ADMIN_AUTH: 'amar_dokan_admin_auth_v1',
  PLATFORM_SETTINGS: 'amar_dokan_platform_settings_v1',
};

export interface PlatformSettings {
  announcementText: string;
  heroHeadline: string;
  heroSubheadline: string;
  helplinePhone: string;
  whatsappNumber: string;
  deliveryCharge: number;
  minFreeDeliveryAmount: number;
  commissionPercent: number;
}

export const DEFAULT_PLATFORM_SETTINGS: PlatformSettings = {
  announcementText: 'বদলগাছী উপজেলার ৮টি ইউনিয়নে দ্রুততম হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা!',
  heroHeadline: 'ঘরের কাছে সেরা পণ্য, আমার দোকান এ সরাসরি অর্ডার!',
  heroSubheadline: 'ঐতিহাসিক পাহাড়পুর থেকে শুরু করে কোলা, বালুভরা ও সদর ইউনিয়নের বিশ্বস্ত উদ্যোক্তাদের তৈরি খাঁটি মিষ্টি, হস্তশিল্প, তাজা কৃষিপণ্য ও গ্রোসারি।',
  helplinePhone: '01755383039',
  whatsappNumber: '01755383039',
  deliveryCharge: 30,
  minFreeDeliveryAmount: 500,
  commissionPercent: 5,
};

function loadStorage<T>(key: string, defaultValue: T): T {
  try {
    const saved = localStorage.getItem(key);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (e) {
    console.error(`Failed to load ${key} from storage:`, e);
  }
  return defaultValue;
}

function saveStorage<T>(key: string, value: T) {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.error(`Failed to save ${key} to storage:`, e);
  }
}

export interface CartItem extends OrderItem {
  shopId: string;
  maxStock: number;
}

export function useAmarDokanStore() {
  const [shops, setShops] = useState<Shop[]>(() => {
    const loaded = loadStorage<Shop[]>(STORAGE_KEYS.SHOPS, INITIAL_SHOPS);
    // Ensure all shops have email field
    return loaded.map((s, idx) => ({
      ...s,
      email: s.email || `vendor${idx + 1}@amardokan.bd`,
      address: s.address || `${s.union}, বদলগাছী, নওগাঁ`,
    }));
  });
  const [products, setProducts] = useState<Product[]>(() =>
    loadStorage<Product[]>(STORAGE_KEYS.PRODUCTS, INITIAL_PRODUCTS)
  );
  const [orders, setOrders] = useState<Order[]>(() =>
    loadStorage<Order[]>(STORAGE_KEYS.ORDERS, INITIAL_ORDERS)
  );
  const [marketingPosts, setMarketingPosts] = useState<MarketingPost[]>(() =>
    loadStorage<MarketingPost[]>(STORAGE_KEYS.MARKETING_POSTS, INITIAL_MARKETING_POSTS)
  );
  const [reviews, setReviews] = useState<Review[]>(() =>
    loadStorage<Review[]>(STORAGE_KEYS.REVIEWS, INITIAL_REVIEWS)
  );
  const [cart, setCart] = useState<CartItem[]>(() =>
    loadStorage<CartItem[]>(STORAGE_KEYS.CART, [])
  );
  const [activeShopId, setActiveShopId] = useState<string>(() =>
    loadStorage<string>(STORAGE_KEYS.ACTIVE_SHOP_ID, INITIAL_SHOPS[0].shopId)
  );
  const [loggedInVendorEmail, setLoggedInVendorEmail] = useState<string | null>(() =>
    loadStorage<string | null>(STORAGE_KEYS.VENDOR_EMAIL, null)
  );
  const [isAdminLoggedIn, setIsAdminLoggedIn] = useState<boolean>(false);
  const [platformSettings, setPlatformSettings] = useState<PlatformSettings>(() =>
    loadStorage<PlatformSettings>(STORAGE_KEYS.PLATFORM_SETTINGS, DEFAULT_PLATFORM_SETTINGS)
  );

  // Sync to localStorage
  useEffect(() => {
    saveStorage(STORAGE_KEYS.SHOPS, shops);
  }, [shops]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.PRODUCTS, products);
  }, [products]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.ORDERS, orders);
  }, [orders]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.MARKETING_POSTS, marketingPosts);
  }, [marketingPosts]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.REVIEWS, reviews);
  }, [reviews]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.CART, cart);
  }, [cart]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.ACTIVE_SHOP_ID, activeShopId);
  }, [activeShopId]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.VENDOR_EMAIL, loggedInVendorEmail);
  }, [loggedInVendorEmail]);

  useEffect(() => {
    saveStorage(STORAGE_KEYS.PLATFORM_SETTINGS, platformSettings);
  }, [platformSettings]);

  // Cart operations
  const addToCart = (product: Product, quantity = 1) => {
    const effectivePrice = product.discountPrice ?? product.price;
    setCart((prev) => {
      const existing = prev.find((item) => item.productId === product.productId);
      if (existing) {
        const newQty = Math.min(existing.quantity + quantity, product.stock);
        return prev.map((item) =>
          item.productId === product.productId ? { ...item, quantity: newQty } : item
        );
      }
      return [
        ...prev,
        {
          productId: product.productId,
          productName: product.productName,
          quantity: Math.min(quantity, product.stock),
          price: effectivePrice,
          imageUrl: product.imageUrl,
          shopId: product.shopId,
          maxStock: product.stock,
        },
      ];
    });
  };

  const updateCartQuantity = (productId: string, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(productId);
      return;
    }
    setCart((prev) =>
      prev.map((item) =>
        item.productId === productId
          ? { ...item, quantity: Math.min(quantity, item.maxStock) }
          : item
      )
    );
  };

  const removeFromCart = (productId: string) => {
    setCart((prev) => prev.filter((item) => item.productId !== productId));
  };

  const clearCart = () => {
    setCart([]);
  };

  // Order Placement
  const placeOrder = (orderData: {
    customerName: string;
    customerPhone: string;
    deliveryAddress: string;
    union: string;
    paymentMethod: 'ক্যাশ অন ডেলিভারি (COD)' | 'বিকাশ (bKash)' | 'নগদ (Nagad)';
    notes?: string;
  }) => {
    if (cart.length === 0) return null;

    // Group items by shopId so each shop owner receives their specific order
    const shopMap = new Map<string, CartItem[]>();
    for (const item of cart) {
      const list = shopMap.get(item.shopId) || [];
      list.push(item);
      shopMap.set(item.shopId, list);
    }

    const createdOrders: Order[] = [];
    const timestamp = Date.now();

    shopMap.forEach((items, shopId) => {
      const shop = shops.find((s) => s.shopId === shopId);
      const totalAmount = items.reduce((sum, it) => sum + it.price * it.quantity, 0);
      const commission = Math.round(totalAmount * 0.05); // 5% platform commission
      const randomId = Math.floor(10000 + Math.random() * 90000);
      const orderId = `ORD-BDL-${randomId}`;

      const newOrder: Order = {
        orderId,
        shopId,
        shopName: shop?.shopName || 'আমার দোকান বিক্রেতা',
        customerName: orderData.customerName,
        customerPhone: orderData.customerPhone,
        deliveryAddress: orderData.deliveryAddress,
        union: orderData.union,
        items: items.map((it) => ({
          productId: it.productId,
          productName: it.productName,
          quantity: it.quantity,
          price: it.price,
          imageUrl: it.imageUrl,
        })),
        totalAmount,
        platformCommission: commission,
        orderStatus: 'Pending',
        paymentMethod: orderData.paymentMethod,
        timestamp,
        notes: orderData.notes,
      };

      createdOrders.push(newOrder);

      // Decrement product stock
      setProducts((currentProducts) =>
        currentProducts.map((p) => {
          const orderedItem = items.find((it) => it.productId === p.productId);
          if (orderedItem) {
            return {
              ...p,
              stock: Math.max(0, p.stock - orderedItem.quantity),
            };
          }
          return p;
        })
      );
    });

    setOrders((prev) => [...createdOrders, ...prev]);
    clearCart();
    return createdOrders;
  };

  // Order status update
  const updateOrderStatus = (orderId: string, status: OrderStatus) => {
    setOrders((prev) =>
      prev.map((ord) => (ord.orderId === orderId ? { ...ord, orderStatus: status } : ord))
    );
  };

  // Product management
  const addProduct = (product: Omit<Product, 'productId' | 'createdAt'>) => {
    const newProduct: Product = {
      ...product,
      productId: `prod-${Date.now()}`,
      createdAt: Date.now(),
    };
    setProducts((prev) => [newProduct, ...prev]);
    return newProduct;
  };

  const updateProduct = (productId: string, updates: Partial<Product>) => {
    setProducts((prev) =>
      prev.map((p) => (p.productId === productId ? { ...p, ...updates } : p))
    );
  };

  const deleteProduct = (productId: string) => {
    setProducts((prev) => prev.filter((p) => p.productId !== productId));
    removeFromCart(productId);
  };

  // Shop registration & update
  const registerShop = (shopData: Omit<Shop, 'shopId' | 'status' | 'createdAt' | 'rating' | 'totalReviews'>) => {
    const newShopId = `shop-${Date.now()}`;
    const newShop: Shop = {
      ...shopData,
      shopId: newShopId,
      status: 'Pending', // New shopkeeper application awaits Admin approval
      rating: 5.0,
      totalReviews: 1,
      createdAt: Date.now(),
    };
    setShops((prev) => [newShop, ...prev]);
    return newShop;
  };

  const approveShop = (shopId: string) => {
    setShops((prev) =>
      prev.map((s) => (s.shopId === shopId ? { ...s, status: 'Active' } : s))
    );
  };

  const rejectShop = (shopId: string) => {
    setShops((prev) =>
      prev.map((s) => (s.shopId === shopId ? { ...s, status: 'Rejected' } : s))
    );
  };

  const deleteShop = (shopId: string) => {
    setShops((prev) => prev.filter((s) => s.shopId !== shopId));
    if (activeShopId === shopId) {
      const remaining = shops.filter((s) => s.shopId !== shopId);
      if (remaining.length > 0) {
        setActiveShopId(remaining[0].shopId);
      }
    }
  };

  const updateShop = (shopId: string, updates: Partial<Shop>) => {
    setShops((prev) => prev.map((s) => (s.shopId === shopId ? { ...s, ...updates } : s)));
  };

  // Direct shop creation from Admin
  const addShopDirectly = (
    shopData: Omit<Shop, 'shopId' | 'createdAt' | 'rating' | 'totalReviews'> & {
      status?: 'Active' | 'Pending' | 'Rejected';
    }
  ) => {
    const newShopId = `shop-${Date.now()}`;
    const newShop: Shop = {
      ...shopData,
      shopId: newShopId,
      status: shopData.status || 'Active',
      rating: 5.0,
      totalReviews: 1,
      createdAt: Date.now(),
    };
    setShops((prev) => [newShop, ...prev]);
    return newShop;
  };

  // Delete Order (Admin)
  const deleteOrder = (orderId: string) => {
    setOrders((prev) => prev.filter((o) => o.orderId !== orderId));
  };

  // Delete Review (Admin)
  const deleteReview = (reviewId: string) => {
    setReviews((prev) => prev.filter((r) => r.reviewId !== reviewId));
  };

  // Platform CMS & settings update (Admin)
  const updatePlatformSettings = (updates: Partial<PlatformSettings>) => {
    setPlatformSettings((prev) => ({ ...prev, ...updates }));
  };

  // Vendor Authentication by Email
  const loginVendorByEmail = (email: string) => {
    const normalized = email.trim().toLowerCase();
    const matchedShop = shops.find(
      (s) => s.email && s.email.trim().toLowerCase() === normalized
    );

    if (!matchedShop) {
      return {
        success: false,
        status: 'NotFound',
        message: 'এই ইমেইল এড্রেস দিয়ে কোনো দোকানের আবেদন পাওয়া যায়নি। অনুগ্রহ করে নতুন দোকান নিবন্ধন আবেদন করুন।',
      };
    }

    if (matchedShop.status === 'Pending') {
      return {
        success: false,
        status: 'Pending',
        shop: matchedShop,
        message: `দোকান "${matchedShop.shopName}" এর আবেদনটি এখনও অ্যাডমিনের বিবেচনাধীন রয়েছে। অ্যাডমিন অনুমোদন নিশ্চিত করার পর আপনি ড্যাশবোর্ডে প্রবেশ করতে পারবেন। দ্রুত অনুমোদনের জন্য যোগাযোগ: 01755383039 (RSTS-BD)।`,
      };
    }

    if (matchedShop.status === 'Rejected') {
      return {
        success: false,
        status: 'Rejected',
        shop: matchedShop,
        message: `দোকান "${matchedShop.shopName}" এর আবেদনটি অ্যাডমিন দ্বারা সাময়িকভাবে স্থগিত করা হয়েছে। অনুগ্রহ করে হেল্পলাইনে যোগাযোগ করুন: 01755383039।`,
      };
    }

    // Active status -> Successful login
    setLoggedInVendorEmail(matchedShop.email);
    setActiveShopId(matchedShop.shopId);
    return {
      success: true,
      status: 'Active',
      shop: matchedShop,
      message: `স্বাগতম! "${matchedShop.shopName}" এর ড্যাশবোর্ডে আপনি সফলভাবে প্রবেশ করেছেন।`,
    };
  };

  const logoutVendor = () => {
    setLoggedInVendorEmail(null);
  };

  // Admin Authentication
  const loginAdmin = (email: string, pass: string) => {
    if (email.trim().toLowerCase() === 'rstsbd@gmail.com' && pass === 'rstsbd1234') {
      setIsAdminLoggedIn(true);
      return { success: true };
    }
    return {
      success: false,
      message: 'ভুল ইমেইল বা পাসওয়ার্ড! অনুগ্রহ করে সঠিক এডমিন ক্রেডেনশিয়াল দিন।',
    };
  };

  const logoutAdmin = () => {
    setIsAdminLoggedIn(false);
  };

  // AI Marketing Post saving
  const saveMarketingPost = (post: {
    shopId: string;
    shopName: string;
    productName: string;
    generatedText: string;
    imageUrl?: string;
  }) => {
    const newPost: MarketingPost = {
      postId: `post-${Date.now()}`,
      ...post,
      createdAt: Date.now(),
    };
    setMarketingPosts((prev) => [newPost, ...prev]);
    return newPost;
  };

  // Reviews
  const addReview = (reviewData: {
    shopId: string;
    productId?: string;
    customerName: string;
    rating: number;
    comment: string;
  }) => {
    const newReview: Review = {
      reviewId: `rev-${Date.now()}`,
      ...reviewData,
      createdAt: Date.now(),
    };
    setReviews((prev) => [newReview, ...prev]);

    // Update shop average rating
    setShops((prev) =>
      prev.map((s) => {
        if (s.shopId === reviewData.shopId) {
          const shopReviews = [...reviews.filter((r) => r.shopId === s.shopId), newReview];
          const avg =
            shopReviews.reduce((sum, r) => sum + r.rating, 0) / (shopReviews.length || 1);
          return {
            ...s,
            rating: Number(avg.toFixed(1)),
            totalReviews: shopReviews.length,
          };
        }
        return s;
      })
    );

    return newReview;
  };

  // Reset to default data
  const resetToDefaults = () => {
    setShops(INITIAL_SHOPS);
    setProducts(INITIAL_PRODUCTS);
    setOrders(INITIAL_ORDERS);
    setMarketingPosts(INITIAL_MARKETING_POSTS);
    setReviews(INITIAL_REVIEWS);
    setCart([]);
    setActiveShopId(INITIAL_SHOPS[0].shopId);
  };

  return {
    shops,
    products,
    orders,
    marketingPosts,
    reviews,
    cart,
    activeShopId,
    setActiveShopId,
    addToCart,
    updateCartQuantity,
    removeFromCart,
    clearCart,
    placeOrder,
    updateOrderStatus,
    addProduct,
    updateProduct,
    deleteProduct,
    registerShop,
    updateShop,
    approveShop,
    rejectShop,
    deleteShop,
    addShopDirectly,
    deleteOrder,
    deleteReview,
    platformSettings,
    updatePlatformSettings,
    loggedInVendorEmail,
    setLoggedInVendorEmail,
    loginVendorByEmail,
    logoutVendor,
    isAdminLoggedIn,
    setIsAdminLoggedIn,
    loginAdmin,
    logoutAdmin,
    saveMarketingPost,
    addReview,
    resetToDefaults,
  };
}
