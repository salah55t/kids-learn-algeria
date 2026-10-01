package com.salah.kidslearn.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.salah.kidslearn.R
import com.salah.kidslearn.ui.animals.AnimalsActivity
import com.salah.kidslearn.ui.colors.ColorsActivity
import com.salah.kidslearn.ui.letters.ArabicLettersActivity
import com.salah.kidslearn.ui.letters.EnglishLettersActivity
import com.salah.kidslearn.ui.numbers.NumbersActivity
import com.salah.kidslearn.ui.shapes.ShapesActivity
import com.salah.kidslearn.utils.ProgressManager
import com.salah.kidslearn.utils.SoundUtils
import com.salah.kidslearn.utils.TtsManager

/**
 * الشاشة الرئيسية - تعرض البطاقات الست للأقسام + شريط الإحصائيات العلوي
 *
 * تخطيط الشاشة:
 * - شريط علوي: نقاط XP | نجوم | سلسلة Streak | زر الإنجازات
 * - عنوان مرحب
 * - شبكة 2×3 من البطاقات:
 *   [الحروف العربية] [الحروف الإنجليزية]
 *   [الأرقام]         [الألوان]
 *   [الأشكال]         [الحيوانات]
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var tvXp: TextView
    private lateinit var tvStars: TextView
    private lateinit var tvStreak: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set content view", e)
            finish()
            return
        }

        // تهيئة المحركات بشكل دفاعي (لا توقف التطبيق عند الفشل)
        try {
            TtsManager.getInstance(this)
        } catch (e: Exception) {
            Log.e(TAG, "TtsManager init failed", e)
        }

        try {
            SoundUtils.getInstance(this)
        } catch (e: Exception) {
            Log.e(TAG, "SoundUtils init failed", e)
        }

        // تحديث السلسلة اليومية
        try {
            ProgressManager.getInstance(this).updateStreak()
        } catch (e: Exception) {
            Log.e(TAG, "ProgressManager init failed", e)
        }

        bindViews()
        setupClickListeners()
        updateStats()
    }

    private fun bindViews() {
        tvXp = findViewById(R.id.tv_xp)
        tvStars = findViewById(R.id.tv_stars)
        tvStreak = findViewById(R.id.tv_streak)
    }

    private fun setupClickListeners() {
        findViewById<CardView>(R.id.card_arabic_letters)?.setOnClickListener {
            playClickSafe()
            startActivity(Intent(this, ArabicLettersActivity::class.java))
        }
        findViewById<CardView>(R.id.card_english_letters)?.setOnClickListener {
            playClickSafe()
            startActivity(Intent(this, EnglishLettersActivity::class.java))
        }
        findViewById<CardView>(R.id.card_numbers)?.setOnClickListener {
            playClickSafe()
            startActivity(Intent(this, NumbersActivity::class.java))
        }
        findViewById<CardView>(R.id.card_colors)?.setOnClickListener {
            playClickSafe()
            startActivity(Intent(this, ColorsActivity::class.java))
        }
        findViewById<CardView>(R.id.card_shapes)?.setOnClickListener {
            playClickSafe()
            startActivity(Intent(this, ShapesActivity::class.java))
        }
        findViewById<CardView>(R.id.card_animals)?.setOnClickListener {
            playClickSafe()
            startActivity(Intent(this, AnimalsActivity::class.java))
        }
        findViewById<View>(R.id.btn_achievements)?.setOnClickListener {
            playClickSafe()
            startActivity(Intent(this, AchievementsActivity::class.java))
        }
    }

    private fun playClickSafe() {
        try {
            SoundUtils.getInstance(this).playClick()
        } catch (e: Exception) {
            Log.e(TAG, "playClick failed", e)
        }
    }

    private fun updateStats() {
        try {
            val pm = ProgressManager.getInstance(this)
            tvXp.text = pm.getXp().toString()
            tvStars.text = pm.getStars().toString()
            tvStreak.text = "${pm.getStreak()}"
        } catch (e: Exception) {
            Log.e(TAG, "updateStats failed", e)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStats() // تحديث بعد العودة من نشاط
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            TtsManager.getInstance(this).destroy()
        } catch (e: Exception) {
            Log.e(TAG, "TtsManager destroy failed", e)
        }
    }
}
