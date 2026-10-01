# 🎓 تطبيق تعلّم و العب - تطبيق تعليمي تفاعلي للأطفال (الجزائر)

<div dir="rtl">

تطبيق أندرويد تعليمي تفاعلي مصمم خصيصاً للأطفال في سن **5-6 سنوات** (المرحلة التحضيرية قبل السنة الأولى ابتدائي بالجزائر). يهدف التطبيق إلى تعليم الطفل الحروف العربية والإنجليزية، الأرقام، الألوان، الأشكال، والحيوانات بطريقة ممتعة وتفاعلية مستوحاة من أسلوب **Duolingo**.

## ✨ الميزات الرئيسية

### 📚 المحتوى التعليمي
- **28 حرف عربي** — مع كلمة مثال ورسم توضيحي لكل حرف (أرنب، بطة، تفاحة...)
- **26 حرف إنجليزي** — A-Z مع كلمات أمثلة (Apple, Ball, Cat, Dog...)
- **11 رقم** — من 0 إلى 10 بالعربية والإنجليزية
- **7 ألوان أساسية** — أحمر، أصفر، أزرق، أخضر، برتقالي، بنفسجي، وردي
- **6 أشكال هندسية** — دائرة، مربع، مثلث، مستطيل، نجمة، قلب
- **10 حيوانات** — مع أصواتها (قطة، كلب، أسد، بقرة، حصان...)

### 🎨 التفاعلية
- **لوح رسم Canvas مخصص** للطفل ليكتب الحروف بإصبعه مع تتبع سلاسة المسار
- **6 ألوان أقلام** قابلة للتبديل (وردي، أحمر، أزرق، أخضر، بنفسجي، برتقالي)
- **حرف دليلي خفيف** في الخلفية لتوجيه الطفل
- **زر مسح سريع** للبدء من جديد
- **نطق تلقائي** للحرف عند فتحه

### 🔊 نظام الصوتيات (مفتوح المصدر)
- **Android TextToSpeech (TTS)** — محرك الصوتيات المدمج في أندرويد (مجاني)
- يدعم العربية (`ar-SA`) والإنجليزية (`en-US`) تلقائياً
- صوت بـ **pitch عالٍ** (1.2x) و **سرعة بطيئة** (0.85x) مناسب للأطفال
- ملفات صوتية صامتة في `res/raw/` يمكن استبدالها بأصوات مفتوحة المصدر لاحقاً

### 🏆 نظام التحفيز (على غرار Duolingo)
- **نقاط XP** — 10 نقاط لكل حرف، 5 نقاط لكل عنصر آخر
- **نجوم** — نجمة لكل درس مكتمل
- **سلسلة يومية (Streak)** — تتبع الأيام المتتالية
- **8 شارات (Badges)** — أول حرف، خمسة حروف، أبجديّ، إلخ
- **6 ملصقات تشجيعية** — دب، أرنب، بومة، ثعلب، أسد، كأس
- **مؤشر "مكتمل ✓"** على الدروس التي أتمها الطفل

### 🌈 التصميم
- **واجهة عربية 100%** مع دعم كامل لـ RTL
- **ألوان زاهية مرحّة** — وردي/أصفر/سماوي على غرار Duolingo
- **خط Almarai** العربي مفتوح المصدر (OFL) من Google Fonts
- **بطاقات Material Design 3** بحواف دائرية وأنماط حدسية
- **أيقونات Vector Drawable** خفيفة (لم يتم استخدام PNG)
- **دعم موافق لإصدارات Android 7.0+** (API 24)

## 🛠️ المتطلبات التقنية

| المكون | الإصدار |
|--------|---------|
| لغة البرمجة | Kotlin 1.9.24 |
| Android Gradle Plugin | 8.5.2 |
| Gradle | 8.7 |
| Java | 17 |
| minSdk | 24 (Android 7.0) |
| targetSdk | 34 (Android 14) |
| compileSdk | 34 |

## 📁 هيكل المشروع

