import React, { useState } from 'react';
import { BADALGACHHI_UNIONS, PRODUCT_CATEGORIES, Shop } from '../types';
import { X, Store, CheckCircle, ShieldCheck } from 'lucide-react';

interface ShopRegistrationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onRegisterShop: (
    shop: Omit<Shop, 'shopId' | 'status' | 'createdAt' | 'rating' | 'totalReviews'>
  ) => Shop;
  onOpenVendorLogin?: (email?: string) => void;
}

export const ShopRegistrationModal: React.FC<ShopRegistrationModalProps> = ({
  isOpen,
  onClose,
  onRegisterShop,
  onOpenVendorLogin,
}) => {
  const [shopName, setShopName] = useState('');
  const [ownerName, setOwnerName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [address, setAddress] = useState('');
  const [union, setUnion] = useState(BADALGACHHI_UNIONS[0]);
  const [category, setCategory] = useState(PRODUCT_CATEGORIES[1]);
  const [description, setDescription] = useState('');
  const [logoUrl, setLogoUrl] = useState('');
  const [registeredShop, setRegisteredShop] = useState<Shop | null>(null);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!shopName.trim() || !ownerName.trim() || !phone.trim() || !email.trim() || !address.trim()) {
      return;
    }

    const newShop = onRegisterShop({
      shopName: shopName.trim(),
      ownerName: ownerName.trim(),
      phone: phone.trim(),
      email: email.trim().toLowerCase(),
      address: address.trim(),
      union,
      upazila: 'বদলগাছী',
      district: 'নওগাঁ',
      category,
      description: description.trim() || 'বদলগাছীর বিশ্বস্ত স্থানীয় প্রতিষ্ঠান।',
      logoUrl:
        logoUrl.trim() ||
        'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?w=500&auto=format&fit=crop&q=60',
    });

    setRegisteredShop(newShop);
  };

  const handleCloseAndReset = () => {
    setRegisteredShop(null);
    setShopName('');
    setOwnerName('');
    setPhone('');
    setEmail('');
    setAddress('');
    setDescription('');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-black/60 backdrop-blur-xs flex items-center justify-center p-3 sm:p-4">
      <div className="bg-white w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden border border-stone-200 relative my-8 animate-in fade-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="p-5 border-b border-stone-200 flex items-center justify-between bg-stone-50">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-xl bg-emerald-600 text-white">
              <Store className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-stone-900">
                {registeredShop ? 'আবেদন সফলভাবে জমা হয়েছে! ⏳' : 'নতুন দোকানদার নিবন্ধন আবেদন'}
              </h3>
              <p className="text-xs text-stone-500">
                বদলগাছী, নওগাঁর স্থানীয় অনলাইন বাজার "আমার দোকান"
              </p>
            </div>
          </div>
          <button
            onClick={handleCloseAndReset}
            className="p-1.5 rounded-lg hover:bg-stone-200 text-stone-500 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Body */}
        <div className="p-6">
          {registeredShop ? (
            <div className="text-center space-y-4 py-2">
              <div className="w-16 h-16 bg-amber-100 text-amber-600 rounded-full flex items-center justify-center mx-auto shadow-inner">
                <CheckCircle className="w-9 h-9" />
              </div>
              <div>
                <span className="inline-block bg-amber-100 text-amber-900 text-xs font-bold px-3 py-1 rounded-full mb-2">
                  ⏳ অ্যাডমিন অনুমোদনের অপেক্ষায় (Pending)
                </span>
                <h4 className="text-lg font-bold text-stone-900">
                  ধন্যবাদ! "{registeredShop.shopName}" এর আবেদনটি সফলভাবে জমা হয়েছে।
                </h4>
                <p className="text-xs text-stone-600 mt-2 leading-relaxed max-w-md mx-auto">
                  আপনার আবেদনটি অ্যাডমিন দ্বারা যাচাই ও কনফার্ম হওয়া মাত্রই আপনি আপনার ইমেইল এড্রেস দিয়ে অটো লগইন করতে পারবেন এবং আপনার নিজস্ব ড্যাশবোর্ড ব্যবহারের সুযোগ পাবেন যার মাধ্যমে আপনি আপনার পণ্য আপলোড ও বিক্রি করতে পারবেন।
                </p>
              </div>

              <div className="bg-stone-50 p-4 rounded-2xl border border-stone-200 text-xs text-stone-700 space-y-1.5 text-left">
                <p className="flex justify-between border-b border-stone-200 pb-1">
                  <span className="text-stone-500">দোকানের নাম:</span>
                  <strong className="text-stone-900">{registeredShop.shopName}</strong>
                </p>
                <p className="flex justify-between border-b border-stone-200 pb-1">
                  <span className="text-stone-500">মালিকের নাম:</span>
                  <strong className="text-stone-900">{registeredShop.ownerName}</strong>
                </p>
                <p className="flex justify-between border-b border-stone-200 pb-1">
                  <span className="text-stone-500">মোবাইল নম্বর:</span>
                  <strong className="text-stone-900">{registeredShop.phone}</strong>
                </p>
                <p className="flex justify-between border-b border-stone-200 pb-1">
                  <span className="text-stone-500">লগইন ইমেইল:</span>
                  <strong className="text-emerald-700 font-mono">{registeredShop.email}</strong>
                </p>
                <p className="flex justify-between border-b border-stone-200 pb-1">
                  <span className="text-stone-500">ঠিকানা ও ইউনিয়ন:</span>
                  <strong className="text-stone-900">{registeredShop.address}, {registeredShop.union}</strong>
                </p>
                <p className="flex justify-between pt-0.5">
                  <span className="text-stone-500">বর্তমান স্ট্যাটাস:</span>
                  <span className="font-bold text-amber-700 bg-amber-50 px-2 py-0.5 rounded border border-amber-200">
                    অপেক্ষমাণ (Pending Admin Approval)
                  </span>
                </p>
              </div>

              <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-200 text-left text-xs text-emerald-900 flex items-start gap-2">
                <ShieldCheck className="w-4 h-4 text-emerald-700 shrink-0 mt-0.5" />
                <p>
                  <strong>এডমিন সাপোর্ট ও দ্রুত ভেরিফিকেশন:</strong> আবেদন অনুমোদনে কোনো প্রশ্ন থাকলে কল করুন <a href="tel:01755383039" className="font-bold underline">01755383039</a> (RSTS-BD)।
                </p>
              </div>

              <div className="flex flex-col sm:flex-row gap-2 pt-2">
                {onOpenVendorLogin && (
                  <button
                    onClick={() => {
                      handleCloseAndReset();
                      onOpenVendorLogin(registeredShop.email);
                    }}
                    className="flex-1 py-2.5 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-bold rounded-xl transition-colors shadow-xs"
                  >
                    ইমেইল দিয়ে লগইন চেক করুন
                  </button>
                )}
                <button
                  onClick={handleCloseAndReset}
                  className="flex-1 py-2.5 bg-stone-200 hover:bg-stone-300 text-stone-800 text-xs font-bold rounded-xl transition-colors"
                >
                  বুঝেছি, বন্ধ করুন
                </button>
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-3.5">
              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  দোকানের নাম <span className="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={shopName}
                  onChange={(e) => setShopName(e.target.value)}
                  placeholder="যেমন: পাহাড়পুর মাটির মায়া হস্তশিল্প"
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    মালিকের নাম <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="text"
                    required
                    value={ownerName}
                    onChange={(e) => setOwnerName(e.target.value)}
                    placeholder="যেমন: মো: আব্দুল করিম"
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    মোবাইল নম্বর <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="tel"
                    required
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="যেমন: 01755383039"
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                  />
                </div>
              </div>

              {/* Email address input for login */}
              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  ইমেইল এড্রেস (লগইনের জন্য ব্যবহৃত হবে) <span className="text-rose-500">*</span>
                </label>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="যেমন: karim.crafts@gmail.com"
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-mono text-stone-900"
                />
                <span className="text-[10px] text-stone-500 block mt-0.5">
                  💡 আবেদন অনুমোদিত হওয়ার পর এই ইমেইল দিয়ে আপনি সরাসরি আপনার ড্যাশবোর্ডে প্রবেশ করবেন।
                </span>
              </div>

              {/* Detailed Address */}
              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  দোকানের ঠিকানা (গ্রাম/মহল্লা, বাজার, রোড) <span className="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  placeholder="যেমন: পাহাড়পুর বাজার কেন্দ্রীয় মসজিদ সংলগ্ন, বৌদ্ধবিহার রোড"
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-stone-700 mb-1">
                    বদলগাছী ইউনিয়ন <span className="text-rose-500">*</span>
                  </label>
                  <select
                    value={union}
                    onChange={(e) => setUnion(e.target.value as any)}
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-medium"
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
                    দোকানের ক্যাটাগরি
                  </label>
                  <select
                    value={category}
                    onChange={(e) => setCategory(e.target.value as any)}
                    className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500 font-medium"
                  >
                    {PRODUCT_CATEGORIES.filter((c) => c !== 'সব ক্যাটাগরি').map((c) => (
                      <option key={c} value={c}>
                        {c}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-stone-700 mb-1">
                  দোকানের বিবরণ ও বিশেষত্ব
                </label>
                <textarea
                  rows={2}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="আপনার দোকানের বিশেষ পণ্য বা বৈশিষ্ট্য..."
                  className="w-full p-2.5 text-xs bg-stone-50 border border-stone-300 rounded-xl focus:ring-2 focus:ring-emerald-500"
                />
              </div>

              <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-100 flex items-center gap-2 text-[11px] text-emerald-800">
                <ShieldCheck className="w-4 h-4 text-emerald-600 shrink-0" />
                <span>
                  প্ল্যাটফর্ম পলিসি: সফল বিক্রির উপর ৫% কমিশন ধার্য হবে। আবেদন জমা হলে অ্যাডমিন (RSTS-BD: 01755383039) কর্তৃক যাচাই করে অনুমোদন দেওয়া হবে।
                </span>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 bg-stone-100 hover:bg-stone-200 text-stone-700 rounded-xl text-xs font-bold"
                >
                  বাতিল
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold shadow-xs"
                >
                  আবেদন জমা দিন ➔
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};
