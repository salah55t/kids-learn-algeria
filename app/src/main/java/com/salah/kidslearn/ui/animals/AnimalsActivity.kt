package com.salah.kidslearn.ui.animals

import android.os.Bundle
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

class AnimalsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animals)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rv_animals)
        rv.layoutManager = GridLayoutManager(this, 2)
        rv.adapter = AnimalsAdapter(ContentProvider.animals) { animal, position ->
            SoundUtils.getInstance(this).playClick()
            // نطق اسم الحيوان + صوته
            TtsManager.getInstance(this).speak("${animal.name}! ${animal.sound}", "ar")
            TtsManager.getInstance(this).speak(animal.englishName, "en")

            val lessonId = "animal_$position"
            val pm = ProgressManager.getInstance(this)
            if (!pm.isLessonCompleted(lessonId)) {
                pm.markLessonCompleted(lessonId)
                pm.addXp(5)
                pm.addStar()
                SoundUtils.getInstance(this).playSuccess()
            }
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
        holder.tvSound.text = "🔊 ${a.sound}"

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
