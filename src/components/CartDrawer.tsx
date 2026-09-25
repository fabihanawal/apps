import React from 'react';
import { CartItem } from '../services/store';
import { X, Trash2, ShoppingBag, ArrowRight } from 'lucide-react';

interface CartDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  cart: CartItem[];
  onUpdateQuantity: (productId: string, quantity: number) => void;
  onRemoveItem: (productId: string) => void;
  onProceedToCheckout: () => void;
}

export const CartDrawer: React.FC<CartDrawerProps> = ({
  isOpen,
  onClose,
  cart,
  onUpdateQuantity,
  onRemoveItem,
  onProceedToCheckout,
}) => {
  if (!isOpen) return null;

  const totalAmount = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-black/50 backdrop-blur-xs flex justify-end animate-in fade-in duration-200">
      <div className="bg-white w-full max-w-md h-full flex flex-col shadow-2xl border-l border-stone-200 animate-in slide-in-from-right duration-300">
        {/* Header */}
        <div className="p-4 border-b border-stone-200 flex items-center justify-between bg-stone-50">
          <div className="flex items-center gap-2">
            <ShoppingBag className="w-5 h-5 text-emerald-600" />
            <h3 className="font-bold text-stone-900 text-base">আপনার শপিং ব্যাগ</h3>
            <span className="bg-emerald-100 text-emerald-800 text-xs font-bold px-2 py-0.5 rounded-full">
              {cart.reduce((cnt, it) => cnt + it.quantity, 0)} টি
            </span>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-stone-200 text-stone-500 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Cart Items List */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          {cart.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center text-center p-6 text-stone-400 space-y-3">
              <div className="w-16 h-16 rounded-full bg-stone-100 flex items-center justify-center text-stone-300">
                <ShoppingBag className="w-8 h-8" />
              </div>
              <div>
                <p className="text-base font-bold text-stone-700">কার্ট খালি রয়েছে</p>
                <p className="text-xs text-stone-500 mt-1">
                  বদলগাছীর স্থানীয় দোকানগুলো থেকে পছন্দের পণ্য যোগ করুন!
                </p>
              </div>
              <button
                onClick={onClose}
                className="mt-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-xs"
              >
                কেনাকাটা শুরু করুন
              </button>
            </div>
          ) : (
            cart.map((item) => (
              <div
                key={item.productId}
                className="flex items-center gap-3 p-3 rounded-2xl bg-stone-50 border border-stone-200"
              >
                <img
                  src={item.imageUrl}
                  alt={item.productName}
                  className="w-16 h-16 rounded-xl object-cover bg-white shrink-0 border border-stone-200"
                />

                <div className="flex-1 min-w-0">
                  <h4 className="text-xs font-bold text-stone-900 truncate">
                    {item.productName}
                  </h4>
                  <div className="text-xs font-bold text-emerald-700 mt-0.5">
                    ৳{item.price}{' '}
                    <span className="text-[10px] text-stone-400 font-normal">
                      x {item.quantity} = ৳{item.price * item.quantity}
                    </span>
                  </div>

                  {/* Quantity Stepper */}
                  <div className="flex items-center gap-2 mt-2">
                    <div className="flex items-center border border-stone-300 rounded-lg bg-white">
                      <button
                        onClick={() => onUpdateQuantity(item.productId, item.quantity - 1)}
                        className="px-2 py-0.5 text-xs text-stone-600 hover:bg-stone-100 font-bold"
                      >
                        -
                      </button>
                      <span className="px-2 text-xs font-bold text-stone-800">
                        {item.quantity}
                      </span>
                      <button
                        onClick={() => onUpdateQuantity(item.productId, item.quantity + 1)}
                        disabled={item.quantity >= item.maxStock}
                        className="px-2 py-0.5 text-xs text-stone-600 hover:bg-stone-100 font-bold disabled:opacity-40"
                      >
                        +
                      </button>
                    </div>

                    <button
                      onClick={() => onRemoveItem(item.productId)}
                      className="p-1 text-rose-500 hover:text-rose-700 transition-colors ml-auto"
                      title="মুছে ফেলুন"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Footer & Checkout */}
        {cart.length > 0 && (
          <div className="p-4 border-t border-stone-200 bg-stone-50 space-y-3">
            <div className="space-y-1.5 text-xs">
              <div className="flex justify-between text-stone-600">
                <span>পণ্যের মোট মূল্য:</span>
                <span className="font-bold text-stone-900">৳{totalAmount}</span>
              </div>
              <div className="flex justify-between text-stone-600">
                <span>বদলগাছী লোকাল ডেলিভারি:</span>
                <span className="text-emerald-700 font-semibold">কুরিয়ার/ডেলিভারিম্যান চার্জ প্রযোজ্য</span>
              </div>
              <div className="flex justify-between text-sm font-extrabold text-stone-900 pt-2 border-t border-stone-200">
                <span>সর্বমোট:</span>
                <span className="text-emerald-700 text-lg">৳{totalAmount}</span>
              </div>
            </div>

            <button
              onClick={() => {
                onClose();
                onProceedToCheckout();
              }}
              className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white font-bold text-xs rounded-xl shadow-md flex items-center justify-center gap-2 transition-all"
            >
              <span>অর্ডার সম্পন্ন করতে এগিয়ে যান</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
