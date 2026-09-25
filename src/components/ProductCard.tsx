import React from 'react';
import { Product, Shop } from '../types';
import { ShoppingCart, Eye, MapPin, Store, MessageCircle } from 'lucide-react';

interface ProductCardProps {
  product: Product;
  shop?: Shop;
  onAddToCart: (product: Product) => void;
  onViewDetails: (product: Product) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({
  product,
  shop,
  onAddToCart,
  onViewDetails,
}) => {
  const hasDiscount = product.discountPrice && product.discountPrice < product.price;
  const discountPercent = hasDiscount
    ? Math.round(((product.price - product.discountPrice!) / product.price) * 100)
    : 0;

  const isOutOfStock = product.stock <= 0;
  const shopPhone = shop?.phone || '01755383039';

  const handleWhatsApp = (e: React.MouseEvent) => {
    e.stopPropagation();
    const text = encodeURIComponent(
      `আসসালামু আলাইকুম, আমি "আমার দোকান" প্ল্যাটফর্ম থেকে "${product.productName}" (দোকান: ${shop?.shopName || 'আমার দোকান'}) সম্পর্কে জানতে চাই। দাম: ৳${product.discountPrice || product.price}।`
    );
    window.open(`https://wa.me/88${shopPhone.replace(/[^0-9]/g, '')}?text=${text}`, '_blank');
  };

  return (
    <div className="group bg-white rounded-2xl border border-stone-200 hover:border-emerald-400 hover:shadow-lg transition-all duration-200 overflow-hidden flex flex-col justify-between">
      {/* Product Image & Badges */}
      <div className="relative aspect-square overflow-hidden bg-stone-100 cursor-pointer" onClick={() => onViewDetails(product)}>
        <img
          src={product.imageUrl}
          alt={product.productName}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          loading="lazy"
        />

        {/* Discount Badge */}
        {hasDiscount && (
          <div className="absolute top-2.5 left-2.5 bg-rose-600 text-white text-[11px] font-bold px-2 py-0.5 rounded-md shadow-sm">
            {discountPercent}% ছাড়
          </div>
        )}

        {/* Stock Status Badge */}
        <div className="absolute top-2.5 right-2.5">
          {isOutOfStock ? (
            <span className="bg-stone-900/80 backdrop-blur text-white text-[10px] font-semibold px-2 py-0.5 rounded-md">
              স্টক শেষ
            </span>
          ) : product.stock < 10 ? (
            <span className="bg-amber-600 text-white text-[10px] font-bold px-2 py-0.5 rounded-md shadow-xs">
              আর মাত্র {product.stock} টি
            </span>
          ) : null}
        </div>

        {/* Quick View Hover Action */}
        <div className="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
          <button
            onClick={(e) => {
              e.stopPropagation();
              onViewDetails(product);
            }}
            className="p-2.5 rounded-full bg-white text-stone-900 hover:bg-emerald-500 hover:text-white transition-colors shadow-lg"
            title="বিস্তারিত দেখুন"
          >
            <Eye className="w-4 h-4" />
          </button>
          <button
            onClick={handleWhatsApp}
            className="p-2.5 rounded-full bg-emerald-600 text-white hover:bg-emerald-700 transition-colors shadow-lg"
            title="দোকানদারের সাথে WhatsApp-এ কথা বলুন"
          >
            <MessageCircle className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Card Body */}
      <div className="p-4 flex flex-col flex-1 justify-between">
        <div className="space-y-2">
          {/* Shop & Union Information */}
          <div className="flex items-center justify-between text-xs text-stone-500 gap-2">
            <div className="flex items-center gap-1 truncate">
              <Store className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
              <span className="truncate font-medium text-stone-700 hover:text-emerald-700">
                {shop?.shopName || 'লোকাল বিক্রেতা'}
              </span>
            </div>
            {shop?.union && (
              <span className="shrink-0 inline-flex items-center gap-0.5 text-[11px] bg-stone-100 text-stone-600 px-1.5 py-0.5 rounded-md border border-stone-200">
                <MapPin className="w-3 h-3 text-emerald-600" />
                <span>{shop.union}</span>
              </span>
            )}
          </div>

          {/* Product Title */}
          <h3
            onClick={() => onViewDetails(product)}
            className="font-semibold text-stone-900 text-sm sm:text-base leading-snug line-clamp-2 cursor-pointer hover:text-emerald-700 transition-colors"
            title={product.productName}
          >
            {product.productName}
          </h3>

          {/* Unit info if present */}
          {product.unit && (
            <p className="text-xs text-stone-500">পরিমাণ: {product.unit}</p>
          )}
        </div>

        {/* Pricing & CTA */}
        <div className="pt-3 mt-2 border-t border-stone-100 flex items-center justify-between gap-2">
          <div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-lg font-bold text-emerald-700">
                ৳{hasDiscount ? product.discountPrice : product.price}
              </span>
              {hasDiscount && (
                <span className="text-xs text-stone-400 line-through">
                  ৳{product.price}
                </span>
              )}
            </div>
            <p className="text-[10px] text-stone-400 font-medium">ক্যাশ অন ডেলিভারি</p>
          </div>

          <button
            onClick={() => onAddToCart(product)}
            disabled={isOutOfStock}
            className={`px-3 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 shadow-xs ${
              isOutOfStock
                ? 'bg-stone-200 text-stone-400 cursor-not-allowed'
                : 'bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white'
            }`}
          >
            <ShoppingCart className="w-3.5 h-3.5" />
            <span>{isOutOfStock ? 'স্টক নেই' : 'কার্টে নিন'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
