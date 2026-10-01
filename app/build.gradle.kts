plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.salah.kidslearn"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.salah.kidslearn"
        minSdk = 24  // Android 7.0 - يدعم ~98% من الأجهزة
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // توقيع debug للاختبار - استبدله بمفتاحك الخاص للنشر على Play Store
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX الأساسية
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Material Design 3 - مكتبة Google الرسمية للتصميم
    implementation("com.google.android.material:material:1.12.0")

    // Gson لتحميل بيانات الحروف من JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // RecyclerView لقوائم الحروف
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // GridLayout للشبكة الرئيسية
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // اختبارات
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("junit:junit:4.13.2")
}
