package com.salah.kidslearn.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * مدير TextToSpeech - يستخدم محرك Android TTS المدمج (مجاني)
 * يدعم العربية والإنجليزية تلقائياً
 *
 * ملاحظة: محرك TTS متوفر افتراضياً في 99% من أجهزة Android الحديثة
 * عبر تطبيق "Speech Services" من Google.
 *
 * تم تصميم الكود ليكون دفاعياً: لا يفشل التطبيق إذا كان TTS غير متوفر.
 */
class TtsManager private constructor(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private var pendingText: String? = null
    private var pendingLang: String = "ar"

    init {
        try {
            tts = TextToSpeech(context) { status ->
                try {
                    if (status == TextToSpeech.SUCCESS) {
                        isReady = true
                        Log.d("TtsManager", "TTS engine ready")
                        pendingText?.let { speakInternal(it, pendingLang); pendingText = null }
                    } else {
                        Log.e("TtsManager", "TTS init failed: $status")
                    }
                } catch (e: Exception) {
                    Log.e("TtsManager", "TTS init callback error", e)
                }
            }
        } catch (e: Exception) {
            Log.e("TtsManager", "TextToSpeech constructor failed", e)
        }
    }

    /**
     * نطق نص بصوت عربي أو إنجليزي
     * @param text النص المراد نطقه
     * @param lang "ar" أو "en"
     */
    fun speak(text: String, lang: String = "ar") {
        if (!isReady) {
            pendingText = text
            pendingLang = lang
            return
        }
        speakInternal(text, lang)
    }

    private fun speakInternal(text: String, lang: String) {
        try {
            val locale = when (lang) {
                "en" -> Locale.US
                "ar" -> Locale("ar")
                else -> Locale.getDefault()
            }
            val result = tts?.setLanguage(locale)
            when (result) {
                TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                    Log.w("TtsManager", "Language $lang not supported, falling back to default")
                    tts?.setLanguage(Locale.getDefault())
                }
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TtsManager", "speak failed", e)
        }
    }

    /**
     * نطق حرف (يدعم النطق البطيء للأطفال)
     */
    fun speakLetter(letter: String, lang: String = "ar") {
        // إضافة Pitches عالية للأطفال لجعل الصوت أكثر مرحاً
        try {
            tts?.setPitch(1.2f)
            tts?.setSpeechRate(0.85f)
            speak(letter, lang)
        } catch (e: Exception) {
            Log.e("TtsManager", "Pitch/rate set failed", e)
            speak(letter, lang)
        } finally {
            try {
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
            } catch (_: Exception) {
                // تجاهل
            }
        }
    }

    /**
     * نطق نص بنبرة مخصصة (لمحاكاة أصوات الحيوانات)
     * مثلاً: نطق "مياو" بنبرة عالية لمحاكاة صوت القطة
     *
     * @param text النص المراد نطقه
     * @param pitch النبرة (0.5 منخفض، 1.0 عادي، 2.0 مرتفع)
     * @param lang اللغة "ar" أو "en"
     */
    fun speakWithPitch(text: String, pitch: Float, lang: String = "ar") {
        try {
            tts?.setPitch(pitch.coerceIn(0.1f, 2.0f))
            tts?.setSpeechRate(0.7f)  // بطيء ليكون أوضح
            speak(text, lang)
        } catch (e: Exception) {
            Log.e("TtsManager", "speakWithPitch failed", e)
            speak(text, lang)
        } finally {
            try {
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
            } catch (_: Exception) {}
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TtsManager", "stop failed", e)
        }
    }

    /**
     * إيقاف كل النطق فوراً - يُستدعى قبل بدء صوت جديد لمنع التداخل
     */
    fun stopAll() {
        try {
            tts?.stop()
            // إعادة ضبط النبرة والسرعة للقيم الافتراضية
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(1.0f)
        } catch (e: Exception) {
            Log.e("TtsManager", "stopAll failed", e)
        }
    }

    fun destroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("TtsManager", "destroy failed", e)
        }
        tts = null
        isReady = false
    }

    companion object {
        @Volatile private var instance: TtsManager? = null
        fun getInstance(context: Context): TtsManager {
            return instance ?: synchronized(this) {
                instance ?: TtsManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
