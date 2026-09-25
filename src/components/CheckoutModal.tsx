import React, { useState } from 'react';
import { CartItem } from '../services/store';
import { BADALGACHHI_UNIONS, Order } from '../types';
import { X, CheckCircle2, Phone, MapPin, Truck, ShieldCheck, MessageCircle, ArrowRight } from 'lucide-react';

interface CheckoutModalProps {
  isOpen: boolean;
  onClose: () => void;
  cart: CartItem[];
  onPlaceOrder: (orderData: {
    customerName: string;
    customerPhone: string;
    deliveryAddress: string;
    union: string;
    paymentMethod: 'ক্যাশ অন ডেলিভারি (COD)' | 'বিকাশ (bKash)' | 'নগদ (Nagad)';
    notes?: string;
  }) => Order[] | null;
}

export const CheckoutModal: React.FC<CheckoutModalProps> = ({
  isOpen,
  onClose,
  cart,
  onPlaceOrder,
}) => {
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [union, setUnion] = useState(BADALGACHHI_UNIONS[0]);
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [paymentMethod, setPaymentMethod] = useState<
    'ক্যাশ অন ডেলিভারি (COD)' | 'বিকাশ (bKash)' | 'নগদ (Nagad)'
  >('ক্যাশ অন ডেলিভারি (COD)');
  const [notes, setNotes] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [placedOrders, setPlacedOrders] = useState<Order[] | null>(null);

  if (!isOpen) return null;

  const totalAmount = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerName.trim() || !customerPhone.trim() || !deliveryAddress.trim()) {
      return;
    }

    setIsSubmitting(true);
    setTimeout(() => {
      const orders = onPlaceOrder({
        customerName: customerName.trim(),
        customerPhone: customerPhone.trim(),
        deliveryAddress: deliveryAddress.trim(),
        union,
        paymentMethod,
        notes: notes.trim(),
      });
      setIsSubmitting(false);
      if (orders) {
        setPlacedOrders(orders);
      }
    }, 600);
  };

  const handleWhatsAppConfirmation = (order: Order) => {
    const itemsList = order.items
      .map((it) => `- ${it.productName} (পরিমাণ: ${it.quantity}টি, মোট: ৳${it.price * it.quantity})`)
      .join('\n');

    const message = encodeURIComponent(
      `🛍️ *আমার দোকান - নতুন অর্ডার নিশ্চিতকরণ*\n\n` +
      `অর্ডার আইডি: *${order.orderId}*\n` +
      `দোকান: ${order.shopName || 'আমার দোকান বিক্রেতা'}\n` +
      `গ্রাহকের নাম: ${order.customerName}\n` +
      `মোবাইল: ${order.customerPhone}\n` +
      `ইউনিয়ন: ${order.union}, বদলগাছী, নওগাঁ\n` +
      `ঠিকানা: ${order.deliveryAddress}\n\n` +
      `*পণ্যসমূহ:*\n${itemsList}\n\n` +
      `মোট প্রদেয় টাকা: *৳${order.totalAmount}*\n` +
      `পেমেন্ট পদ্ধতি: ${order.paymentMethod}\n\n` +
      `ধন্যবাদ! আরএসটিএস-বিডি (RSTS-BD: 01755383039) পরিচালিত "আমার দোকান" প্ল্যাটফর্ম।`
    );

    window.open(`https://wa.me/8801755383039?text=${message}`, '_blank');
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/60 backdrop-blur-xs flex items-center justify-center p-3 sm:p-4">
      <div className="bg-white w-full max-w-xl rounded-3xl shadow-2xl overflow-hidden border border-stone-200 relative my-8 animate-in fade-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="p-4 sm:p-5 border-b border-stone-200 flex items-center justify-between bg-stone-50">
          <div>
            <h3 className="text-base sm:text-lg font-bold text-stone-900">
              {placedOrders ? 'অর্ডার সফল হয়েছে! 🎉' : 'অর্ডার ও ডেলিভারি তথ্য'}
            </h3>
            <p className="text-xs text-stone-500">
              বদলগাছী, নওগাঁর স্থানীয় হোম ডেলিভারি ও দ্রুত সার্ভিস
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-stone-200 text-stone-500 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-5 sm:p-6 max-h-[80vh] overflow-y-auto">
          {placedOrders ? (
            /* Order Placed Success View */
            <div className="text-center space-y-5 py-2">
              <div className="w-16 h-16 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto shadow-inner">
                <CheckCircle2 className="w-10 h-10" />
              </div>

              <div>
                <h3 className="text-xl font-bold text-stone-900">
                  আপনার অর্ডারটি সফলভাবে গ্রহণ করা হয়েছে!
                </h3>
                <p className="text-xs text-stone-600 mt-1.5 max-w-md mx-auto">
                  বদলগাছীর স্থানীয় দোকানদারকে আপনার অর্ডার সম্পর্কে অবহিত করা হয়েছে। শীঘ্রই তারা পণ্য প্যাক করে ডেলিভারিতে পাঠাবেন।
                </p>
              </div>

              {/* Order Cards */}
              <div className="space-y-3 text-left">
                {placedOrders.map((ord) => (
                  <div
                    key={ord.orderId}
                    className="p-4 rounded-2xl bg-stone-50 border border-stone-200 space-y-2.5"
                  >
                    <div className="flex items-center justify-between border-b border-stone-200 pb-2">
                      <div>
                        <span className="text-xs font-bold text-emerald-800">
                          {ord.orderId}
                        </span>
                        <p className="text-[11px] text-stone-500">
                          দোকান: {ord.shopName}
                        </p>
                      </div>
                      <div className="text-right">
                        <span className="text-xs font-black text-stone-900">
                          ৳{ord.totalAmount}
                        </span>
                        <span className="block text-[10px] text-amber-700 font-semibold">
                          {ord.orderStatus === 'Pending' ? 'অপেক্ষমাণ (Pending)' : ord.orderStatus}
                        </span>
                      </div>
                    </div>

                    <div className="text-xs text-stone-600 space-y-1">
                      <p>
                        <strong>ডেলিভারি ঠিকানা:</strong> {ord.deliveryAddress}, {ord.union}, বদলগাছী
                      </p>
                      <p>
                        <strong>পেমেন্ট:</strong> {ord.paymentMethod}
                      </p>
                    </div>

                    {/* WhatsApp Action */}
                    <button
                      onClick={() => handleWhatsAppConfirmation(ord)}
                      className="w-full py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors flex items-center justify-center gap-1.5 shadow-xs"
                    >
                      <MessageCircle className="w-4 h-4" />
                      <span>দোকানদার/RSTS-BD কে WhatsApp-এ কনফার্ম করুন</span>
                    </button>
                  </div>
                ))}
              </div>

              <div className="pt-2">
                <button
                  onClick={onClose}
                  className="px-6 py-2.5 bg-stone-900 hover:bg-stone-800 text-white text-xs font-bold rounded-xl"
                >
                  আরও কেনাকাটা করুন
                </button>
              </div>
            </div>
          ) : (
            /* Checkout Form */
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* Order Items Snapshot */}
              <div className="bg-stone-50 p-3 rounded-2xl border border-stone-200 space-y-2">
                <span className="text-xs font-bold text-stone-800">অর্ডারের পণ্যসমূহ ({cart.length} টি):</span>
                <div className="max-h-28 overflow-y-auto space-y-1.5 text-xs">
                  {cart.map((it) => (
                    <div key={it.productId} className="flex justify-between text-stone-600">
                      <span className="truncate pr-2">
                        {it.productName} x {it.quantity}
                      </span>
                      <span className="font-bold text-stone-900 shrink-0">
                        ৳{it.price * it.quantity}
                      </span>
                    </div>
                  ))}
                </div>
                <div className="pt-2 border-t border-stone-200 flex justify-between text-xs font-extrabold text-stone-900">
                  <span>সর্বমোট প্রদেয়:</span>
                  <span className="text-emerald-700 text-sm">৳{totalAmount}</span>
                </div>
              </div>

              {/* Customer Inputs */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    আপনার পূর্ণ নাম <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="text"
                    required
                    value={customerName}
                    onChange={(e) => setCustomerName(e.target.value)}
                    placeholder="যেমন: মো: কামরুল হাসান"
                    className="w-full p-2.5 text-xs bg-white border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    মোবাইল নম্বর <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="tel"
                    required
                    value={customerPhone}
                    onChange={(e) => setCustomerPhone(e.target.value)}
                    placeholder="যেমন: 017xxxxxxxx"
                    className="w-full p-2.5 text-xs bg-white border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    বদলগাছী ইউনিয়ন <span className="text-rose-500">*</span>
                  </label>
                  <select
                    value={union}
                    onChange={(e) => setUnion(e.target.value as any)}
                    className="w-full p-2.5 text-xs bg-white border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-medium"
                  >
                    {BADALGACHHI_UNIONS.map((u) => (
                      <option key={u} value={u}>
                        {u}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    উপজেলা ও জেলা
                  </label>
                  <input
                    type="text"
                    disabled
                    value="বদলগাছী, নওগাঁ"
                    className="w-full p-2.5 text-xs bg-stone-100 border border-stone-200 rounded-xl text-stone-500 font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  গ্রাম, পাড়া ও বিস্তারিত ঠিকানা <span className="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={deliveryAddress}
                  onChange={(e) => setDeliveryAddress(e.target.value)}
                  placeholder="যেমন: আধাইপুর মধ্যপাড়া, তিনমাথা মোড়ের সাথে"
                  className="w-full p-2.5 text-xs bg-white border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              {/* Payment Method */}
              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1.5">
                  মূল্য পরিশোধের পদ্ধতি
                </label>
                <div className="grid grid-cols-3 gap-2">
                  {[
                    'ক্যাশ অন ডেলিভারি (COD)',
                    'বিকাশ (bKash)',
                    'নগদ (Nagad)',
                  ].map((method) => (
                    <button
                      key={method}
                      type="button"
                      onClick={() => setPaymentMethod(method as any)}
                      className={`p-2 rounded-xl text-[11px] font-bold border text-center transition-all ${
                        paymentMethod === method
                          ? 'bg-emerald-50 border-emerald-500 text-emerald-800 ring-1 ring-emerald-500'
                          : 'bg-white border-stone-200 text-stone-600 hover:bg-stone-50'
                      }`}
                    >
                      {method}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  ডেলিভারি সংক্রান্ত বিশেষ নির্দেশনা (ঐচ্ছিক)
                </label>
                <input
                  type="text"
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  placeholder="যেমন: বিকেলে ফোন দিয়ে আসবেন"
                  className="w-full p-2.5 text-xs bg-white border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="p-3 bg-emerald-50/60 rounded-xl border border-emerald-100 flex items-start gap-2 text-[11px] text-emerald-800">
                <ShieldCheck className="w-4 h-4 shrink-0 text-emerald-600 mt-0.5" />
                <span>
                  আপনার অর্ডারটি সরাসরি বদলগাছীর নিবন্ধিত স্থানীয় দোকান মালিকের নিকট পৌঁছাবে। আরএসটিএস-বিডি (RSTS-BD) দ্বারা নিরাপদ ডেলিভারি নিশ্চিত করা হয়।
                </span>
              </div>

              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 active:scale-98 text-white rounded-xl font-bold text-xs shadow-md transition-all flex items-center justify-center gap-2 disabled:bg-stone-400"
              >
                {isSubmitting ? (
                  <span>অর্ডার তৈরি হচ্ছে...</span>
                ) : (
                  <>
                    <span>অর্ডার নিশ্চিত করুন (৳{totalAmount})</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};
