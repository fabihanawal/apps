export type UnionName =
  | 'বদলগাছী সদর'
  | 'পাহাড়পুর'
  | 'মথুরাপুর'
  | 'বালুভরা'
  | 'কোলা'
  | 'বিলাশবাড়ী'
  | 'আধাইপুর'
  | 'মিঠাপুর';

export const BADALGACHHI_UNIONS: UnionName[] = [
  'বদলগাছী সদর',
  'পাহাড়পুর',
  'মথুরাপুর',
  'বালুভরা',
  'কোলা',
  'বিলাশবাড়ী',
  'আধাইপুর',
  'মিঠাপুর',
];

export type ProductCategory =
  | 'সব ক্যাটাগরি'
  | 'কাঁচাবাজার ও মুদি'
  | 'তৈরি পোশাক ও বস্ত্র'
  | 'স্থানীয় হস্তশিল্প ও ঐতিহ্য'
  | 'কৃষি পণ্য ও বীজ'
  | 'মিষ্টি, দই ও বেকারি'
  | 'ইলেকট্রনিক্স ও গ্যাজেট'
  | 'স্বাস্থ্য ও প্রসাধনী';

export const PRODUCT_CATEGORIES: ProductCategory[] = [
  'সব ক্যাটাগরি',
  'কাঁচাবাজার ও মুদি',
  'তৈরি পোশাক ও বস্ত্র',
  'স্থানীয় হস্তশিল্প ও ঐতিহ্য',
  'কৃষি পণ্য ও বীজ',
  'মিষ্টি, দই ও বেকারি',
  'ইলেকট্রনিক্স ও গ্যাজেট',
  'স্বাস্থ্য ও প্রসাধনী',
];

export interface Shop {
  shopId: string;
  shopName: string;
  ownerName: string;
  phone: string;
  email: string;
  address?: string;
  union: string;
  upazila: string;
  district: string;
  category: string;
  status: 'Active' | 'Pending' | 'Rejected';
  description?: string;
  logoUrl?: string;
  rating?: number;
  totalReviews?: number;
  createdAt: number;
}

export interface Product {
  productId: string;
  shopId: string;
  productName: string;
  price: number;
  discountPrice?: number;
  stock: number;
  description: string;
  imageUrl: string;
  category: string;
  unit?: string;
  createdAt: number;
}

export interface OrderItem {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  imageUrl: string;
}

export type OrderStatus = 'Pending' | 'Confirmed' | 'Packed' | 'Shipped' | 'Delivered' | 'Cancelled';

export interface Order {
  orderId: string;
  shopId: string;
  shopName?: string;
  customerName: string;
  customerPhone: string;
  deliveryAddress: string;
  union: string;
  items: OrderItem[];
  totalAmount: number;
  platformCommission: number; // e.g. 5% of total
  orderStatus: OrderStatus;
  paymentMethod: 'ক্যাশ অন ডেলিভারি (COD)' | 'বিকাশ (bKash)' | 'নগদ (Nagad)';
  timestamp: number;
  notes?: string;
}

export interface MarketingPost {
  postId: string;
  shopId: string;
  shopName: string;
  productName: string;
  generatedText: string;
  imageUrl?: string;
  createdAt: number;
}

export interface Review {
  reviewId: string;
  shopId: string;
  productId?: string;
  customerName: string;
  rating: number;
  comment: string;
  createdAt: number;
}