```
kids-learn-algeria/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/salah/kidslearn/
│       │   ├── data/
│       │   │   ├── Models.kt              # نماذج البيانات (Parcelable)
│       │   │   └── ContentProvider.kt    # محتوى الحروف والأرقام...
│       │   ├── ui/
│       │   │   ├── main/
│       │   │   │   ├── MainActivity.kt            # الشاشة الرئيسية
│       │   │   │   └── AchievementsActivity.kt   # شاشة الإنجازات
│       │   │   ├── letters/
│       │   │   │   ├── ArabicLettersActivity.kt
│       │   │   │   ├── EnglishLettersActivity.kt
│       │   │   │   └── LetterDetailActivity.kt   # نشاط كتابة الحرف
│       │   │   ├── numbers/NumbersActivity.kt
│       │   │   ├── colors/ColorsActivity.kt
│       │   │   ├── shapes/ShapesActivity.kt
│       │   │   └── animals/AnimalsActivity.kt
│       │   ├── utils/
│       │   │   ├── TtsManager.kt          # مدير TextToSpeech
│       │   │   ├── ProgressManager.kt      # نظام XP/نجوم/سلسلة/شارات
│       │   │   └── SoundUtils.kt          # مؤثرات صوتية ولمسية
│       │   └── widgets/
│       │       └── DrawingCanvasView.kt   # لوح الرسم التفاعلي
│       └── res/
│           ├── drawable/             # 87 رسماً Vector
│           ├── layout/               # 12 تخطيطاً XML
│           ├── values/               # الألوان/النصوص/الثيمات
│           ├── values-ar/            # نصوص عربية إضافية
│           ├── font/                 # خط Almarai
│           ├── raw/                  # ملفات صوتية
│           ├── mipmap-anydpi-v26/    # أيقونة التطبيق
│           └── xml/                  # قواعد النسخ الاحتياطي
├── .github/workflows/android.yml     # GitHub Actions workflow
├── build.gradle.kts                  # إعدادات المشروع
├── settings.gradle.kts
├── gradle.properties
├── gradle/wrapper/                   # Gradle Wrapper
├── .gitignore
├── LICENSE                           # MIT License
└── README.md
```

## 🚀 البناء والتشغيل

### المتطلبات
- Android Studio Hedgehog أو أحدث
- JDK 17
- Android SDK Platform 34

### خطوات البناء المحلي

1. **استنساخ المستودع:**
   ```bash
   git clone https://github.com/salah55t/kids-learn-algeria.git
   cd kids-learn-algeria
   ```

2. **البناء عبر Android Studio:**
   - افتح المشروع في Android Studio
   - انتظر تزامن Gradle
   - اضغط زر Run (▶) للتشغيل على محاكي/جهاز حقيقي

3. **البناء عبر سطر الأوامر:**
   ```bash
   chmod +x gradlew
   ./gradlew assembleDebug     # بناء Debug APK
   ./gradlew assembleRelease   # بناء Release APK
   ./gradlew testDebugUnitTest # تشغيل الاختبارات
   ./gradlew lintDebug         # فحص Lint
   ```

