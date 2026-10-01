package com.salah.kidslearn.ui.letters

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.salah.kidslearn.R
import com.salah.kidslearn.data.ContentProvider
import com.salah.kidslearn.data.Letter
import com.salah.kidslearn.utils.ProgressManager
import com.salah.kidslearn.utils.SoundUtils
import com.salah.kidslearn.utils.TtsManager
import com.salah.kidslearn.widgets.DrawingCanvasView

/**
 * نشاط تفاصيل الحرف - القلب التفاعلي للتطبيق
 *
 * يعرض:
 * 1. الحرف بشكل كبير في الأعلى
 * 2. زر "استمع" للنطق عبر TTS
 * 3. لوح رسم Canvas للطفل ليكتب الحرف بإصبعه
 * 4. أزرار تغيير لون القلم (6 ألوان)
 * 5. زر مسح
 * 6. كلمة مثال + رسم توضيحي
 * 7. زر السابق/التالي
 *
 * عند إكمال الكتابة:
 * - يحسب نسبة دقة الرسم عبر DrawingCanvasView.verifyDrawing()
 * - يمنح نقاط XP حسب الدقة (10 لممتاز، 7 لجيد، 3 لمحاولة)
 * - يمنح نجمة كاملة فقط عند الدقة >= 50%
 * - يفتح ملصقات تشجيعية بعد كل 3 دروس
 */
class LetterDetailActivity : AppCompatActivity() {

    private val TAG = "LetterDetail"

    private lateinit var drawingCanvas: DrawingCanvasView
    private lateinit var tvLetter: TextView
    private lateinit var tvLetterName: TextView
    private lateinit var tvExampleWord: TextView
    private lateinit var ivExampleImage: ImageView
    private lateinit var cardSpeak: CardView
    private lateinit var cardClear: CardView
    private lateinit var tvFeedback: TextView

