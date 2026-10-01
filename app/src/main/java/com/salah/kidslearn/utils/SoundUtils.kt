package com.salah.kidslearn.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.salah.kidslearn.R

/**
 * مدير المؤثرات الصوتية واللمسية
 * يستخدم SoundPool للمؤثرات القصيرة (تصفيق، خطأ، نجاح)
 * و Vibrator للتغذية الراجعة اللمسية عند الكتابة
 *
 * المؤثرات الصوتية مولّدة برمجياً لتجبن تحميل ملفات خارجية
 *
 * التصميم دفاعي: لا يفشل التطبيق إذا كان الصوت أو الاهتزاز غير متوفر.
 */
class SoundUtils private constructor(private val context: Context) {

    private val TAG = "SoundUtils"

    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<String, Int>()
    private var vibrator: Vibrator? = null

    init {
        // تهيئة SoundPool بشكل دفاعي
        try {
            soundPool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "SoundPool init failed", e)
            // fallback to deprecated constructor
            try {
                @Suppress("DEPRECATION")
                soundPool = SoundPool(4, android.media.AudioManager.STREAM_MUSIC, 0)
            } catch (e2: Exception) {
                Log.e(TAG, "SoundPool fallback init also failed", e2)
            }
        }

        // تهيئة الاهتزاز بشكل دفاعي
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.e(TAG, "Vibrator init failed", e)
        }

        loadSounds()
    }

    private fun loadSounds() {
        try {
            soundIds["success"] = try {
                soundPool?.load(context, R.raw.success, 1) ?: 0
            } catch (e: Exception) { 0 }
            soundIds["error"] = try {
                soundPool?.load(context, R.raw.error, 1) ?: 0
            } catch (e: Exception) { 0 }
            soundIds["click"] = try {
                soundPool?.load(context, R.raw.click, 1) ?: 0
            } catch (e: Exception) { 0 }
        } catch (e: Exception) {
            Log.e(TAG, "loadSounds failed", e)
        }
    }

    fun playSuccess() {
        try {
            soundIds["success"]?.takeIf { it != 0 }?.let {
                soundPool?.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
            }
            vibrate(150)
        } catch (e: Exception) {
            Log.e(TAG, "playSuccess failed", e)
        }
    }

    fun playError() {
        try {
            soundIds["error"]?.takeIf { it != 0 }?.let {
                soundPool?.play(it, 0.7f, 0.7f, 1, 0, 1.0f)
            }
            vibrate(300)
        } catch (e: Exception) {
            Log.e(TAG, "playError failed", e)
        }
    }

    fun playClick() {
        try {
            soundIds["click"]?.takeIf { it != 0 }?.let {
                soundPool?.play(it, 0.5f, 0.5f, 1, 0, 1.0f)
            }
        } catch (e: Exception) {
            Log.e(TAG, "playClick failed", e)
        }
    }

    fun vibrate(durationMs: Long = 50) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            Log.e(TAG, "vibrate failed", e)
        }
    }

    fun release() {
        try {
            soundPool?.release()
        } catch (e: Exception) {
            Log.e(TAG, "release failed", e)
        }
    }

    /**
     * يحمل ملف صوت من res/raw ويعيد soundId لاستعماله لاحقاً
     * @param rawName اسم الملف بدون امتداد (مثال: "animal_cat")
     * @return soundId أو 0 عند الفشل
     */
    fun loadRaw(rawName: String): Int {
        // نتحقق من التحميل المسبق
        soundIds["raw_$rawName"]?.let { return it }

        return try {
            val resId = context.resources.getIdentifier(rawName, "raw", context.packageName)
            if (resId == 0) {
                Log.w(TAG, "Raw resource not found: $rawName")
                return 0
            }
            val sid = soundPool?.load(context, resId, 1) ?: 0
            soundIds["raw_$rawName"] = sid
            Log.d(TAG, "Loaded raw $rawName -> soundId=$sid")
            sid
        } catch (e: Exception) {
            Log.e(TAG, "loadRaw failed for $rawName", e)
            0
        }
    }

    /**
     * يشغّل صوتاً من res/raw حسب اسمه
     * @param rawName اسم الملف بدون امتداد
     * @return true إذا تم التشغيل، false إذا فشل
     */
    fun playRaw(rawName: String): Boolean {
        return try {
            var sid = soundIds["raw_$rawName"]
            if (sid == null || sid == 0) {
                sid = loadRaw(rawName)
            }
            if (sid == 0) return false
            soundPool?.play(sid, 1.0f, 1.0f, 1, 0, 1.0f)
            true
        } catch (e: Exception) {
            Log.e(TAG, "playRaw failed for $rawName", e)
            false
        }
    }

    companion object {
        @Volatile private var instance: SoundUtils? = null
        fun getInstance(context: Context): SoundUtils {
            return instance ?: synchronized(this) {
                instance ?: SoundUtils(context.applicationContext).also { instance = it }
            }
        }
    }
}
