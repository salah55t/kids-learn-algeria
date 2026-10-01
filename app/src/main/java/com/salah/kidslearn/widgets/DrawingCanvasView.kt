package com.salah.kidslearn.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
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
 * - تحقق من صحة الكتابة عبر حساب نسبة التداخل بين الرسم والحرف الدليلي
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
        try {
            color = ContextCompat.getColor(this@DrawingCanvasView.context, R.color.guide_letter)
            style = Paint.Style.STROKE
            strokeWidth = 80f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            alpha = 60
            isAntiAlias = true
        } catch (e: Exception) {
            Log.e("DrawingCanvas", "guidePaint init error", e)
        }
    }

    // لون القلم الافتراضي
    var brushColor: Int = try {
        ContextCompat.getColor(context, R.color.brush_default)
    } catch (e: Exception) {
        Color.parseColor("#E91E63")  // fallback
    }
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

    // الحرف الدليلي
    var guideLetter: String = ""
        set(value) {
            field = value
            invalidate()
        }

    // لغة الحرف
    var language: String = "ar"

    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private val touchTolerance = 4f

    var onDrawingStart: (() -> Unit)? = null
    var onDrawingEnd: (() -> Unit)? = null
    var onDrawingProgress: ((Float) -> Unit)? = null

    init {
        try {
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
        } catch (e: Exception) {
            Log.e("DrawingCanvas", "init block error", e)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            // خلفية بيضاء مدوّرة
            val bgPaint = Paint().apply {
                color = Color.WHITE
                isAntiAlias = true
            }
            val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            canvas.drawRoundRect(rect, 24f, 24f, bgPaint)

            // رسم الحرف الدليلي
            if (guideLetter.isNotEmpty()) {
                val textSize = minOf(width, height) * 0.65f
                guidePaint.textSize = textSize
                guidePaint.textAlign = Paint.Align.CENTER

                val textBounds = Rect()
                guidePaint.getTextBounds(guideLetter, 0, guideLetter.length, textBounds)

                val baseline = height / 2f + textBounds.height() / 2f - textBounds.bottom
                canvas.drawText(guideLetter, width / 2f, baseline, guidePaint)
            }

            paths.forEach { (path, paint) ->
                canvas.drawPath(path, paint)
            }

            currentPath?.let { canvas.drawPath(it, currentPaint) }
        } catch (e: Exception) {
            Log.e("DrawingCanvas", "onDraw error", e)
        }
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
            onDrawingProgress?.invoke(0.5f)
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

    fun clearCanvas() {
        paths.clear()
        currentPath = null
        invalidate()
    }

    fun hasDrawing(): Boolean = paths.isNotEmpty() || currentPath != null

    /**
     * التحقق من صحة الرسم - يحسب نسبة التداخل بين رسم الطفل والحرف الدليلي
     *
     * @return نسبة من 0.0 إلى 1.0:
     *   - 1.0 = رسم مثالي (يطابق الحرف الدليلي تماماً)
     *   - 0.5 = تغطية 50% من مساحة الحرف الدليلي
     *   - 0.0 = لا يوجد تداخل (الطفل لم يكتب الحرف بعد)
     *
     * الخوارزمية:
     * 1. نرسم الحرف الدليلي على Bitmap (semi-transparent)
     * 2. نرسم رسم الطفل على Bitmap آخر (بقلم أعرض للتسامح)
     * 3. نأخذ عينات بكسل من كل Bitmap
     * 4. نحسب نسبة البكسلات في رسم الطفل التي تتداخل مع الحرف الدليلي
     */
    fun verifyDrawing(): Float {
        if (guideLetter.isEmpty() || paths.isEmpty()) return 0.0f
        if (width == 0 || height == 0) return 0.0f

        try {
            // 1. نرسم الحرف الدليلي على Bitmap (بـ stroke واسع للتسامح)
            val guideBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val guideCanvas = Canvas(guideBmp)
            val guideVerifyPaint = Paint(guidePaint).apply {
                alpha = 255  // رسم معتم بدلاً من الشفاف
                style = Paint.Style.STROKE
                strokeWidth = 50f  // حد أدنى للتسامح
            }
            val textSize = minOf(width, height) * 0.65f
            guideVerifyPaint.textSize = textSize
            guideVerifyPaint.textAlign = Paint.Align.CENTER

            val textBounds = Rect()
            guideVerifyPaint.getTextBounds(guideLetter, 0, guideLetter.length, textBounds)
            val baseline = height / 2f + textBounds.height() / 2f - textBounds.bottom
            guideCanvas.drawText(guideLetter, width / 2f, baseline, guideVerifyPaint)

            // 2. نرسم رسم الطفل على Bitmap آخر
            val userBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val userCanvas = Canvas(userBmp)
            val userVerifyPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                strokeWidth = brushSize * 1.5f  // قلم أعرض للتسامح
                color = Color.BLACK
            }
            paths.forEach { (path, _) ->
                userCanvas.drawPath(path, userVerifyPaint)
            }
            currentPath?.let { userCanvas.drawPath(it, userVerifyPaint) }

            // 3. نأخذ عينات (نخطّي كل بكسلين لتسريع الأداء)
            var guidePixels = 0
            var overlapPixels = 0
            val step = 3  // عينة كل 3 بكسلات لتسريع الحساب

            for (y in 0 until height step step) {
                for (x in 0 until width step step) {
                    val guidePixel = guideBmp.getPixel(x, y)
                    val userPixel = userBmp.getPixel(x, y)
                    val guideAlpha = Color.alpha(guidePixel)
                    val userAlpha = Color.alpha(userPixel)

                    if (guideAlpha > 30) {
                        guidePixels++
                        if (userAlpha > 30) {
                            overlapPixels++
                        }
                    }
                }
            }

            // إعادة تدوير الـ Bitmaps لتحرير الذاكرة
            guideBmp.recycle()
            userBmp.recycle()

            val score = if (guidePixels == 0) 0.0f else overlapPixels.toFloat() / guidePixels
            Log.d("DrawingCanvas", "verifyDrawing: guide=$guidePixels overlap=$overlapPixels score=$score")
            return score.coerceIn(0.0f, 1.0f)

        } catch (e: Exception) {
            Log.e("DrawingCanvas", "verifyDrawing error", e)
            return 0.0f
        }
    }

    /**
     * نسخة محسّنة من toBitmap تستعمل رسم الطفل فقط (للمشاركة)
     */
    fun toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}
