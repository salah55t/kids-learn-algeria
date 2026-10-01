package com.salah.kidslearn.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * نموذج بيانات الحرف (عربي أو إنجليزي)
 * @param letter الحرف نفسه
 * @param name نطق الحرف بالعربية (مثال: "أ" → "ألف")
 * @param pronunciation نطق الحرف بالإنجليزية للـ TTS
 * @param exampleWord كلمة تبدأ بالحرف (مثال: "أرنب")
 * @param exampleTranslation ترجمة الكلمة (للإنجليزية: "Rabbit")
 * @param exampleDrawable معرف رسم الكلمة
 * @param language "ar" أو "en"
 */
@Parcelize
data class Letter(
    val letter: String,
    val name: String,
    val pronunciation: String,
    val exampleWord: String,
    val exampleTranslation: String = "",
    val exampleDrawable: String = "",
    val language: String = "ar"
) : Parcelable

/**
 * نموذج بيانات الرقم
 * @param value قيمة الرقم
 * @param name اسم الرقم بالعربية
 * @param englishName اسم الرقم بالإنجليزية
 * @param drawable معرف رسم العدد المرئي (مثال: 5 تفاحات)
 */
@Parcelize
data class NumberItem(
    val value: Int,
    val name: String,
    val englishName: String,
    val drawable: String
) : Parcelable

/**
 * نموذج بيانات اللون
 * @param name اسم اللون بالعربية
 * @param englishName اسم اللون بالإنجليزية
 * @param colorHex كود اللون (مثال: "#FF0000" للأحمر)
 * @param exampleObject اسم كائن يرمز للون (مثال: "تفاحة")
 * @param exampleDrawable معرف رسم الكائن
 */
@Parcelize
data class ColorItem(
    val name: String,
    val englishName: String,
    val colorHex: String,
    val exampleObject: String,
    val exampleDrawable: String
) : Parcelable

/**
 * نموذج بيانات الشكل الهندسي
 * @param name اسم الشكل بالعربية
 * @param englishName اسم الشكل بالإنجليزية
 * @param drawable معرف رسم الشكل
 */
@Parcelize
data class ShapeItem(
    val name: String,
    val englishName: String,
    val drawable: String
) : Parcelable

/**
 * نموذج بيانات الحيوان
 * @param name اسم الحيوان بالعربية
 * @param englishName اسم الحيوان بالإنجليزية
 * @param sound صوت الحيوان (نص للـ TTS)
 * @param drawable معرف رسم الحيوان
 */
@Parcelize
data class AnimalItem(
    val name: String,
    val englishName: String,
    val sound: String,
    val drawable: String
) : Parcelable

/**
 * نموذج الشارة (Badge)
 */
@Parcelize
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val drawable: String,
    val requiredXp: Int
) : Parcelable
