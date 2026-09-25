import React, { useState } from 'react';
import { Database, ShieldCheck, Smartphone, Copy, Check, Code, Layers } from 'lucide-react';

interface FirebaseArchitectureModalProps {
  onClose?: () => void;
}

export const FirebaseArchitectureModal: React.FC<FirebaseArchitectureModalProps> = () => {
  const [activeTab, setActiveTab] = useState<'schema' | 'rules' | 'flutter' | 'web'>('schema');
  const [copiedKey, setCopiedKey] = useState<string | null>(null);

  const handleCopy = (text: string, key: string) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(key);
    setTimeout(() => setCopiedKey(null), 2500);
  };

  const firestoreRulesCode = `rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isShopOwner(shopId) {
      return isAuthenticated() && (
        request.auth.uid == shopId || 
        resource.data.ownerId == request.auth.uid ||
        request.auth.token.phone == resource.data.phone
      );
    }

    // 1. Shops Collection (বদলগাছী, নওগাঁর দোকানসমূহ)
    match /shops/{shopId} {
      allow read: if true;
      allow create: if isAuthenticated();
      allow update, delete: if isShopOwner(shopId);
    }

    // 2. Products Collection (পণ্যের তালিকা)
    match /products/{productId} {
      allow read: if true;
      allow create: if isAuthenticated() && request.resource.data.shopId != null;
      allow update, delete: if isAuthenticated() && (
        request.auth.uid == resource.data.shopId ||
        request.resource.data.shopId == resource.data.shopId
      );
    }

    // 3. Orders Collection (অর্ডার ও কমিশন ম্যানেজমেন্ট)
    match /orders/{orderId} {
      allow create: if true; // Allows local guest checkout or authenticated customer
      allow read: if true;
      allow update: if isAuthenticated() || request.resource.data.diff(resource.data).affectedKeys().hasOnly(['orderStatus']);
      allow delete: if false; // Orders locked for accounting & commission audit
    }

    // 4. Marketing Posts (AI জেনারেটেড পোস্ট)
    match /marketing_posts/{postId} {
      allow read: if true;
      allow create, update: if true;
      allow delete: if isAuthenticated();
    }

    // 5. Reviews Collection (গ্রাহক রিভিউ ও রেটিং)
    match /reviews/{reviewId} {
      allow read: if true;
      allow create: if true;
      allow update, delete: if isAuthenticated();
    }
  }
}`;

  const flutterServiceCode = `// lib/services/amar_dokan_firestore.dart
import 'package:cloud_firestore/cloud_firestore.dart';

class AmarDokanFirestoreService {
  final FirebaseFirestore _db = FirebaseFirestore.instance;

  // 1. Get shops filtered by Union in Badalgachhi, Naogaon
  Stream<List<Map<String, dynamic>>> getShopsByUnion(String? union) {
    Query query = _db.collection('shops').where('status', isEqualTo: 'Active');
    if (union != null && union.isNotEmpty) {
      query = query.where('union', isEqualTo: union);
    }
    return query.snapshots().map((snapshot) =>
        snapshot.docs.map((doc) => {'shopId': doc.id, ...doc.data() as Map<String, dynamic>}).toList());
  }

  // 2. Get products for a shop or union
  Stream<List<Map<String, dynamic>>> getProducts({String? shopId, String? category}) {
    Query query = _db.collection('products');
    if (shopId != null) query = query.where('shopId', isEqualTo: shopId);
    if (category != null && category != 'সব ক্যাটাগরি') query = query.where('category', isEqualTo: category);
    
    return query.snapshots().map((snapshot) =>
        snapshot.docs.map((doc) => {'productId': doc.id, ...doc.data() as Map<String, dynamic>}).toList());
  }

  // 3. Place new Order with 5% Platform Commission to RSTS-BD (01755383039)
  Future<String> placeOrder({
    required String shopId,
    required String customerName,
    required String customerPhone,
    required String deliveryAddress,
    required String union,
    required List<Map<String, dynamic>> items,
    required double totalAmount,
    required String paymentMethod,
  }) async {
    final commission = totalAmount * 0.05; // 5% platform commission
    final orderRef = _db.collection('orders').doc();
    
    await orderRef.set({
      'orderId': orderRef.id,
      'shopId': shopId,
      'customerName': customerName,
      'customerPhone': customerPhone,
      'deliveryAddress': deliveryAddress,
      'union': union,
      'upazila': 'Badalgachhi',
      'district': 'Naogaon',
      'items': items,
      'totalAmount': totalAmount,
      'platformCommission': commission,
      'orderStatus': 'Pending',
      'paymentMethod': paymentMethod,
      'timestamp': FieldValue.serverTimestamp(),
    });

    return orderRef.id;
  }

  // 4. Update Order Status (Shop Owner Action)
  Future<void> updateOrderStatus(String orderId, String newStatus) async {
    await _db.collection('orders').doc(orderId).update({
      'orderStatus': newStatus,
      'updatedAt': FieldValue.serverTimestamp(),
    });
  }

  // 5. Save AI Marketing Post
  Future<void> saveMarketingPost({
    required String shopId,
    required String generatedText,
    String? imageUrl,
  }) async {
    await _db.collection('marketing_posts').add({
      'shopId': shopId,
      'generatedText': generatedText,
      'imageUrl': imageUrl,
      'createdAt': FieldValue.serverTimestamp(),
    });
  }
}`;

  return (
    <div className="max-w-6xl mx-auto py-6 px-4 sm:px-6 space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-3xl border border-stone-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="p-3 rounded-2xl bg-amber-500 text-white shadow-md">
            <Database className="w-6 h-6" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl font-extrabold text-stone-900">
                ফায়ারবেস ও ফ্লাটার আর্কিটেকচার হাব
              </h1>
              <span className="text-xs bg-amber-100 text-amber-900 font-bold px-2 py-0.5 rounded-full">
                Firebase Firestore & Rules
              </span>
            </div>
            <p className="text-xs text-stone-500 mt-0.5">
              "আমার দোকান" প্ল্যাটফর্মের ডাটাবেজ মডেল, সিকিউরিটি রুলস ও Flutter SDK ইন্টিগ্রেশন
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2 text-xs">
          <span className="text-stone-500">ডেভেলপার:</span>
          <strong className="text-emerald-800">RSTS-BD | হটলাইন: 01755383039</strong>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex flex-wrap items-center gap-2 border-b border-stone-200 pb-2 text-xs font-bold">
        <button
          onClick={() => setActiveTab('schema')}
          className={`px-4 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
            activeTab === 'schema'
              ? 'bg-amber-600 text-white shadow-xs'
              : 'bg-white text-stone-700 hover:bg-stone-100 border border-stone-200'
          }`}
        >
          <Layers className="w-4 h-4" />
          <span>Firestore Schema (ডাটাবেজ স্কিমা)</span>
        </button>

        <button
          onClick={() => setActiveTab('rules')}
          className={`px-4 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
            activeTab === 'rules'
              ? 'bg-amber-600 text-white shadow-xs'
              : 'bg-white text-stone-700 hover:bg-stone-100 border border-stone-200'
          }`}
        >
          <ShieldCheck className="w-4 h-4" />
          <span>firestore.rules (সিকিউরিটি রুলস)</span>
        </button>

        <button
          onClick={() => setActiveTab('flutter')}
          className={`px-4 py-2 rounded-xl transition-all flex items-center gap-1.5 ${
            activeTab === 'flutter'
              ? 'bg-amber-600 text-white shadow-xs'
              : 'bg-white text-stone-700 hover:bg-stone-100 border border-stone-200'
          }`}
        >
          <Smartphone className="w-4 h-4" />
          <span>Flutter / Dart SDK সার্ভিস</span>
        </button>
      </div>

      {/* TAB 1: SCHEMA SPECIFICATION */}
      {activeTab === 'schema' && (
        <div className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* 1. shops */}
            <div className="bg-white p-5 rounded-2xl border border-stone-200 shadow-xs space-y-3">
              <div className="flex items-center justify-between border-b border-stone-100 pb-2">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-emerald-500" />
                  <h3 className="font-extrabold text-stone-900 text-sm">
                    1. shops Collection (ডোকানসমূহ)
                  </h3>
                </div>
                <span className="text-[11px] font-mono text-stone-400">/shops/{'{shopId}'}</span>
              </div>
              <ul className="space-y-1.5 text-xs text-stone-600 font-mono bg-stone-50 p-3 rounded-xl">
                <li>• <strong>shopId</strong>: String (Primary Key)</li>
                <li>• <strong>shopName</strong>: String ("পাহাড়পুর মাটির মায়া")</li>
                <li>• <strong>ownerName</strong>: String ("মো: আব্দুল করিম")</li>
                <li>• <strong>phone</strong>: String ("01755383039")</li>
                <li>• <strong>union</strong>: String ("Paharpur" / "Badalgachhi Sadar")</li>
                <li>• <strong>upazila</strong>: String ("Badalgachhi")</li>
                <li>• <strong>district</strong>: String ("Naogaon")</li>
                <li>• <strong>category</strong>: String ("হস্তশিল্প" / "মুদি")</li>
                <li>• <strong>status</strong>: String ("Active" / "Pending")</li>
                <li>• <strong>createdAt</strong>: Timestamp</li>
              </ul>
            </div>

            {/* 2. products */}
            <div className="bg-white p-5 rounded-2xl border border-stone-200 shadow-xs space-y-3">
              <div className="flex items-center justify-between border-b border-stone-100 pb-2">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-teal-500" />
                  <h3 className="font-extrabold text-stone-900 text-sm">
                    2. products Collection (পণ্যের তালিকা)
                  </h3>
                </div>
                <span className="text-[11px] font-mono text-stone-400">/products/{'{productId}'}</span>
              </div>
              <ul className="space-y-1.5 text-xs text-stone-600 font-mono bg-stone-50 p-3 rounded-xl">
                <li>• <strong>productId</strong>: String (Primary Key)</li>
                <li>• <strong>shopId</strong>: String (Foreign Key -&gt; shops)</li>
                <li>• <strong>productName</strong>: String</li>
                <li>• <strong>price</strong>: Number (e.g. 500)</li>
                <li>• <strong>discountPrice</strong>: Number (e.g. 450)</li>
                <li>• <strong>stock</strong>: Number (e.g. 20)</li>
                <li>• <strong>description</strong>: String</li>
                <li>• <strong>imageUrl</strong>: String</li>
                <li>• <strong>createdAt</strong>: Timestamp</li>
              </ul>
            </div>

            {/* 3. orders */}
            <div className="bg-white p-5 rounded-2xl border border-stone-200 shadow-xs space-y-3">
              <div className="flex items-center justify-between border-b border-stone-100 pb-2">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-amber-500" />
                  <h3 className="font-extrabold text-stone-900 text-sm">
                    3. orders Collection (অর্ডার ম্যানেজমেন্ট)
                  </h3>
                </div>
                <span className="text-[11px] font-mono text-stone-400">/orders/{'{orderId}'}</span>
              </div>
              <ul className="space-y-1.5 text-xs text-stone-600 font-mono bg-stone-50 p-3 rounded-xl">
                <li>• <strong>orderId</strong>: String (Primary Key)</li>
                <li>• <strong>shopId</strong>: String</li>
                <li>• <strong>customerName</strong>: String</li>
                <li>• <strong>customerPhone</strong>: String ("01755383039")</li>
                <li>• <strong>deliveryAddress</strong>: String (Village/Union)</li>
                <li>• <strong>items</strong>: Array &lt;productId, quantity, price&gt;</li>
                <li>• <strong>totalAmount</strong>: Number</li>
                <li>• <strong>platformCommission</strong>: Number (5% RSTS-BD)</li>
                <li>• <strong>orderStatus</strong>: "Pending" | "Confirmed" | "Packed" | "Shipped" | "Delivered"</li>
                <li>• <strong>timestamp</strong>: Timestamp</li>
              </ul>
            </div>

            {/* 4. marketing_posts */}
            <div className="bg-white p-5 rounded-2xl border border-stone-200 shadow-xs space-y-3">
              <div className="flex items-center justify-between border-b border-stone-100 pb-2">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-purple-500" />
                  <h3 className="font-extrabold text-stone-900 text-sm">
                    4. marketing_posts (সোশ্যাল মিডিয়া পোস্ট)
                  </h3>
                </div>
                <span className="text-[11px] font-mono text-stone-400">/marketing_posts/{'{postId}'}</span>
              </div>
              <ul className="space-y-1.5 text-xs text-stone-600 font-mono bg-stone-50 p-3 rounded-xl">
                <li>• <strong>postId</strong>: String (Primary Key)</li>
                <li>• <strong>shopId</strong>: String</li>
                <li>• <strong>generatedText</strong>: String (বাংলা ফেসবুক পোস্ট)</li>
                <li>• <strong>imageUrl</strong>: String (Optional)</li>
                <li>• <strong>createdAt</strong>: Timestamp</li>
              </ul>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: FIRESTORE RULES */}
      {activeTab === 'rules' && (
        <div className="bg-stone-900 text-stone-100 p-5 rounded-3xl space-y-3 shadow-lg">
          <div className="flex items-center justify-between">
            <span className="text-xs font-mono text-stone-400 flex items-center gap-2">
              <Code className="w-4 h-4 text-emerald-400" />
              <span>firestore.rules</span>
            </span>
            <button
              onClick={() => handleCopy(firestoreRulesCode, 'rules')}
              className="px-3 py-1.5 bg-stone-800 hover:bg-stone-700 text-white text-xs font-bold rounded-lg flex items-center gap-1 transition-colors"
            >
              {copiedKey === 'rules' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copiedKey === 'rules' ? 'কপি হয়েছে!' : 'রুলস কপি করুন'}</span>
            </button>
          </div>
          <pre className="p-4 bg-stone-950 rounded-2xl overflow-x-auto text-xs font-mono text-emerald-300 leading-relaxed max-h-[500px]">
            {firestoreRulesCode}
          </pre>
        </div>
      )}

      {/* TAB 3: FLUTTER CODE */}
      {activeTab === 'flutter' && (
        <div className="bg-stone-900 text-stone-100 p-5 rounded-3xl space-y-3 shadow-lg">
          <div className="flex items-center justify-between">
            <span className="text-xs font-mono text-stone-400 flex items-center gap-2">
              <Smartphone className="w-4 h-4 text-cyan-400" />
              <span>lib/services/amar_dokan_firestore.dart</span>
            </span>
            <button
              onClick={() => handleCopy(flutterServiceCode, 'flutter')}
              className="px-3 py-1.5 bg-stone-800 hover:bg-stone-700 text-white text-xs font-bold rounded-lg flex items-center gap-1 transition-colors"
            >
              {copiedKey === 'flutter' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copiedKey === 'flutter' ? 'কপি হয়েছে!' : 'ফ্লাটার কোড কপি করুন'}</span>
            </button>
          </div>
          <pre className="p-4 bg-stone-950 rounded-2xl overflow-x-auto text-xs font-mono text-cyan-300 leading-relaxed max-h-[500px]">
            {flutterServiceCode}
          </pre>
        </div>
      )}
    </div>
  );
};
