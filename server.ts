import express, { Request, Response } from 'express';
import dotenv from 'dotenv';
import path from 'path';
import { fileURLToPath } from 'url';
import { GoogleGenAI } from '@google/genai';

dotenv.config();

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());

// Initialize Gemini API client on the server side
const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY || '',
  httpOptions: {
    headers: {
      'User-Agent': 'aistudio-build',
    },
  },
});

// Marketing Post Generator Endpoint using Gemini AI
app.post('/api/marketing/generate', async (req: Request, res: Response) => {
  try {
    const {
      productName,
      price,
      discountPrice,
      shopName,
      unionName,
      upazila = 'বদলগাছী',
      district = 'নওগাঁ',
      category,
      description,
      tone = 'আকর্ষণীয় ও চটকদার',
      customInstructions = '',
      phone = '01755383039',
    } = req.body;

    if (!productName || !shopName) {
      return res.status(400).json({ error: 'পণ্য এবং দোকানের নাম প্রদান আবশ্যক।' });
    }

    const priceText = discountPrice && discountPrice < price 
      ? `পূর্বমূল্য: ৳${price}, অফার মূল্য: মাত্র ৳${discountPrice}!` 
      : `মূল্য: মাত্র ৳${price}`;

    const prompt = `আপনি বদলগাছী, নওগাঁর স্থানীয় অনলাইন মার্কেটপ্লেস "আমার দোকান" (Amar Dokan) এর একজন সেরা সোশ্যাল মিডিয়া ডিজিটাল মার্কেটার।
দোকানের পণ্যের জন্য একটি অত্যন্ত আকর্ষণীয়, প্রাণবন্ত এবং বিক্রি বাড়ানোর উপযোগী ফেসবুক পোস্ট লিখুন।

পণ্যের বিবরণ:
- পণ্যের নাম: ${productName}
- দোকান: ${shopName}
- এলাকা: ${unionName ? unionName + ', ' : ''}${upazila}, ${district}
- ক্যাটাগরি: ${category || 'সাধারণ পণ্য'}
- দামের বিবরণ: ${priceText}
- বিস্তারিত: ${description || 'উচ্চমানের স্থানীয় পণ্য'}
- পছন্দের টোন/স্টাইল: ${tone}
- অতিরিক্ত নির্দেশনা: ${customInstructions || 'স্থানীয় ভাষায় আন্তরিক আবেদন'}
- যোগাযোগের নম্বর/হোয়াটসঅ্যাপ: ${phone} (প্ল্যাটফর্ম হেল্পলাইন: 01755383039)

পোস্টের ফরম্যাট শর্তাবলী:
1. প্রথম লাইনে একটি চোখ ধাঁধানো শিরোনাম বা হুক লাইন (উপযুক্ত ইমোজি সহ যেমন 🔥, 📢, ✨, 🛒)।
2. পণ্যের মান, স্থানীয় উৎপাদন এবং বদলগাছীর মানুষদের জন্য কেন এটি সেরা তা তুলে ধরুন।
3. স্পষ্ট দাম ও ডিসকাউন্ট অফার হাইলাইট করুন।
4. বদলগাছী ও আশেপাশের ইউনিয়নের জন্য হোম ডেলিভারির সুবিধা উল্লেখ করুন।
5. সুন্দর কল-টু-অ্যাকশন (CTA): "অর্ডার করতে এখনই ইনবক্স করুন অথবা কল/হোয়াটসঅ্যাপ করুন: ${phone} / 01755383039"।
6. প্রাসঙ্গিক হ্যাশট্যাগ (#বদলগাছী #নওগাঁ #আমারদোকান #AmarDokan #Badalgachhi #OnlineShopping #RSTS_BD) যোগ করুন।
7. সম্পূর্ণ পোস্টটি শুদ্ধ ও সাবলীল বাংলা ভাষায় হতে হবে। কোনো অপ্রয়োজনীয় ভূমিকা বা প্রি-টেক্সট ছাড়া সরাসরি পোস্টের টেক্সট দিন।`;

    if (!process.env.GEMINI_API_KEY) {
      // Fallback response if API key is not yet set in environment
      const defaultPost = `📢 বদলগাছীবাসীর জন্য বিশেষ অফার! 🔥\n\n"${shopName}" নিয়ে এলো আসল ও টাটকা ${productName}! 🌿\n\nআমাদের বদলগাছী, নওগাঁর ঘরে ঘরে খাঁটি ও সেরা মানের পণ্য পৌঁছে দিতে আমরা প্রতিশ্রুতিবদ্ধ।\n\n🏷️ ${priceText}\n📦 ঘরে বসেই দ্রুত হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা!\n📍 এলাকা: ${unionName ? unionName + ', ' : ''}${upazila}, ${district}\n\n👉 স্টক সীমিত! আপনার অর্ডারটি নিশ্চিত করতে এখনই যোগাযোগ করুন:\n📞 কল/WhatsApp: ${phone}\n🌐 "আমার দোকান" - বদলগাছী, নওগাঁ (Developed by RSTS-BD: 01755383039)\n\n#বদলগাছী #নওগাঁ #আমারদোকান #AmarDokan #অনলাইন_শপিং #RSTS_BD`;
      return res.json({ generatedText: defaultPost, fallback: true });
    }

    const response = await ai.models.generateContent({
      model: 'gemini-3.8-flash',
      contents: prompt,
      config: {
        systemInstruction: 'You are an expert Bangladeshi e-commerce social media copywriter specializing in hyper-local Bengali Facebook marketing copy for Amar Dokan platform in Badalgachhi, Naogaon.',
        temperature: 0.8,
      },
    });

    const generatedText = response.text || '';
    return res.json({ generatedText });
  } catch (error: any) {
    console.error('Gemini Marketing Gen Error:', error);
    // Graceful fallback with good Bengali copy
    const {
      productName,
      shopName,
      price,
      discountPrice,
      unionName,
      upazila = 'বদলগাছী',
      phone = '01755383039',
    } = req.body;
    const priceInfo = discountPrice && discountPrice < price 
      ? `অফার মূল্য: মাত্র ৳${discountPrice} (পূর্বে ৳${price})` 
      : `মূল্য: মাত্র ৳${price}`;

    const fallbackText = `🔥 বদলগাছীবাসীর জন্য বিশেষ অফার! 📢\n\n"${shopName}" এ পাওয়া যাচ্ছে প্রিমিয়াম ও খাঁটি ${productName}!\n\n🏷️ ${priceInfo}\n📦 আপনার ইউনিয়ন (${unionName || 'বদলগাছী'}) এ দ্রুততম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা!\n📍 বদলগাছী, নওগাঁ\n\n👉 স্টক সীমিত! অর্ডার করতে কল বা WhatsApp করুন:\n📞 WhatsApp/কল: ${phone} (হেল্পলাইন: 01755383039)\n🌐 "আমার দোকান" - Developed by RSTS-BD\n\n#বদলগাছী #নওগাঁ #আমারদোকান #AmarDokan #RSTS_BD`;
    return res.json({ generatedText: fallbackText, error: error.message });
  }
});

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({
    status: 'online',
    platform: 'আমার দোকান (Amar Dokan)',
    location: 'বদলগাছী, নওগাঁ',
    hotline: '01755383039',
    developer: 'RSTS-BD',
  });
});

// Start Vite in dev mode or static files in production
async function startServer() {
  if (process.env.NODE_ENV === 'production') {
    app.use(express.static(path.resolve(__dirname, 'dist')));
    app.get('*', (req, res) => {
      res.sendFile(path.resolve(__dirname, 'dist/index.html'));
    });
  } else {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  }

  app.listen(port, () => {
    console.log(`[আমার দোকান Server] Listening on http://localhost:${port}`);
  });
}

startServer();
