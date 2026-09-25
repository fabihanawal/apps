import React from 'react';
import { Store, Phone, MapPin, MessageCircle, Heart, ShieldCheck, Truck, ShoppingBag, CheckCircle2, RotateCcw } from 'lucide-react';

interface FooterProps {
  onSelectCategory?: (category: string) => void;
  onOpenAdmin: () => void;
  helplinePhone?: string;
  whatsappPhone?: string;
}

export const Footer: React.FC<FooterProps> = ({
  onSelectCategory,
  onOpenAdmin,
  helplinePhone = '01755383039',
  whatsappPhone = '01755383039',
}) => {
  const [heartClicks, setHeartClicks] = React.useState(0);
  const clickTimeoutRef = React.useRef<NodeJS.Timeout | null>(null);

  const handleHeartClick = () => {
    setHeartClicks((prev) => {
      const next = prev + 1;
      if (next >= 3) {
        onOpenAdmin();
        return 0;
      }
      return next;
    });

    if (clickTimeoutRef.current) {
      clearTimeout(clickTimeoutRef.current);
    }

    clickTimeoutRef.current = setTimeout(() => {
      setHeartClicks(0);
    }, 2000);
  };

  return (
    <footer className="bg-stone-900 text-stone-300 pt-12 pb-8 border-t border-stone-800 mt-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-12">
          {/* Col 1: Platform & Branding */}
          <div className="space-y-4">
            <div className="flex items-center gap-2.5">
              <div className="w-10 h-10 rounded-xl bg-emerald-600 flex items-center justify-center text-white font-bold shadow-md">
                <Store className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xl font-bold text-white tracking-tight">
                  আমার দোকান
                </span>
                <span className="block text-[11px] text-emerald-400 font-medium">
                  বদলগাছী, নওগাঁর স্থানীয় অনলাইন হাট
                </span>
              </div>
            </div>

            <p className="text-xs text-stone-400 leading-relaxed">
              বদলগাছী উপজেলার ৮টি ইউনিয়নের প্রান্তিক উদ্যোক্তা ও স্থানীয় কারিগরদের খাঁটি পণ্য সরাসরি আপনার দোরগোড়ায় পৌঁছে দেওয়ার বিশ্বস্ত মাধ্যম। ক্যাশ অন ডেলিভারিতে ঝামেলাহীন কেনাকাটা করুন।
            </p>

            <div className="pt-2">
              <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-emerald-950 border border-emerald-800/60 text-emerald-300 text-xs">
                <ShieldCheck className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>RSTS-BD পরিচালিত নিরাপদ স্থানীয় প্ল্যাটফর্ম</span>
              </div>
            </div>
          </div>

          {/* Col 2: Customer Care & Shopping Benefits */}
          <div className="space-y-3">
            <h4 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-1.5">
              <ShoppingBag className="w-4 h-4 text-emerald-400" />
              <span>গ্রাহক সেবা ও সুবিধা</span>
            </h4>
            <ul className="space-y-2.5 text-xs text-stone-400">
              <li className="flex items-start gap-2">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
                <span>১০০% খাঁটি ও তাজা স্থানীয় পণ্যের নিশ্চয়তা</span>
              </li>
              <li className="flex items-start gap-2">
                <Truck className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
                <span>দ্রুততম হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি</span>
              </li>
              <li className="flex items-start gap-2">
                <RotateCcw className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
                <span>পণ্য দেখে মূল্য পরিশোধ ও সহজ রিটার্ন সুবিধা</span>
              </li>
              <li className="flex items-start gap-2">
                <ShieldCheck className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
                <span>যাচাইকৃত স্থানীয় বিশ্বস্ত দোকানদার ও খামারি</span>
              </li>
            </ul>
          </div>

          {/* Col 3: Popular Local Specialities */}
          <div className="space-y-3">
            <h4 className="text-sm font-bold text-white uppercase tracking-wider">
              বদলগাছীর জনপ্রিয় পণ্য
            </h4>
            <div className="flex flex-col space-y-2 text-xs text-stone-400">
              <span className="hover:text-emerald-400 cursor-default">
                🍯 খাঁটি মিষ্টি, চমচম ও সুস্বাদু দই
              </span>
              <span className="hover:text-emerald-400 cursor-default">
                🏺 ঐতিহাসিক পাহাড়পুর পোড়ামাটির মৃৎশিল্প
              </span>
              <span className="hover:text-emerald-400 cursor-default">
                🌾 স্থানীয় কৃষকদের তাজা শাকসবজি ও ফলমূল
              </span>
              <span className="hover:text-emerald-400 cursor-default">
                🛍️ দেশি তেল, মসলা ও দৈনন্দিন গ্রোসারি
              </span>
              <span className="hover:text-emerald-400 cursor-default">
                🍗 ফ্রেশ দেশি মুরগি ও ডিম
              </span>
            </div>
          </div>

          {/* Col 4: Contact & Developed By */}
          <div className="space-y-4">
            <h4 className="text-sm font-bold text-white uppercase tracking-wider">
              যোগাযোগ ও হেল্পলাইন
            </h4>

            <div className="space-y-2.5 text-xs text-stone-300">
              <p className="flex items-center gap-2">
                <Phone className="w-4 h-4 text-emerald-400 shrink-0" />
                <a href={`tel:${helplinePhone}`} className="hover:text-white font-bold">
                  {helplinePhone}
                </a>
              </p>

              <p className="flex items-center gap-2">
                <MessageCircle className="w-4 h-4 text-emerald-400 shrink-0" />
                <a
                  href={`https://wa.me/88${whatsappPhone.replace(/[^0-9]/g, '')}`}
                  target="_blank"
                  rel="noreferrer"
                  className="hover:text-white font-bold text-emerald-400"
                >
                  WhatsApp: {whatsappPhone}
                </a>
              </p>

              <p className="text-stone-400 leading-snug">
                বদলগাছী সদর, নওগাঁ - ৬৫৭০, রাজশাহী বিভাগ, বাংলাদেশ।
              </p>
            </div>

            {/* Developed By Badge */}
            <div className="p-3 rounded-2xl bg-stone-800/80 border border-stone-700 space-y-1">
              <span className="text-[11px] text-stone-400 block font-medium">
                কারিগরি তত্ত্বাবধানে ও নির্মাণে:
              </span>
              <p className="text-xs font-bold text-white tracking-wide">
                Developed by <span className="text-emerald-400">RSTS-BD</span> (আরএসটিএস-বিডি)
              </p>
              <p className="text-[10px] text-stone-400">
                Contact: <strong className="text-emerald-300">{helplinePhone}</strong>
              </p>
            </div>
          </div>
        </div>

        {/* Bottom copyright & attribution */}
        <div className="pt-6 border-t border-stone-800 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-stone-500">
          <p>
            © {new Date().getFullYear()} আমার দোকান (Amar Dokan) • বদলগাছী, নওগাঁ। সর্বস্বত্ব সংরক্ষিত।
          </p>

          <p className="flex items-center gap-1.5 font-medium text-stone-400 select-none">
            <span>গর্বের সাথে তৈরি</span>
            <button
              type="button"
              onClick={handleHeartClick}
              title="RSTS-BD Admin Access"
              className="p-1 rounded-full hover:bg-stone-800 transition-transform active:scale-90 group relative cursor-pointer"
            >
              <Heart
                className={`w-4 h-4 text-rose-500 fill-rose-500 transition-all ${
                  heartClicks > 0 ? 'scale-125 animate-ping' : 'hover:scale-125'
                }`}
              />
              {heartClicks > 0 && (
                <span className="absolute -top-6 left-1/2 -translate-x-1/2 bg-rose-600 text-white text-[9px] font-bold px-1.5 py-0.5 rounded-full shadow-lg">
                  {heartClicks}/3
                </span>
              )}
            </button>
            <span>
              Developed by <strong className="text-emerald-400">RSTS-BD</strong> | {helplinePhone}
            </span>
          </p>
        </div>
      </div>
    </footer>
  );
};
