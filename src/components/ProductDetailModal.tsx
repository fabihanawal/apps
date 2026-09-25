import React, { useState } from 'react';
import { Product, Shop, Review } from '../types';
import { X, ShoppingCart, MessageCircle, MapPin, Store, Star, User, ShieldCheck, Check } from 'lucide-react';

interface ProductDetailModalProps {
  product: Product | null;
  shop?: Shop;
  reviews: Review[];
  onClose: () => void;
  onAddToCart: (product: Product, quantity: number) => void;
  onAddReview: (reviewData: {
    shopId: string;
    productId?: string;
    customerName: string;
    rating: number;
    comment: string;
  }) => void;
}

export const ProductDetailModal: React.FC<ProductDetailModalProps> = ({
  product,
  shop,
  reviews,
  onClose,
  onAddToCart,
  onAddReview,
}) => {
  const [quantity, setQuantity] = useState(1);
  const [activeTab, setActiveTab] = useState<'details' | 'reviews'>('details');

  // Review form state
  const [reviewerName, setReviewerName] = useState('');
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewSuccess, setReviewSuccess] = useState(false);

  if (!product) return null;

  const productReviews = reviews.filter(
    (r) => r.productId === product.productId || r.shopId === product.shopId
  );

  const hasDiscount = product.discountPrice && product.discountPrice < product.price;
  const effectivePrice = hasDiscount ? product.discountPrice! : product.price;
  const isOutOfStock = product.stock <= 0;
  const shopPhone = shop?.phone || '01755383039';

  const handleWhatsApp = () => {
    const text = encodeURIComponent(
      `আসসালামু আলাইকুম! "আমার দোকান" প্ল্যাটফর্ম থেকে "${product.productName}" সম্পর্কে জানতে চাই।\nমূল্য: ৳${effectivePrice}\nদোকান: ${shop?.shopName || 'আমার দোকান'}\nএলাকা: ${shop?.union || 'বদলগাছী'}, নওগাঁ।`
    );
    window.open(`https://wa.me/88${shopPhone.replace(/[^0-9]/g, '')}?text=${text}`, '_blank');
  };

  const handleReviewSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!reviewerName.trim() || !reviewComment.trim()) return;

    onAddReview({
      shopId: product.shopId,
      productId: product.productId,
      customerName: reviewerName.trim(),
      rating: reviewRating,
      comment: reviewComment.trim(),
    });

    setReviewComment('');
    setReviewerName('');
    setReviewSuccess(true);
    setTimeout(() => setReviewSuccess(false), 3000);
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/60 backdrop-blur-xs flex items-center justify-center p-3 sm:p-4">
      <div className="bg-white w-full max-w-3xl rounded-3xl shadow-2xl overflow-hidden border border-stone-200 relative my-8 animate-in fade-in zoom-in-95 duration-200">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 z-10 p-2 rounded-full bg-white/90 hover:bg-white text-stone-700 shadow-md transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="grid grid-cols-1 md:grid-cols-12 max-h-[85vh] overflow-y-auto">
          {/* Product Image Column */}
          <div className="md:col-span-6 bg-stone-100 p-6 flex flex-col justify-between border-b md:border-b-0 md:border-r border-stone-200">
            <div className="aspect-square rounded-2xl overflow-hidden bg-white shadow-xs">
              <img
                src={product.imageUrl}
                alt={product.productName}
                className="w-full h-full object-cover"
              />
            </div>

            {/* Shop info card */}
            <div className="mt-4 p-4 rounded-xl bg-white border border-stone-200 shadow-xs space-y-2">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="p-2 rounded-lg bg-emerald-100 text-emerald-800">
                    <Store className="w-4 h-4" />
                  </div>
                  <div>
                    <h4 className="text-xs font-bold text-stone-900">{shop?.shopName}</h4>
                    <p className="text-[11px] text-stone-500">প্রোপাইটর: {shop?.ownerName}</p>
                  </div>
                </div>
                <span className="text-[10px] font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
                  {shop?.status === 'Active' ? 'সক্রিয় ভেরিফাইড' : 'পেন্ডিং'}
                </span>
              </div>

              <div className="flex items-center justify-between text-xs text-stone-600 pt-1 border-t border-stone-100">
                <span className="flex items-center gap-1">
                  <MapPin className="w-3.5 h-3.5 text-emerald-600" />
                  <span>{shop?.union}, বদলগাছী</span>
                </span>
                <span className="font-semibold text-stone-800">📞 {shop?.phone}</span>
              </div>
            </div>
          </div>

          {/* Product Info & Tabs Column */}
          <div className="md:col-span-6 p-6 flex flex-col justify-between space-y-4">
            <div>
              {/* Category & Badge */}
              <div className="flex items-center gap-2 mb-2">
                <span className="text-xs font-semibold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-lg border border-emerald-200">
                  {product.category}
                </span>
                {product.stock > 0 ? (
                  <span className="text-xs font-medium text-emerald-800 bg-emerald-100 px-2 py-0.5 rounded-md">
                    স্টকে আছে ({product.stock} টি)
                  </span>
                ) : (
                  <span className="text-xs font-medium text-rose-800 bg-rose-100 px-2 py-0.5 rounded-md">
                    স্টক শেষ
                  </span>
                )}
              </div>

              {/* Title */}
              <h2 className="text-xl font-bold text-stone-900 leading-snug">
                {product.productName}
              </h2>

              {/* Pricing */}
              <div className="flex items-baseline gap-3 my-3">
                <span className="text-2xl font-extrabold text-emerald-700">
                  ৳{effectivePrice}
                </span>
                {hasDiscount && (
                  <span className="text-sm text-stone-400 line-through">
                    ৳{product.price}
                  </span>
                )}
                {product.unit && (
                  <span className="text-xs text-stone-500 font-medium">
                    / {product.unit}
                  </span>
                )}
              </div>

              {/* Tab Navigation */}
              <div className="flex border-b border-stone-200 mb-3 text-xs font-semibold">
                <button
                  onClick={() => setActiveTab('details')}
                  className={`pb-2 px-3 border-b-2 transition-colors ${
                    activeTab === 'details'
                      ? 'border-emerald-600 text-emerald-700'
                      : 'border-transparent text-stone-500 hover:text-stone-800'
                  }`}
                >
                  বিবরণ ও ডেলিভারি
                </button>
                <button
                  onClick={() => setActiveTab('reviews')}
                  className={`pb-2 px-3 border-b-2 transition-colors flex items-center gap-1.5 ${
                    activeTab === 'reviews'
                      ? 'border-emerald-600 text-emerald-700'
                      : 'border-transparent text-stone-500 hover:text-stone-800'
                  }`}
                >
                  <Star className="w-3.5 h-3.5 text-amber-500 fill-amber-500" />
                  <span>গ্রাহক রিভিউ ({productReviews.length})</span>
                </button>
              </div>

              {/* Tab Content */}
              {activeTab === 'details' ? (
                <div className="space-y-3 text-xs text-stone-600 leading-relaxed">
                  <p>{product.description}</p>

                  <div className="bg-stone-50 p-3 rounded-xl border border-stone-200 space-y-1.5">
                    <div className="flex items-center gap-2 font-medium text-stone-800">
                      <ShieldCheck className="w-4 h-4 text-emerald-600" />
                      <span>আমার দোকান নিশ্চয়তা</span>
                    </div>
                    <ul className="list-disc list-inside text-stone-500 space-y-0.5 pl-1">
                      <li>বদলগাছী উপজেলার ভেতরে দ্রুততম স্থানীয় ডেলিভারি</li>
                      <li>পণ্য হাতে পেয়ে মূল্য পরিশোধ (ক্যাশ অন ডেলিভারি)</li>
                      <li>সরাসরি দোকানদারের সাথে যোগাযোগ সুবিধা</li>
                    </ul>
                  </div>
                </div>
              ) : (
                <div className="space-y-4 max-h-56 overflow-y-auto pr-1">
                  {/* Review List */}
                  {productReviews.length === 0 ? (
                    <p className="text-xs text-stone-500 py-3 text-center">
                      এই পণ্যে এখনও কোনো রিভিউ দেওয়া হয়নি। আপনিই প্রথম রিভিউ দিন!
                    </p>
                  ) : (
                    <div className="space-y-2.5">
                      {productReviews.map((rev) => (
                        <div
                          key={rev.reviewId}
                          className="bg-stone-50 p-3 rounded-xl border border-stone-200 space-y-1"
                        >
                          <div className="flex items-center justify-between">
                            <span className="font-bold text-stone-900 text-xs flex items-center gap-1">
                              <User className="w-3 h-3 text-stone-400" />
                              {rev.customerName}
                            </span>
                            <div className="flex items-center text-amber-500">
                              {[...Array(5)].map((_, i) => (
                                <Star
                                  key={i}
                                  className={`w-3 h-3 ${
                                    i < rev.rating ? 'fill-amber-500' : 'text-stone-300'
                                  }`}
                                />
                              ))}
                            </div>
                          </div>
                          <p className="text-xs text-stone-600">{rev.comment}</p>
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Add Review Form */}
                  <form onSubmit={handleReviewSubmit} className="pt-2 border-t border-stone-200 space-y-2">
                    <h4 className="text-xs font-bold text-stone-800">রিভিউ যুক্ত করুন</h4>
                    {reviewSuccess && (
                      <div className="p-2 rounded bg-emerald-50 text-emerald-700 text-xs flex items-center gap-1">
                        <Check className="w-3.5 h-3.5" /> আপনার রিভিউ সফলভাবে যুক্ত হয়েছে!
                      </div>
                    )}
                    <div className="flex items-center gap-2">
                      <input
                        type="text"
                        placeholder="আপনার নাম"
                        value={reviewerName}
                        onChange={(e) => setReviewerName(e.target.value)}
                        className="w-1/2 p-2 text-xs border border-stone-300 rounded-lg focus:ring-1 focus:ring-emerald-500"
                        required
                      />
                      <div className="flex items-center gap-1">
                        <span className="text-xs text-stone-500">রেটিং:</span>
                        {[1, 2, 3, 4, 5].map((star) => (
                          <button
                            type="button"
                            key={star}
                            onClick={() => setReviewRating(star)}
                            className="p-1"
                          >
                            <Star
                              className={`w-3.5 h-3.5 ${
                                star <= reviewRating
                                  ? 'text-amber-500 fill-amber-500'
                                  : 'text-stone-300'
                              }`}
                            />
                          </button>
                        ))}
                      </div>
                    </div>
                    <textarea
                      placeholder="পণ্য সম্পর্কে আপনার মন্তব্য লিখুন..."
                      value={reviewComment}
                      onChange={(e) => setReviewComment(e.target.value)}
                      rows={2}
                      className="w-full p-2 text-xs border border-stone-300 rounded-lg focus:ring-1 focus:ring-emerald-500"
                      required
                    />
                    <button
                      type="submit"
                      className="w-full py-1.5 bg-stone-900 hover:bg-stone-800 text-white text-xs font-bold rounded-lg transition-colors"
                    >
                      রিভিউ সাবমিট করুন
                    </button>
                  </form>
                </div>
              )}
            </div>

            {/* Quantity & CTA Buttons */}
            <div className="pt-4 border-t border-stone-200 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-stone-700">পরিমাণ:</span>
                <div className="flex items-center border border-stone-300 rounded-xl overflow-hidden bg-stone-50">
                  <button
                    onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                    disabled={quantity <= 1 || isOutOfStock}
                    className="px-3 py-1.5 text-stone-700 hover:bg-stone-200 text-sm font-bold disabled:opacity-50"
                  >
                    -
                  </button>
                  <span className="px-4 py-1 text-sm font-bold text-stone-900">
                    {quantity}
                  </span>
                  <button
                    onClick={() => setQuantity((q) => Math.min(product.stock, q + 1))}
                    disabled={quantity >= product.stock || isOutOfStock}
                    className="px-3 py-1.5 text-stone-700 hover:bg-stone-200 text-sm font-bold disabled:opacity-50"
                  >
                    +
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <button
                  onClick={() => {
                    onAddToCart(product, quantity);
                    onClose();
                  }}
                  disabled={isOutOfStock}
                  className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-2 shadow-sm disabled:bg-stone-300 disabled:cursor-not-allowed"
                >
                  <ShoppingCart className="w-4 h-4" />
                  <span>কার্টে যোগ করুন</span>
                </button>

                <button
                  onClick={handleWhatsApp}
                  className="w-full py-3 bg-teal-600 hover:bg-teal-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-2 shadow-sm"
                >
                  <MessageCircle className="w-4 h-4" />
                  <span>হোয়াটসঅ্যাপ ইনকোয়ারি</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
