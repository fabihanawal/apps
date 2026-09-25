import React, { useState } from 'react';
import { Order } from '../types';
import { Search, PackageCheck, Clock, Truck, CheckCircle2, XCircle, Phone, MessageCircle } from 'lucide-react';

interface OrderTrackerModalProps {
  orders: Order[];
}

export const OrderTrackerModal: React.FC<OrderTrackerModalProps> = ({ orders }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [searched, setSearched] = useState(false);

  const filteredOrders = orders.filter((o) => {
    if (!searchTerm.trim()) return false;
    const term = searchTerm.trim().toLowerCase();
    return (
      o.orderId.toLowerCase().includes(term) ||
      o.customerPhone.includes(term)
    );
  });

  const getStepIndex = (status: string) => {
    switch (status) {
      case 'Pending':
        return 1;
      case 'Confirmed':
        return 2;
      case 'Packed':
        return 3;
      case 'Shipped':
        return 4;
      case 'Delivered':
        return 5;
      case 'Cancelled':
        return -1;
      default:
        return 1;
    }
  };

  const steps = [
    { label: 'অপেক্ষমাণ', key: 'Pending', desc: 'অর্ডার গৃহীত হয়েছে' },
    { label: 'নিশ্চিত', key: 'Confirmed', desc: 'দোকানদার গ্রহণ করেছেন' },
    { label: 'প্যাক সম্পন্ন', key: 'Packed', desc: 'প্যাকেট প্রস্তুত' },
    { label: 'ডেলিভারিতে', key: 'Shipped', desc: 'ডেলিভারি ম্যানের কাছে' },
    { label: 'ডেলিভার্ড', key: 'Delivered', desc: 'গ্রাহকের হাতে পৌঁছেছে' },
  ];

  return (
    <div className="max-w-4xl mx-auto py-6 px-4 space-y-6">
      <div className="bg-white p-6 rounded-3xl border border-stone-200 shadow-sm text-center space-y-3">
        <div className="w-12 h-12 rounded-2xl bg-emerald-100 text-emerald-700 flex items-center justify-center mx-auto">
          <PackageCheck className="w-6 h-6" />
        </div>
        <h2 className="text-xl font-bold text-stone-900">
          আপনার অর্ডার ট্র্যাক করুন
        </h2>
        <p className="text-xs text-stone-500 max-w-md mx-auto">
          আপনার অর্ডার আইডি (যেমন: ORD-BDL-10293) অথবা চেকআউটে ব্যবহৃত মোবাইল নম্বর দিয়ে বর্তমান অবস্থা জানুন।
        </p>

        <div className="max-w-md mx-auto flex items-center gap-2 pt-2">
          <div className="relative flex-1">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setSearched(true);
              }}
              placeholder="অর্ডার আইডি বা মোবাইল নম্বর লিখুন..."
              className="w-full pl-9 pr-4 py-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:bg-white"
            />
            <Search className="w-4 h-4 text-stone-400 absolute left-3 top-3" />
          </div>
          <button
            onClick={() => setSearched(true)}
            className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors"
          >
            খুঁজুন
          </button>
        </div>
      </div>

      {/* Results Display */}
      {searched && (
        <div className="space-y-4">
          {filteredOrders.length === 0 ? (
            <div className="bg-white p-8 rounded-3xl border border-stone-200 text-center text-stone-500 space-y-2">
              <p className="text-sm font-semibold">কোনো অর্ডার খুঁজে পাওয়া যায়নি</p>
              <p className="text-xs text-stone-400">
                অনুগ্রহ করে সঠিক অর্ডার আইডি বা মোবাইল নম্বর দিয়েছেন কিনা যাচাই করুন। অথবা সহায়তার জন্য কল করুন: 01755383039
              </p>
            </div>
          ) : (
            filteredOrders.map((order) => {
              const currentStep = getStepIndex(order.orderStatus);

              return (
                <div
                  key={order.orderId}
                  className="bg-white p-6 rounded-3xl border border-stone-200 shadow-sm space-y-6"
                >
                  {/* Order Top Bar */}
                  <div className="flex flex-wrap items-center justify-between gap-3 border-b border-stone-100 pb-4">
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-extrabold text-stone-900 text-base">
                          {order.orderId}
                        </span>
                        <span className="text-xs bg-emerald-50 text-emerald-800 font-bold px-2 py-0.5 rounded-md border border-emerald-200">
                          {order.shopName}
                        </span>
                      </div>
                      <p className="text-xs text-stone-400 mt-0.5">
                        তারিখ:{' '}
                        {new Date(order.timestamp).toLocaleString('bn-BD', {
                          dateStyle: 'medium',
                          timeStyle: 'short',
                        })}
                      </p>
                    </div>

                    <div className="text-right">
                      <span className="text-xs text-stone-500 block">মোট প্রদেয়:</span>
                      <span className="text-lg font-black text-emerald-700">
                        ৳{order.totalAmount}
                      </span>
                      <span className="block text-[11px] text-stone-500 font-medium">
                        {order.paymentMethod}
                      </span>
                    </div>
                  </div>

                  {/* Stepper */}
                  {order.orderStatus === 'Cancelled' ? (
                    <div className="p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-xs font-bold flex items-center gap-2">
                      <XCircle className="w-5 h-5 text-rose-600" />
                      <span>এই অর্ডারটি বাতিল করা হয়েছে। বিস্তারিত জানতে যোগাযোগ করুন 01755383039 নম্বরে।</span>
                    </div>
                  ) : (
                    <div>
                      <div className="grid grid-cols-5 gap-2 relative">
                        {steps.map((st, idx) => {
                          const stepNum = idx + 1;
                          const isDone = currentStep >= stepNum;
                          const isCurrent = currentStep === stepNum;

                          return (
                            <div key={st.key} className="text-center space-y-1.5">
                              <div
                                className={`w-8 h-8 rounded-full flex items-center justify-center mx-auto text-xs font-black transition-all ${
                                  isDone
                                    ? 'bg-emerald-600 text-white shadow-md'
                                    : 'bg-stone-200 text-stone-500'
                                } ${isCurrent ? 'ring-4 ring-emerald-200 scale-110' : ''}`}
                              >
                                {isDone ? '✓' : stepNum}
                              </div>
                              <p
                                className={`text-[11px] font-bold ${
                                  isCurrent
                                    ? 'text-emerald-700'
                                    : isDone
                                    ? 'text-stone-800'
                                    : 'text-stone-400'
                                }`}
                              >
                                {st.label}
                              </p>
                              <p className="text-[10px] text-stone-400 hidden sm:block">
                                {st.desc}
                              </p>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  )}

                  {/* Order Items & Delivery Location */}
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2 border-t border-stone-100 text-xs">
                    <div className="space-y-1.5">
                      <span className="font-bold text-stone-800">পণ্য তালিকা:</span>
                      <ul className="space-y-1 text-stone-600">
                        {order.items.map((it) => (
                          <li key={it.productId} className="flex justify-between">
                            <span>
                              {it.productName} (x{it.quantity})
                            </span>
                            <span className="font-semibold text-stone-900">
                              ৳{it.price * it.quantity}
                            </span>
                          </li>
                        ))}
                      </ul>
                    </div>

                    <div className="space-y-1.5 sm:border-l sm:border-stone-100 sm:pl-4">
                      <span className="font-bold text-stone-800">ডেলিভারি তথ্য:</span>
                      <p className="text-stone-600">
                        <strong>গ্রাহক:</strong> {order.customerName} (📞 {order.customerPhone})
                      </p>
                      <p className="text-stone-600">
                        <strong>ঠিকানা:</strong> {order.deliveryAddress}, {order.union}, বদলগাছী
                      </p>
                      {order.notes && (
                        <p className="text-stone-500 italic">নোট: {order.notes}</p>
                      )}
                    </div>
                  </div>

                  {/* Helpline */}
                  <div className="flex flex-wrap items-center justify-between gap-2 pt-2 border-t border-stone-100">
                    <span className="text-[11px] text-stone-500">
                      কোনো সহযোগিতার প্রয়োজন হলে RSTS-BD কাস্টমার কেয়ারে যোগাযোগ করুন:
                    </span>
                    <a
                      href="https://wa.me/8801755383039"
                      target="_blank"
                      rel="noreferrer"
                      className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-50 text-emerald-800 hover:bg-emerald-100 rounded-lg text-xs font-bold transition-colors"
                    >
                      <MessageCircle className="w-3.5 h-3.5" />
                      <span>WhatsApp এ কথা বলুন (01755383039)</span>
                    </a>
                  </div>
                </div>
              );
            })
          )}
        </div>
      )}

      {/* Recent orders preview if not yet searched */}
      {!searched && (
        <div className="space-y-3">
          <h3 className="text-xs font-bold text-stone-600 uppercase tracking-wider">
            সম্প্রতি করা কিছু অর্ডার (ডেমো ট্র্যাকিং):
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {orders.slice(0, 4).map((ord) => (
              <button
                key={ord.orderId}
                onClick={() => {
                  setSearchTerm(ord.orderId);
                  setSearched(true);
                }}
                className="p-3.5 bg-white hover:bg-emerald-50/50 border border-stone-200 hover:border-emerald-300 rounded-2xl text-left transition-all group"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-emerald-800 group-hover:text-emerald-700">
                    {ord.orderId}
                  </span>
                  <span className="text-[10px] bg-stone-100 text-stone-700 font-semibold px-2 py-0.5 rounded">
                    {ord.orderStatus}
                  </span>
                </div>
                <p className="text-xs text-stone-800 font-medium mt-1 truncate">
                  গ্রাহক: {ord.customerName} ({ord.union})
                </p>
                <div className="flex justify-between items-center text-[11px] text-stone-500 mt-1">
                  <span>মোট: ৳{ord.totalAmount}</span>
                  <span className="text-emerald-600 font-semibold">ট্র্যাক করুন &rarr;</span>
                </div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
