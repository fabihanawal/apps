import React from 'react';
import {
  ShoppingBag,
  Store,
  Search,
  MapPin,
  X,
  Phone,
  CheckCircle2,
} from 'lucide-react';
import { BADALGACHHI_UNIONS } from '../types';

interface HeaderProps {
  cartCount: number;
  openCart: () => void;
  activeView: 'store' | 'vendor' | 'track' | 'schema';
  setActiveView: (view: 'store' | 'vendor' | 'track' | 'schema') => void;
  selectedUnion: string;
  setSelectedUnion: (union: string) => void;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  announcementText?: string;
  helplinePhone?: string;
}

export const Header: React.FC<HeaderProps> = ({
  cartCount,
  openCart,
  activeView,
  setActiveView,
  selectedUnion,
  setSelectedUnion,
  searchQuery,
  setSearchQuery,
  announcementText = 'বদলগাছী উপজেলার ৮টি ইউনিয়নে দ্রুততম হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা!',
  helplinePhone = '01755383039',
}) => {
  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur border-b border-stone-200 shadow-xs">
      {/* Top micro announcement bar */}
      <div className="bg-emerald-700 text-white text-xs py-1.5 px-4">
        <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-2">
          <div className="flex items-center gap-2">
            <span className="inline-flex items-center px-1.5 py-0.5 rounded bg-emerald-800 text-[11px] font-medium">
              বদলগাছী, নওগাঁ
            </span>
            <span className="hidden sm:inline text-emerald-100 font-medium">
              {announcementText}
            </span>
          </div>
          <div className="flex items-center gap-4 text-xs">
            <a
              href={`https://wa.me/88${helplinePhone.replace(/[^0-9]/g, '')}`}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-1.5 hover:text-emerald-200 transition-colors font-medium"
            >
              <Phone className="w-3.5 h-3.5" />
              <span>সহায়তা ও হোয়াটসঅ্যাপ: {helplinePhone}</span>
            </a>
            <span className="hidden md:inline text-emerald-300">|</span>
            <span className="hidden md:inline text-emerald-100 text-[11px]">
              Developed by <strong className="text-white">RSTS-BD</strong>
            </span>
          </div>
        </div>
      </div>

      {/* Main navigation header */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3">
        <div className="flex items-center justify-between gap-3 md:gap-6">
          {/* Logo & Platform Name */}
          <button
            onClick={() => setActiveView('store')}
            className="flex items-center gap-3 text-left focus:outline-hidden group"
          >
            <div className="w-10 h-10 sm:w-11 sm:h-11 rounded-xl bg-gradient-to-br from-emerald-600 to-teal-700 flex items-center justify-center text-white shadow-md shadow-emerald-700/20 group-hover:scale-105 transition-transform">
              <Store className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="text-xl sm:text-2xl font-bold tracking-tight text-stone-900 group-hover:text-emerald-700 transition-colors">
                  আমার দোকান
                </span>
                <span className="bg-amber-100 text-amber-800 text-[10px] font-bold px-1.5 py-0.5 rounded-full uppercase tracking-wider">
                  বদলগাছী
                </span>
              </div>
              <p className="text-[11px] text-stone-500 font-medium leading-none mt-0.5">
                উপজেলা ডিজিটাল হাট • নওগাঁ
              </p>
            </div>
          </button>

          {/* Search Bar */}
          <div className="flex-1 max-w-md hidden md:block">
            <div className="relative">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="মিষ্টি, পোড়ামাটির পাত্র, তাজা গুড় বা পণ্য খুঁজুন..."
                className="w-full pl-9 pr-8 py-2 bg-stone-100 hover:bg-stone-50 focus:bg-white text-sm rounded-xl border border-stone-200 focus:outline-hidden focus:ring-2 focus:ring-emerald-600 focus:border-transparent transition-all"
              />
              <Search className="w-4 h-4 text-stone-400 absolute left-3 top-2.5" />
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="absolute right-2.5 top-2.5 text-stone-400 hover:text-stone-600"
                >
                  <X className="w-4 h-4" />
                </button>
              )}
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center gap-2 sm:gap-3">
            {/* Union Quick Dropdown */}
            <div className="relative hidden lg:block">
              <div className="flex items-center gap-1 text-xs text-stone-600 bg-stone-100 hover:bg-stone-200/80 px-2.5 py-1.5 rounded-lg transition-colors border border-stone-200">
                <MapPin className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                <select
                  value={selectedUnion}
                  onChange={(e) => setSelectedUnion(e.target.value)}
                  className="bg-transparent text-xs font-semibold text-stone-800 focus:outline-hidden cursor-pointer"
                >
                  <option value="">সকল ইউনিয়ন</option>
                  {BADALGACHHI_UNIONS.map((u) => (
                    <option key={u} value={u}>
                      {u}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Order Tracking */}
            <button
              onClick={() => setActiveView('track')}
              className={`px-2.5 py-1.5 rounded-lg text-xs font-semibold border transition-colors flex items-center gap-1.5 ${
                activeView === 'track'
                  ? 'bg-emerald-50 border-emerald-300 text-emerald-700'
                  : 'border-stone-200 text-stone-700 hover:bg-stone-50'
              }`}
              title="অর্ডার ট্র্যাক করুন"
            >
              <CheckCircle2 className="w-4 h-4 text-emerald-600" />
              <span>ট্র্যাক অর্ডার</span>
            </button>

            {/* Cart Button */}
            <button
              onClick={openCart}
              className="relative p-2 sm:px-3 sm:py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white transition-colors flex items-center gap-1.5 shadow-sm font-semibold text-xs"
              aria-label="শপিং ব্যাগ"
            >
              <ShoppingBag className="w-4 h-4" />
              <span className="hidden sm:inline">ব্যাগ</span>
              {cartCount > 0 && (
                <span className="bg-amber-400 text-stone-900 text-[11px] font-black w-5 h-5 rounded-full flex items-center justify-center shadow-xs">
                  {cartCount}
                </span>
              )}
            </button>
          </div>
        </div>

        {/* Mobile search bar */}
        <div className="mt-2.5 md:hidden">
          <div className="relative">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="পণ্য বা দোকান খুঁজুন..."
              className="w-full pl-9 pr-8 py-2 bg-stone-100 text-xs rounded-xl border border-stone-200 focus:outline-hidden focus:ring-2 focus:ring-emerald-600"
            />
            <Search className="w-3.5 h-3.5 text-stone-400 absolute left-3 top-2.5" />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="absolute right-2.5 top-2.5 text-stone-400"
              >
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