    private var letterIndex: Int = 0
    private var language: String = "ar"
    private var letter: Letter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_letter_detail)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set content view", e)
            finish()
            return
        }

        language = intent.getStringExtra(EXTRA_LANGUAGE) ?: "ar"
        letterIndex = intent.getIntExtra(EXTRA_LETTER_INDEX, 0)
        loadLetter()

        bindViews()
        setupListeners()
        displayLetter()

        // نطق تلقائي عند الفتح
        drawingCanvas.postDelayed({
            try {
                TtsManager.getInstance(this).speakLetter(letter?.letter ?: "", language)
            } catch (e: Exception) {
                Log.e(TAG, "TTS speak error", e)
            }
        }, 600)
    }

    private fun loadLetter() {
        val list = if (language == "ar") ContentProvider.arabicLetters else ContentProvider.englishLetters
        if (letterIndex < 0 || letterIndex >= list.size) {
            finish()
            return
        }
        letter = list[letterIndex]
    }

    private fun bindViews() {
        drawingCanvas = findViewById(R.id.drawing_canvas)
        tvLetter = findViewById(R.id.tv_letter_big)
        tvLetterName = findViewById(R.id.tv_letter_name)
        tvExampleWord = findViewById(R.id.tv_example_word)
        ivExampleImage = findViewById(R.id.iv_example_image)
        cardSpeak = findViewById(R.id.card_speak)
        cardClear = findViewById(R.id.card_clear)

        // tv_feedback سيُضاف في الـ layout لكن نتجاهل غيابه
        @Suppress("UnsafeCallKotlinNullable")
        tvFeedback = findViewById(R.id.tv_feedback) ?: TextView(this).also {
            it.visibility = View.GONE
        }

        // أزرار الألوان
        findViewById<View>(R.id.btn_color_pink)?.setOnClickListener { changeBrushColor(R.color.brush_red) }
        findViewById<View>(R.id.btn_color_pink_default)?.setOnClickListener { changeBrushColor(R.color.brush_default) }
        findViewById<View>(R.id.btn_color_blue)?.setOnClickListener { changeBrushColor(R.color.brush_blue) }
        findViewById<View>(R.id.btn_color_green)?.setOnClickListener { changeBrushColor(R.color.brush_green) }
        findViewById<View>(R.id.btn_color_purple)?.setOnClickListener { changeBrushColor(R.color.brush_purple) }
        findViewById<View>(R.id.btn_color_orange)?.setOnClickListener { changeBrushColor(R.color.brush_orange) }

        findViewById<View>(R.id.btn_back)?.setOnClickListener { finish() }
        findViewById<View>(R.id.btn_prev)?.setOnClickListener {
            if (letterIndex > 0) {
                try { SoundUtils.getInstance(this).playClick() } catch (_: Exception) {}
                letterIndex--
                loadLetter()
                displayLetter()
            }
        }
        findViewById<View>(R.id.btn_next)?.setOnClickListener {
            val list = if (language == "ar") ContentProvider.arabicLetters else ContentProvider.englishLetters
            if (letterIndex < list.size - 1) {
                try { SoundUtils.getInstance(this).playClick() } catch (_: Exception) {}
                letterIndex++
                loadLetter()
                displayLetter()
            } else {
                Toast.makeText(this, getString(R.string.lesson_complete), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun changeBrushColor(colorRes: Int) {
        try {
            SoundUtils.getInstance(this).playClick()
            drawingCanvas.brushColor = ContextCompat.getColor(this, colorRes)
        } catch (e: Exception) {
            Log.e(TAG, "changeBrushColor error", e)
        }
    }

    private fun setupListeners() {
        cardSpeak?.setOnClickListener {
            letter?.let { l ->
                try {
                    SoundUtils.getInstance(this).playClick()
                    TtsManager.getInstance(this).speakLetter(l.letter, language)
                } catch (e: Exception) {
                    Log.e(TAG, "Speak error", e)
                }
            }
        }
        cardClear?.setOnClickListener {
            try {
                SoundUtils.getInstance(this).playClick()
                drawingCanvas.clearCanvas()
                tvFeedback?.text = ""
            } catch (e: Exception) {
                Log.e(TAG, "Clear error", e)
            }
        }

        findViewById<View>(R.id.btn_complete)?.setOnClickListener {
            onLessonComplete()
        }
    }

    private fun displayLetter() {
        letter?.let { l ->
            tvLetter.text = l.letter
            tvLetterName.text = l.name
            tvExampleWord.text = l.exampleWord
            drawingCanvas.guideLetter = l.letter
            drawingCanvas.language = l.language
            drawingCanvas.clearCanvas()
            tvFeedback?.text = ""

            val resId = resources.getIdentifier(l.exampleDrawable, "drawable", packageName)
            if (resId != 0) {
                ivExampleImage.setImageResource(resId)
                ivExampleImage.visibility = View.VISIBLE
            } else {
                ivExampleImage.visibility = View.GONE
            }

            findViewById<View>(R.id.btn_prev)?.visibility =
                if (letterIndex > 0) View.VISIBLE else View.INVISIBLE

            drawingCanvas.postDelayed({
                try {
                    TtsManager.getInstance(this).speakLetter(l.letter, language)
                } catch (e: Exception) {
                    Log.e(TAG, "TTS auto speak error", e)
                }
            }, 400)
        }
    }

    /**
     * عند إكمال الدرس - يحسب دقة الرسم ويعطي تغذية راجعة مناسبة
     */
    private fun onLessonComplete() {
        if (!drawingCanvas.hasDrawing()) {
            try { SoundUtils.getInstance(this).playError() } catch (_: Exception) {}
            Toast.makeText(this, "ارسم الحرف أولاً ✏️", Toast.LENGTH_SHORT).show()
            tvFeedback?.text = "ارسم الحرف أولاً ✏️"
            tvFeedback?.setTextColor(ContextCompat.getColor(this, R.color.error))
            return
        }

        // نحسب دقة الرسم
        val accuracy = drawingCanvas.verifyDrawing()
        Log.d(TAG, "Drawing accuracy: $accuracy")

        val lessonId = "letter_${language}_$letterIndex"
        val pm = ProgressManager.getInstance(this)

        // المنح المكافآت حسب الدقة (عتبات مخفّضة للأطفال)
        val xpEarned: Int
        val feedback: String
        val feedbackColor: Int

        when {
            accuracy >= 0.35 -> {
                // ممتاز (عتبة مخفّضة من 0.7 إلى 0.35 لتسامح أكثر)
                xpEarned = 10
                feedback = "أحسنت! رسم ممتاز ⭐⭐⭐"
                feedbackColor = R.color.success
                try { SoundUtils.getInstance(this).playSuccess() } catch (_: Exception) {}
                try {
                    val tts = TtsManager.getInstance(this)
                    tts.speak("أحسنت يا بطل!", "ar")
                } catch (_: Exception) {}
            }
            accuracy >= 0.2 -> {
                // جيد جداً (عتبة مخفّضة من 0.4 إلى 0.2)
                xpEarned = 7
                feedback = "جيد جداً! ⭐⭐"
                feedbackColor = R.color.warning
                try { SoundUtils.getInstance(this).playSuccess() } catch (_: Exception) {}
                try {
                    TtsManager.getInstance(this).speak("جيد جداً، استمر!", "ar")
                } catch (_: Exception) {}
            }
            accuracy >= 0.1 -> {
                // جيد - متوسط (عتبة مخفّضة من 0.2 إلى 0.1)
                xpEarned = 5
                feedback = "جيد، تابع التدريب ⭐"
                feedbackColor = R.color.warning
                try { SoundUtils.getInstance(this).playClick() } catch (_: Exception) {}
                try {
                    TtsManager.getInstance(this).speak("جيد، حاول مرة أخرى لتحسّن", "ar")
                } catch (_: Exception) {}
            }
            else -> {
                // ضعيف - نحنّم الطفل بلطف
                xpEarned = 3
                feedback = "حاول تتبع الحرف بإحكام 💪"
                feedbackColor = R.color.error
                try { SoundUtils.getInstance(this).playError() } catch (_: Exception) {}
                try {
                    TtsManager.getInstance(this).speak("حاول أن تطابق الحرف", "ar")
                } catch (_: Exception) {}
            }
        }

        // عرض التغذية الراجعة
        tvFeedback?.text = feedback
        try {
            tvFeedback?.setTextColor(ContextCompat.getColor(this, feedbackColor))
        } catch (_: Exception) {}

        // تسجيل الإنجاز (مرة واحدة لكل درس)
        if (!pm.isLessonCompleted(lessonId)) {
            pm.markLessonCompleted(lessonId)
            pm.addXp(xpEarned)
            // نمنح نجمة كاملة فقط عند الدقة >= 50%
            if (accuracy >= 0.5) {
                pm.addStar()
            }
            pm.updateStreak()

            // فحص الملصقات
            val stickerRes = pm.checkStickerUnlock()
            if (stickerRes != null) {
                Toast.makeText(this, getString(R.string.sticker_unlocked_msg), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "+$xpEarned نقطة ⭐", Toast.LENGTH_SHORT).show()
            }
        } else {
            // الدرس مكتمل من قبل - نعطي نصف XP فقط للتحفيز
            pm.addXp(xpEarned / 2)
            Toast.makeText(this, getString(R.string.completed), Toast.LENGTH_SHORT).show()
        }

        // الانتقال للتالي بعد قليل
        drawingCanvas.postDelayed({
            try {
                val list = if (language == "ar") ContentProvider.arabicLetters else ContentProvider.englishLetters
                if (letterIndex < list.size - 1) {
                    letterIndex++
                    loadLetter()
                    displayLetter()
                } else {
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Navigation error", e)
                finish()
            }
        }, 1800)
    }

    companion object {
        const val EXTRA_LANGUAGE = "extra_language"
        const val EXTRA_LETTER_INDEX = "extra_letter_index"
    }
}