4. **موقع ملفات APK الناتجة:**
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release.apk`

### 🤖 البناء عبر GitHub Actions

المستودع مزود بـ GitHub Actions workflow يبني المشروع تلقائياً عند:
- **كل push** على فروع `main`, `master`, `develop`
- **كل pull request** على `main`/`master`
- **كل tag يبدأ بـ `v`** (مثلاً `v1.0.0`) — يُنشئ GitHub Release تلقائياً
- **تشغيل يدوي** عبر واجهة GitHub Actions

نتائج البناء تكون متاحة كـ **artifacts** قابلة للتنزيل (Debug APK + Release APK + تقارير Lint).

![Build Status](https://github.com/salah55t/kids-learn-algeria/actions/workflows/android.yml/badge.svg)

## 📦 المكتبات المستخدمة

| المكتبة | الإصدار | الغرض | الترخيص |
|---------|---------|-------|---------|
| AndroidX Core KTX | 1.13.1 | Kotlin extensions | Apache 2.0 |
| AndroidX AppCompat | 1.7.0 | توافق مع الإصدارات السابقة | Apache 2.0 |
| Material Components | 1.12.0 | Material Design 3 | Apache 2.0 |
| ConstraintLayout | 2.1.4 | تخطيطات معقدة | Apache 2.0 |
| RecyclerView | 1.3.2 | قوائم الحروف | Apache 2.0 |
| GridLayout | 1.0.0 | الشبكة الرئيسية | Apache 2.0 |
| Gson | 2.10.1 | معالجة JSON | Apache 2.0 |
| Almarai Font | 1.000 | خط عربي | SIL Open Font License |

كل المكتبات والخطوط مجانية ومفتوحة المصدر.

## 🎯 الجمهور المستهدف

- **العمر**: 5-6 سنوات
- **المرحلة**: التحضيري (ما قبل السنة الأولى ابتدائي)
- **المنطقة**: الجزائر (مع إمكانية التوسع لكل الدول العربية)
- **اللغة الأم**: العربية (مع تقديم الإنجليزية كلغة ثانية)

## 📚 منهجية التعلم

يتبع التطبيق مبادئ التعلم القائم على اللعب (Play-Based Learning):
1. **العرض البصري** — الحرف بشكل كبير وملون
2. **النطق الصوتي** — TTS ينطق الحرف تلقائياً عند الفتح
3. **الكتابة الحركية** — الطفل يرسم الحرف بإصبعه (تعلم حركي)
4. **الترابط** — كلمة مثال + رسم توضيحي (تفاحة، قطة...)
5. **التعزيز الإيجابي** — نقاط + نجوم + ملصقات + اهتزاز + صوت نجاح
6. **التكرار المتباعد** — يظهر مؤشر "مكتمل" للحروف المنجزة

## 🌟 خارطة الطريق (ميزات مستقبلية)

- [ ] إضافة قسم للكلمات الشائعة (10-20 كلمة)
- [ ] إضافة اختبارات قصيرة (Quiz) بعد كل 5 حروف
- [ ] إضافة وضع ليلي (Dark Theme)
- [ ] دعم اللغة الفرنسية كخيار ثالث (الجزائر فرنكوفونية)
- [ ] ربط صوتي مع ملفات صوت مسجلة لجودة أعلى (Wikimedia Commons)
- [ ] إضافة شخصية كرتونية مرشدة (Mascot)
- [ ] نظام متابعة الأهل (Parents Dashboard)
- [ ] دعم الوضع الأفقي (Landscape)
- [ ] إعداد صوتي قابل للتخصيص (إيقاف/تشغيل TTS)

## 🤝 المساهمة

المساهمات مرحب بها! للمساهمة:

1. افتح **Issue** لمناقبة الميزة المقترحة أو الخطأ
2. افتح (Fork) المستودع و أنشئ فرعاً (`feature/اسم-الميزة`)
3. قم بالتعديلات مع الحفاظ على نمط الكود
4. افتح (Pull Request) مع وصف واضح للتغييرات

يرجى تشغيل `./gradlew lintDebug` قبل إرسال PR.

## 📝 الترخيص

هذا المشروع مرخص تحت رخصة **MIT** — راجع ملف [LICENSE](LICENSE) للتفاصيل.

## 🙏 شكر وتقدير

- **Duolingo** — لإلهام أسلوب التعلم المرح
- **Google Fonts** — على خط Almarai العربي مفتوح المصدر
- **Android Open Source Project** — على مكتبات AndroidX و Material Components
- **الآباء والأمهات في الجزائر** — الذين يبحثون عن محتوى تعليمي عربي عالي الجودة

## 👨‍💻 المؤلف

**Hamza** — [@salah55t](https://github.com/salah55t)

---

<div align="center">

**صُنع بحب للأطفال الجزائريين** 🇩🇿

</div>

</div>
