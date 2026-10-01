package com.salah.kidslearn.ui.animals

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salah.kidslearn.R
import com.salah.kidslearn.data.AnimalItem
import com.salah.kidslearn.data.ContentProvider
import com.salah.kidslearn.utils.ProgressManager
import com.salah.kidslearn.utils.SoundUtils
import com.salah.kidslearn.utils.TtsManager

/**
 * قسم الحيوانات - يعرض 10 حيوانات مع أصواتها
 *
 * عند الضغط على حيوان:
 * 1. إيقاف كل صوت قيد التشغيل لمنع التداخل
 * 2. إن وُجد ملف صوتي حقيقي نشغّله أولاً
 * 3. بعد انتهائه (تأخير 2.5 ثانية) ننطق اسم الحيوان بالعربية
 * 4. ثم نطق صوته (onomatopoeia) بنبرة مخصصة
 * 5. ثم نطق اسمه بالإنجليزية
 *
 * كل الأصوات تُشغل تسلسلياً (لا تداخل).
 */
class AnimalsActivity : AppCompatActivity() {

    private val TAG = "AnimalsActivity"
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_animals)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set content view", e)
            finish()
            return
        }

        findViewById<View>(R.id.btn_back)?.setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rv_animals)
        rv.layoutManager = GridLayoutManager(this, 2)
        rv.adapter = AnimalsAdapter(ContentProvider.animals) { animal, position ->
            playClickSafe()
            playAnimalSound(animal)

            val lessonId = "animal_$position"
            val pm = ProgressManager.getInstance(this)
            try {
                if (!pm.isLessonCompleted(lessonId)) {
                    pm.markLessonCompleted(lessonId)
                    pm.addXp(5)
                    pm.addStar()
                    pm.updateStreak()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Progress update failed", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // إيقاف كل الأصوات عند مغادرة النشاط
        try {
            handler.removeCallbacksAndMessages(null)
            SoundUtils.getInstance(this).stopAll()
            TtsManager.getInstance(this).stopAll()
        } catch (e: Exception) {
            Log.e(TAG, "onDestroy stop sounds failed", e)
        }
    }

    /**
     * يشغّل صوت الحيوان بشكل تسلسلي (لا تداخل):
     *
     * التسلسل:
     * - T+0: إيقاف كل صوت قيد التشغيل
     * - T+0: إن وُجد صوت حقيقي نشغّله عبر SoundPool
     * - T+0 أو T+2500: TTS ينطق اسم الحيوان بالعربية (بنبرة عادية)
     * - T+(السابق + 1500): TTS ينطق صوت الحيوان بنبرة مخصصة
     * - T+(السابق + 1500): TTS ينطق الاسم الإنجليزي
     */
    private fun playAnimalSound(animal: AnimalItem) {
        // 1. إيقاف كل صوت قيد التشغيل وإلغاء أي callbacks مجدولة
        handler.removeCallbacksAndMessages(null)
        try {
            SoundUtils.getInstance(this).stopAll()
            TtsManager.getInstance(this).stopAll()
        } catch (e: Exception) {
            Log.e(TAG, "stopAll failed", e)
        }

        val hasRealSound = animal.soundFile.isNotEmpty()
        var currentDelay = 0L  // نضيف التأخيرات بشكل تراكمي

        // 2. تشغيل الصوت الحقيقي إن وُجد
        if (hasRealSound) {
            try {
                val played = SoundUtils.getInstance(this).playRaw(animal.soundFile)
                Log.d(TAG, "Played real sound ${animal.soundFile}: $played")
            } catch (e: Exception) {
                Log.e(TAG, "playRaw failed", e)
            }
            // ننتظر انتهاء الصوت الحقيقي قبل بدء TTS
            currentDelay += 2500L
        }

        // 3. نطق اسم الحيوان بالعربية (بنبرة عادية)
        scheduleTts(currentDelay) {
            try {
                TtsManager.getInstance(this).speak(animal.name, "ar")
            } catch (e: Exception) {
                Log.e(TAG, "TTS Arabic name failed", e)
            }
        }
        currentDelay += 1500L

        // 4. نطق صوت الحيوان (onomatopoeia) بنبرة مخصصة
        scheduleTts(currentDelay) {
            try {
                TtsManager.getInstance(this).speakWithPitch(
                    animal.sound,
                    animal.ttsPitch,
                    "ar"
                )
            } catch (e: Exception) {
                Log.e(TAG, "TTS pitch speak failed", e)
            }
        }
        currentDelay += 1500L

        // 5. نطق الاسم الإنجليزي (بنبرة عادية)
        scheduleTts(currentDelay) {
            try {
                TtsManager.getInstance(this).speak(animal.englishName, "en")
            } catch (e: Exception) {
                Log.e(TAG, "TTS English speak failed", e)
            }
        }
    }

    /**
     * يجدول استدعاء TTS بعد تأخير محدد
     */
    private fun scheduleTts(delayMs: Long, action: () -> Unit) {
        handler.postDelayed({
            try { action() } catch (e: Exception) {
                Log.e(TAG, "scheduled TTS action failed", e)
            }
        }, delayMs)
    }

    private fun playClickSafe() {
        try { SoundUtils.getInstance(this).playClick() } catch (e: Exception) {
            Log.e(TAG, "playClick failed", e)
        }
    }
}

class AnimalsAdapter(
    private val items: List<AnimalItem>,
    private val onClick: (AnimalItem, Int) -> Unit
) : RecyclerView.Adapter<AnimalsAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivAnimal: ImageView = view.findViewById(R.id.iv_animal)
        val tvName: TextView = view.findViewById(R.id.tv_animal_name)
        val tvEnglishName: TextView = view.findViewById(R.id.tv_animal_english_name)
        val tvSound: TextView = view.findViewById(R.id.tv_animal_sound)
        val cv: CardView = view.findViewById(R.id.card_animal)
        val completed: View = view.findViewById(R.id.completed_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_animal, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = items[position]
        holder.tvName.text = a.name
        holder.tvEnglishName.text = a.englishName

        // نعرض أيقونة "🎵" للأصوات الحقيقية للتمييز
        val soundIcon = if (a.soundFile.isNotEmpty()) "🎵🔊" else "🔊"
        holder.tvSound.text = "$soundIcon ${a.sound}"

        val resId = holder.itemView.context.resources.getIdentifier(
            a.drawable, "drawable", holder.itemView.context.packageName
        )
        if (resId != 0) {
            holder.ivAnimal.setImageResource(resId)
        }

        val colors = intArrayOf(
            R.color.bg_section_orange,
            R.color.bg_section_yellow,
            R.color.bg_section_green,
            R.color.bg_section_pink,
            R.color.bg_section_blue,
            R.color.bg_section_purple
        )
        holder.cv.setCardBackgroundColor(
            androidx.core.content.ContextCompat.getColor(
                holder.itemView.context, colors[position % colors.size]
            )
        )

        val lessonId = "animal_$position"
        val isDone = ProgressManager.getInstance(holder.itemView.context).isLessonCompleted(lessonId)
        holder.completed.visibility = if (isDone) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener { onClick(a, position) }
    }

    override fun getItemCount() = items.size
}
