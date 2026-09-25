import React, { useState } from 'react';
import { Shop } from '../types';
import {
  Mail,
  Store,
  CheckCircle2,
  AlertCircle,
  X,
  PlusCircle,
  ArrowRight,
  ShieldCheck,
  UserCheck,
} from 'lucide-react';

interface VendorLoginModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLoginByEmail: (email: string) => {
    success: boolean;
    status?: string;
    shop?: Shop;
    message: string;
  };
  onOpenRegistration: () => void;
  onSuccessLogin?: (shop: Shop) => void;
  shops: Shop[];
}

export const VendorLoginModal: React.FC<VendorLoginModalProps> = ({
  isOpen,
  onClose,
  onLoginByEmail,
  onOpenRegistration,
  onSuccessLogin,
  shops,
}) => {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState<{ type: 'error' | 'warning' | 'success'; text: string } | null>(null);

  if (!isOpen) return null;

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;

    setMessage(null);
    const result = onLoginByEmail(email.trim());

    if (result.success && result.shop) {
      setMessage({ type: 'success', text: result.message });
      setTimeout(() => {
        if (onSuccessLogin && result.shop) onSuccessLogin(result.shop);
        onClose();
      }, 700);
    } else if (result.status === 'Pending') {
      setMessage({ type: 'warning', text: result.message });
    } else {
      setMessage({ type: 'error', text: result.message });
    }
  };

  const handleQuickFill = (demoEmail: string) => {
    setEmail(demoEmail);
    setMessage(null);
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/65 backdrop-blur-xs flex items-center justify-center p-3 sm:p-4">
      <div className="bg-white w-full max-w-md rounded-3xl shadow-2xl overflow-hidden border border-stone-200 relative my-6 animate-in fade-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="p-5 border-b border-stone-200 flex items-center justify-between bg-stone-50">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-emerald-600 text-white shadow-xs">
              <Store className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-stone-900">
                দোকানদার লগইন (ইমেইল এড্রেস)
              </h3>
              <p className="text-xs text-stone-500">
                আপনার নিবন্ধিত ইমেইল দিয়ে ড্যাশবোর্ডে প্রবেশ করুন
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-stone-200 text-stone-500 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Body */}
        <div className="p-6 space-y-4">
          {message && (
            <div
              className={`p-3 rounded-2xl text-xs flex items-start gap-2 ${
                message.type === 'success'
                  ? 'bg-emerald-50 border border-emerald-300 text-emerald-900'
                  : message.type === 'warning'
                  ? 'bg-amber-50 border border-amber-300 text-amber-900'
                  : 'bg-rose-50 border border-rose-300 text-rose-900'
              }`}
            >
              {message.type === 'success' ? (
                <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600 mt-0.5" />
              ) : (
                <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
              )}
              <p className="leading-relaxed font-medium">{message.text}</p>
            </div>
          )}

          <form onSubmit={handleLogin} className="space-y-4">
            <div>
              <label className="block text-xs font-bold text-stone-700 mb-1">
                নিবন্ধিত ইমেইল এড্রেস <span className="text-rose-500">*</span>
              </label>
              <div className="relative">
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="যেমন: paharpur.crafts@gmail.com"
                  className="w-full pl-9 pr-3 py-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-mono text-stone-900"
                />
                <Mail className="w-4 h-4 text-stone-400 absolute left-3 top-3" />
              </div>
              <span className="text-[11px] text-stone-500 block mt-1">
                💡 আবেদন করার সময় যে ইমেইল দিয়েছিলেন সেটি লিখুন। অ্যাডমিন কর্তৃক কনফার্ম হলে প্রবেশাধিকার পাবেন।
              </span>
            </div>

            <button
              type="submit"
              className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl transition-colors shadow-md flex items-center justify-center gap-2"
            >
              <UserCheck className="w-4 h-4" />
              <span>ড্যাশবোর্ডে প্রবেশ করুন</span>
            </button>
          </form>

          {/* New Shop Registration Prompt */}
          <div className="pt-3 border-t border-stone-100 flex items-center justify-between text-xs">
            <span className="text-stone-500">এখনও আবেদন করেননি?</span>
            <button
              onClick={() => {
                onClose();
                onOpenRegistration();
              }}
              className="text-emerald-700 font-bold hover:underline flex items-center gap-1"
            >
              <PlusCircle className="w-3.5 h-3.5" />
              <span>নতুন দোকান নিবন্ধন আবেদন ➔</span>
            </button>
          </div>

          {/* Quick Demo Logins helper */}
          <div className="p-3 bg-stone-50 rounded-2xl border border-stone-200 text-[11px] space-y-2">
            <span className="font-bold text-stone-700 block">
              দ্রুত পরীক্ষার জন্য বর্তমান দোকানদের ইমেইল:
            </span>
            <div className="flex flex-wrap gap-1.5">
              {shops.slice(0, 4).map((s) => (
                <button
                  key={s.shopId}
                  type="button"
                  onClick={() => handleQuickFill(s.email)}
                  className={`px-2 py-1 rounded-md text-[10px] font-mono border transition-colors ${
                    s.status === 'Pending'
                      ? 'bg-amber-50 text-amber-800 border-amber-300 hover:bg-amber-100'
                      : 'bg-white text-stone-700 border-stone-300 hover:border-emerald-500'
                  }`}
                  title={`${s.shopName} (${s.status})`}
                >
                  {s.email} {s.status === 'Pending' ? '⏳(Pending)' : ''}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
