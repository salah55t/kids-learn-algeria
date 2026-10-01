package com.salah.kidslearn.ui.letters

import android.graphics.Bitmap
import android.os.Bundle
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
 * عند إكمال الكتابة: يكافئ الطفل بـ XP + نجمة + صوت نجاح
 */
class LetterDetailActivity : AppCompatActivity() {

    private lateinit var drawingCanvas: DrawingCanvasView
    private lateinit var tvLetter: TextView
    private lateinit var tvLetterName: TextView
    private lateinit var tvExampleWord: TextView
    private lateinit var ivExampleImage: ImageView
    private lateinit var cardSpeak: CardView
    private lateinit var cardClear: CardView

    private var letterIndex: Int = 0
    private var language: String = "ar"
    private var letter: Letter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_detail)

        language = intent.getStringExtra(EXTRA_LANGUAGE) ?: "ar"
        letterIndex = intent.getIntExtra(EXTRA_LETTER_INDEX, 0)
        loadLetter()

        bindViews()
        setupListeners()
        displayLetter()

        // نطق تلقائي عند الفتح
        drawingCanvas.postDelayed({
            TtsManager.getInstance(this).speakLetter(letter?.letter ?: "", language)
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

        // أزرار الألوان
        findViewById<View>(R.id.btn_color_pink).setOnClickListener { changeBrushColor(R.color.brush_red) }
        findViewById<View>(R.id.btn_color_blue).setOnClickListener { changeBrushColor(R.color.brush_blue) }
        findViewById<View>(R.id.btn_color_green).setOnClickListener { changeBrushColor(R.color.brush_green) }
        findViewById<View>(R.id.btn_color_purple).setOnClickListener { changeBrushColor(R.color.brush_purple) }
        findViewById<View>(R.id.btn_color_orange).setOnClickListener { changeBrushColor(R.color.brush_orange) }
        findViewById<View>(R.id.btn_color_pink_default).setOnClickListener { changeBrushColor(R.color.brush_default) }

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_prev).setOnClickListener {
            if (letterIndex > 0) {
                SoundUtils.getInstance(this).playClick()
                letterIndex--
                loadLetter()
                displayLetter()
            }
        }
        findViewById<View>(R.id.btn_next).setOnClickListener {
            val list = if (language == "ar") ContentProvider.arabicLetters else ContentProvider.englishLetters
            if (letterIndex < list.size - 1) {
                SoundUtils.getInstance(this).playClick()
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
        SoundUtils.getInstance(this).playClick()
        drawingCanvas.brushColor = ContextCompat.getColor(this, colorRes)
    }

    private fun setupListeners() {
        cardSpeak.setOnClickListener {
            letter?.let {
                SoundUtils.getInstance(this).playClick()
                TtsManager.getInstance(this).speakLetter(it.letter, language)
            }
        }
        cardClear.setOnClickListener {
            SoundUtils.getInstance(this).playClick()
            drawingCanvas.clearCanvas()
        }
        // عند بدء الكتابة، نظهر زر "اكتمل الدرس" يمكن إضافة منطق الكشف التلقائي
    }

    private fun displayLetter() {
        letter?.let { l ->
            tvLetter.text = l.letter
            tvLetterName.text = l.name
            tvExampleWord.text = if (language == "ar") l.exampleWord else l.exampleWord
            drawingCanvas.guideLetter = l.letter
            drawingCanvas.language = l.language
            drawingCanvas.clearCanvas()

            // تحميل رسم الكلمة المثال
            val resId = resources.getIdentifier(l.exampleDrawable, "drawable", packageName)
            if (resId != 0) {
                ivExampleImage.setImageResource(resId)
                ivExampleImage.visibility = View.VISIBLE
            } else {
                ivExampleImage.visibility = View.GONE
            }

            // إظهار/إخفاء زر السابق
            findViewById<View>(R.id.btn_prev).visibility =
                if (letterIndex > 0) View.VISIBLE else View.INVISIBLE

            // نطق الحرف تلقائياً
            drawingCanvas.postDelayed({
                TtsManager.getInstance(this).speakLetter(l.letter, language)
            }, 400)

            // زر "اكمل الدرس"
            findViewById<View>(R.id.btn_complete).setOnClickListener {
                onLessonComplete()
            }
        }
    }

    private fun onLessonComplete() {
        if (!drawingCanvas.hasDrawing()) {
            Toast.makeText(this, "ارسم الحرف أولاً ✏️", Toast.LENGTH_SHORT).show()
            return
        }

        val lessonId = "letter_${language}_$letterIndex"
        val pm = ProgressManager.getInstance(this)
        if (!pm.isLessonCompleted(lessonId)) {
            pm.markLessonCompleted(lessonId)
            pm.addXp(10)
            pm.addStar()
            pm.updateStreak()
            SoundUtils.getInstance(this).playSuccess()

            // فحص الملصقات
            val stickerRes = pm.checkStickerUnlock()
            if (stickerRes != null) {
                Toast.makeText(this, getString(R.string.sticker_unlocked_msg), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "أحسنت! +10 نقاط ⭐",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.completed), Toast.LENGTH_SHORT).show()
        }

        // الانتقال للتالي بعد قليل
        drawingCanvas.postDelayed({
            val list = if (language == "ar") ContentProvider.arabicLetters else ContentProvider.englishLetters
            if (letterIndex < list.size - 1) {
                letterIndex++
                loadLetter()
                displayLetter()
            } else {
                finish()
            }
        }, 1200)
    }

    companion object {
        const val EXTRA_LANGUAGE = "extra_language"
        const val EXTRA_LETTER_INDEX = "extra_letter_index"
    }
}
