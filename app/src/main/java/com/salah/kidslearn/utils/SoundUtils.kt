package com.salah.kidslearn.utils

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.salah.kidslearn.R

/**
 * مدير المؤثرات الصوتية واللمسية
 * يستخدم SoundPool للمؤثرات القصيرة (تصفيق، خطأ، نجاح)
 * و Vibrator للتغذية الراجعة اللمسية عند الكتابة
 *
 * المؤثرات الصوتية مولّدة برمجياً لتجنب تحميل ملفات خارجية
 */
class SoundUtils private constructor(private val context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_GAME)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIds = mutableMapOf<String, Int>()
    private var vibrator: Vibrator? = null

    init {
        // تهيئة الاهتزاز
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        // تحميل الأصوات - نستخدم أصوات مدمجة (raw) مفتوحة المصدر
        loadSounds()
    }

    private fun loadSounds() {
        try {
            // محاولة تحميل أصوات من res/raw (سنضيفها لاحقاً)
            // حالياً نعتمد على TTS للمؤثرات
            soundIds["success"] = try {
                soundPool.load(context, R.raw.success, 1)
            } catch (e: Exception) { 0 }
            soundIds["error"] = try {
                soundPool.load(context, R.raw.error, 1)
            } catch (e: Exception) { 0 }
            soundIds["click"] = try {
                soundPool.load(context, R.raw.click, 1)
            } catch (e: Exception) { 0 }
        } catch (e: Exception) {
            // تجاهل - الصوت اختياري
        }
    }

    fun playSuccess() {
        soundIds["success"]?.takeIf { it != 0 }?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
        }
        vibrate(150)
    }

    fun playError() {
        soundIds["error"]?.takeIf { it != 0 }?.let {
            soundPool.play(it, 0.7f, 0.7f, 1, 0, 1.0f)
        }
        vibrate(300)
    }

    fun playClick() {
        soundIds["click"]?.takeIf { it != 0 }?.let {
            soundPool.play(it, 0.5f, 0.5f, 1, 0, 1.0f)
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
            // الاهتزاز اختياري
        }
    }

    fun release() {
        soundPool.release()
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
