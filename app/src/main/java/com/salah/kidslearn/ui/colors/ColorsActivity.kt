package com.salah.kidslearn.ui.colors

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
import com.salah.kidslearn.data.ColorItem
import com.salah.kidslearn.data.ContentProvider
import com.salah.kidslearn.utils.ProgressManager
import com.salah.kidslearn.utils.SoundUtils
import com.salah.kidslearn.utils.TtsManager

class ColorsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colors)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rv_colors)
        rv.layoutManager = GridLayoutManager(this, 2)
        rv.adapter = ColorsAdapter(ContentProvider.colors) { color, position ->
            SoundUtils.getInstance(this).playClick()
            TtsManager.getInstance(this).speak(color.name, "ar")
            TtsManager.getInstance(this).speak(color.englishName, "en")

            val lessonId = "color_$position"
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

class ColorsAdapter(
    private val items: List<ColorItem>,
    private val onClick: (ColorItem, Int) -> Unit
) : RecyclerView.Adapter<ColorsAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val colorCircle: View = view.findViewById(R.id.v_color_circle)
        val tvName: TextView = view.findViewById(R.id.tv_color_name)
        val tvEnglishName: TextView = view.findViewById(R.id.tv_color_english_name)
        val ivExample: ImageView = view.findViewById(R.id.iv_example)
        val completed: View = view.findViewById(R.id.completed_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.tvName.text = c.name
        holder.tvEnglishName.text = c.englishName
        holder.colorCircle.setBackgroundColor(android.graphics.Color.parseColor(c.colorHex))

        val resId = holder.itemView.context.resources.getIdentifier(
            c.exampleDrawable, "drawable", holder.itemView.context.packageName
        )
        if (resId != 0) {
            holder.ivExample.setImageResource(resId)
            holder.ivExample.visibility = View.VISIBLE
        } else {
            holder.ivExample.visibility = View.GONE
        }

        val lessonId = "color_$position"
        val isDone = ProgressManager.getInstance(holder.itemView.context).isLessonCompleted(lessonId)
        holder.completed.visibility = if (isDone) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener { onClick(c, position) }
    }

    override fun getItemCount() = items.size
}
