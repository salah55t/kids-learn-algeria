package com.salah.kidslearn.ui.animals

import android.os.Bundle
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
 * 1. إن وُجد ملف صوتي حقيقي في res/raw (مثلاً animal_cat.ogg)
 *    نشغّله عبر SoundPool
 * 2. إضافة لذلك، ننطق اسم الحيوان وصوته (onomatopoeia)
 *    عبر TTS بنبرة مخصصة لمحاكاة صوت الحيوان
 * 3. نمنح الطفل نقاط XP ونجمة (مرة لكل حيوان)
 *
 * الأصوات الحقيقية المتوفرة حالياً (من Wikimedia Commons، CC-BY-SA):
 * - قطة (animal_cat.ogg)  ← صوت مواء حقيقي
 * - بطة (animal_duck.ogg)  ← صوت بط حقيقي
 * - حصان (animal_horse.ogg) ← صوت صهيل حقيقي
 *
 * لبقية الحيوانات نستعمل TTS بنبرة مخصصة:
 * - كلب: بنبرة 0.7 (نباح منخفض)
 * - أسد: بنبرة 0.5 (زئير منخفض جداً)
 * - بقرة: بنبرة 0.8 (خوار)
 * - خروف: بنبرة 1.3 (ثغاء مرتفع)
 * - دجاجة: بنبرة 1.8 (صياح مرتفع)
 * - سمكة: بنبرة 1.6 (بل بل)
 * - فيل: بنبرة 0.4 (بوق منخفض جداً)
 */
class AnimalsActivity : AppCompatActivity() {

    private val TAG = "AnimalsActivity"

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
                    SoundUtils.getInstance(this).playSuccess()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Progress update failed", e)
            }
        }
    }

    /**
     * يشغّل صوت الحيوان:
     * 1. إن وُجد ملف صوتي حقيقي نشغّله عبر SoundPool
     * 2. ننطق اسم الحيوان وصوته عبر TTS بنبرة مخصصة
     */
    private fun playAnimalSound(animal: AnimalItem) {
        try {
            // 1. تشغيل الصوت الحقيقي إن وُجد
            if (animal.soundFile.isNotEmpty()) {
                val played = SoundUtils.getInstance(this).playRaw(animal.soundFile)
                Log.d(TAG, "Played real sound ${animal.soundFile}: $played")
            }

            // 2. نطق اسم الحيوان عبر TTS بصوت عادي
            TtsManager.getInstance(this).speak("${animal.name}!", "ar")

            // 3. بعد قليل نطق صوت الحيوان (onomatopoeia) بنبرة مخصصة
            // نؤخر قليلاً لتفادي التداخل مع الصوت الحقيقي إن كان موجوداً
            val delay = if (animal.soundFile.isNotEmpty()) 1500L else 800L
            drawingCanvasPostDelayed(delay) {
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

            // 4. نطق الاسم الإنجليزي بعد ذلك
            drawingCanvasPostDelayed(delay + 1200) {
                try {
                    TtsManager.getInstance(this).speak(animal.englishName, "en")
                } catch (e: Exception) {
                    Log.e(TAG, "TTS English speak failed", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "playAnimalSound error", e)
        }
    }

    /**
     * نسخة مغلّفة بـ try-catch من postDelayed
     */
    private fun drawingCanvasPostDelayed(delay: Long, action: () -> Unit) {
        // نستعمل الـ RecyclerView لأنه موجود دائماً
        findViewById<View>(R.id.rv_animals)?.postDelayed({
            try { action() } catch (e: Exception) {
                Log.e(TAG, "postDelayed action failed", e)
            }
        }, delay)
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

        // نعرض أيقونة "🔊" مع رمز يوضح إذا كان الصوت حقيقياً
        val soundIcon = if (a.soundFile.isNotEmpty()) "🔊🎵" else "🔊"
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
