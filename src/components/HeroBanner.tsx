import React from 'react';
import { MapPin, ShieldCheck, Truck, Sparkles, Phone, ArrowRight, Store } from 'lucide-react';
import { BADALGACHHI_UNIONS } from '../types';

interface HeroBannerProps {
  selectedUnion: string;
  setSelectedUnion: (union: string) => void;
  totalShops: number;
  totalProducts: number;
  onRegisterShopClick: () => void;
  heroHeadline?: string;
  heroSubheadline?: string;
}

export const HeroBanner: React.FC<HeroBannerProps> = ({
  selectedUnion,
  setSelectedUnion,
  totalShops,
  totalProducts,
  onRegisterShopClick,
  heroHeadline = 'ঘরের কাছে সেরা পণ্য, আমার দোকান এ সরাসরি অর্ডার!',
  heroSubheadline = 'ঐতিহাসিক পাহাড়পুর থেকে শুরু করে কোলা, বালুভরা ও সদর ইউনিয়নের বিশ্বস্ত উদ্যোক্তাদের তৈরি খাঁটি মিষ্টি, হস্তশিল্প, তাজা কৃষিপণ্য ও গ্রোসারি। স্থানীয়ভাবে দ্রুত হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা।',
}) => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-br from-emerald-900 via-teal-900 to-stone-900 text-white py-10 sm:py-14 px-4 sm:px-6 mb-8 rounded-2xl shadow-xl border border-emerald-800/40">
      {/* Subtle decorative background pattern */}
      <div className="absolute inset-0 opacity-10 bg-[radial-gradient(#10b981_1px,transparent_1px)] [background-size:16px_16px] pointer-events-none" />
      <div className="absolute -right-16 -top-16 w-80 h-80 bg-emerald-500/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -left-16 -bottom-16 w-80 h-80 bg-teal-500/20 rounded-full blur-3xl pointer-events-none" />

      <div className="relative max-w-7xl mx-auto">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          {/* Left Text & Value Proposition */}
          <div className="lg:col-span-7 space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/20 border border-emerald-400/30 text-emerald-300 text-xs font-semibold">
              <Sparkles className="w-3.5 h-3.5 text-amber-300 animate-pulse" />
              <span>বদলগাছী, নওগাঁর নিজস্ব হাইপার-লোকাল অনলাইন বাজার</span>
            </div>

            <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight leading-tight">
              {heroHeadline}
            </h1>

            <p className="text-sm sm:text-base text-stone-200 leading-relaxed max-w-2xl font-normal">
              {heroSubheadline}
            </p>

            {/* Quick stats & local trust tags */}
            <div className="pt-2 flex flex-wrap items-center gap-4 text-xs font-medium text-emerald-100">
              <div className="flex items-center gap-1.5 bg-white/10 backdrop-blur px-3 py-1.5 rounded-lg border border-white/10">
                <Store className="w-4 h-4 text-amber-300" />
                <span>{totalShops}+ নিবন্ধিত স্থানীয় দোকান</span>
              </div>
              <div className="flex items-center gap-1.5 bg-white/10 backdrop-blur px-3 py-1.5 rounded-lg border border-white/10">
                <Truck className="w-4 h-4 text-emerald-300" />
                <span>দ্রুত ইউনিয়ন ও হোম ডেলিভারি</span>
              </div>
              <div className="flex items-center gap-1.5 bg-white/10 backdrop-blur px-3 py-1.5 rounded-lg border border-white/10">
                <ShieldCheck className="w-4 h-4 text-teal-300" />
                <span>RSTS-BD ভেরিফাইড প্ল্যাটফর্ম</span>
              </div>
            </div>

            {/* Action buttons */}
            <div className="pt-2 flex flex-wrap gap-3">
              <a
                href="#products-section"
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-stone-950 font-bold text-sm transition-all shadow-lg shadow-emerald-500/30 hover:scale-[1.02]"
              >
                <span>পণ্য দেখুন ও কিনুন</span>
                <ArrowRight className="w-4 h-4" />
              </a>

              <button
                onClick={onRegisterShopClick}
                className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-white/10 hover:bg-white/20 border border-white/20 text-white font-semibold text-sm transition-all"
              >
                <Store className="w-4 h-4 text-amber-300" />
                <span>আপনার দোকান যুক্ত করুন</span>
              </button>
            </div>
          </div>

          {/* Right Hyper-Local Union Filter Box */}
          <div className="lg:col-span-5">
            <div className="bg-white/10 backdrop-blur-md p-5 sm:p-6 rounded-2xl border border-white/20 shadow-2xl space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="p-2 rounded-lg bg-emerald-500/30 text-emerald-300">
                    <MapPin className="w-5 h-5" />
                  </div>
                  <div>
                    <h2 className="text-base font-bold text-white">ইউনিয়নভিত্তিক পণ্য বাছাই</h2>
                    <p className="text-xs text-emerald-200">উপজেলা: বদলগাছী | জেলা: নওগাঁ</p>
                  </div>
                </div>
                {selectedUnion && (
                  <button
                    onClick={() => setSelectedUnion('')}
                    className="text-xs text-amber-300 hover:underline font-medium"
                  >
                    সব ইউনিয়ন দেখুন
                  </button>
                )}
              </div>

              <p className="text-xs text-stone-200">
                আপনার এলাকার স্থানীয় দোকানদারদের দেখতে আপনার ইউনিয়ন সিলেক্ট করুন:
              </p>

              {/* Union Chips Grid */}
              <div className="grid grid-cols-2 sm:grid-cols-2 gap-2">
                <button
                  onClick={() => setSelectedUnion('')}
                  className={`px-3 py-2 rounded-xl text-xs font-semibold text-left transition-all border ${
                    selectedUnion === ''
                      ? 'bg-emerald-500 text-stone-950 border-emerald-400 shadow-md font-bold'
                      : 'bg-stone-900/50 hover:bg-stone-800/80 text-stone-200 border-stone-700/60'
                  }`}
                >
                  🌐 সমগ্র বদলগাছী (সব)
                </button>
                {BADALGACHHI_UNIONS.map((union) => (
                  <button
                    key={union}
                    onClick={() => setSelectedUnion(union)}
                    className={`px-3 py-2 rounded-xl text-xs font-medium text-left transition-all border truncate ${
                      selectedUnion === union
                        ? 'bg-emerald-500 text-stone-950 border-emerald-400 shadow-md font-bold'
                        : 'bg-stone-900/50 hover:bg-stone-800/80 text-stone-200 border-stone-700/60'
                    }`}
                  >
                    📍 {union}
                  </button>
                ))}
              </div>

              <div className="pt-2 border-t border-white/10 flex items-center justify-between text-xs text-stone-300">
                <span>সার্ভিস হটলাইন:</span>
                <a
                  href="tel:01755383039"
                  className="text-amber-300 hover:text-amber-200 font-bold flex items-center gap-1"
                >
                  <Phone className="w-3.5 h-3.5" />
                  <span>01755383039</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
