package com.salah.kidslearn.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.salah.kidslearn.R

/**
 * لوح الرسم التفاعلي - يسمح للطفل بتتبع الحرف بإصبعه
 * يرسم خلفية شفافة فوق رسم الحرف الدليلي
 *
 * الميزات:
 * - تتبع حركة الإصبع بسلاسة باستخدام منحنيات بيزير
 * - لون قلم رصاص زاهٍ (أحمر/أزرق) مع تأثير ضربة قلم
 * - دعم نمط "تتبع الحرف" (مع حرف خفيف في الخلفية) أو "رسم حر"
 * - زر مسح سريع
 */
class DrawingCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var currentPath: Path? = null
    private var currentPaint: Paint = Paint()

    // رسم الحرف الدليلي (خفيف في الخلفية)
    private val guidePaint = Paint().apply {
        color = ContextCompat.getColor(this@DrawingCanvasView.context, R.color.guide_letter)
        style = Paint.Style.STROKE
        strokeWidth = 80f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        alpha = 60
        isAntiAlias = true
    }

    // لون القلم الافتراضي (يتغير حسب اختيار الطفل)
    var brushColor: Int = ContextCompat.getColor(context, R.color.brush_default)
        set(value) {
            field = value
            currentPaint.color = value
            invalidate()
        }

    // حجم القلم
    var brushSize: Float = 24f
        set(value) {
            field = value
            currentPaint.strokeWidth = value
            invalidate()
        }

    // الحرف الدليلي (مثال: "ا")
    var guideLetter: String = ""
        set(value) {
            field = value
            invalidate()
        }

    // لغة الحرف (تحدد اتجاه الرسم)
    var language: String = "ar"

    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private val touchTolerance = 4f

    // دالة callback عند بدء الكتابة
    var onDrawingStart: (() -> Unit)? = null
    var onDrawingEnd: (() -> Unit)? = null
    var onDrawingProgress: ((Float) -> Unit)? = null  // نسبة التغطية 0..1

    init {
        // إعداد القلم الافتراضي
        currentPaint.apply {
            isAntiAlias = true
            isDither = true
            color = brushColor
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = brushSize
        }
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // رسم خلفية بيضاء مدوّرة
        val bgPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
        }
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect, 24f, 24f, bgPaint)

        // رسم الحرف الدليلي في الوسط (إذا كان موجوداً)
        if (guideLetter.isNotEmpty()) {
            val textSize = minOf(width, height) * 0.65f
            guidePaint.textSize = textSize
            guidePaint.textAlign = Paint.Align.CENTER

            val textBounds = android.graphics.Rect()
            guidePaint.getTextBounds(guideLetter, 0, guideLetter.length, textBounds)

            val baseline = height / 2f + textBounds.height() / 2f - textBounds.bottom
            canvas.drawText(guideLetter, width / 2f, baseline, guidePaint)
        }

        // رسم كل المسارات المرسومة
        paths.forEach { (path, paint) ->
            canvas.drawPath(path, paint)
        }

        // رسم المسار الحالي
        currentPath?.let { canvas.drawPath(it, currentPaint) }
    }

    private fun startTouch(x: Float, y: Float) {
        currentPath = Path().apply {
            moveTo(x, y)
        }
        lastX = x
        lastY = y
        onDrawingStart?.invoke()
    }

    private fun moveTouch(x: Float, y: Float) {
        val dx = Math.abs(x - lastX)
        val dy = Math.abs(y - lastY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            currentPath?.apply {
                quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
            }
            lastX = x
            lastY = y
            onDrawingProgress?.invoke(0.5f) // مبسط - يمكن حساب النسبة الفعلية
        }
    }

    private fun endTouch() {
        currentPath?.let { path ->
            paths.add(Pair(path, Paint(currentPaint)))
        }
        currentPath = null
        onDrawingEnd?.invoke()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startTouch(x, y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                moveTouch(x, y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                endTouch()
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                endTouch()
                invalidate()
                return true
            }
        }
        return false
    }

    /**
     * مسح الرسم
     */
    fun clearCanvas() {
        paths.clear()
        currentPath = null
        invalidate()
    }

    /**
     * هل قام الطفل بالرسم؟
     */
    fun hasDrawing(): Boolean = paths.isNotEmpty() || currentPath != null

    /**
     * حفظ الرسم كـ Bitmap (لمشاركة الإنجاز)
     */
    fun toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}
