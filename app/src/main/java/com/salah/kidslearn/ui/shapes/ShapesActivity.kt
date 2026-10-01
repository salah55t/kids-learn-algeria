package com.salah.kidslearn.ui.shapes

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
import com.salah.kidslearn.data.ContentProvider
import com.salah.kidslearn.data.ShapeItem
import com.salah.kidslearn.utils.ProgressManager
import com.salah.kidslearn.utils.SoundUtils
import com.salah.kidslearn.utils.TtsManager

class ShapesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shapes)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rv_shapes)
        rv.layoutManager = GridLayoutManager(this, 2)
        rv.adapter = ShapesAdapter(ContentProvider.shapes) { shape, position ->
            SoundUtils.getInstance(this).playClick()
            TtsManager.getInstance(this).speak(shape.name, "ar")
            TtsManager.getInstance(this).speak(shape.englishName, "en")

            val lessonId = "shape_$position"
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

class ShapesAdapter(
    private val items: List<ShapeItem>,
    private val onClick: (ShapeItem, Int) -> Unit
) : RecyclerView.Adapter<ShapesAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivShape: ImageView = view.findViewById(R.id.iv_shape)
        val tvName: TextView = view.findViewById(R.id.tv_shape_name)
        val tvEnglishName: TextView = view.findViewById(R.id.tv_shape_english_name)
        val cv: CardView = view.findViewById(R.id.card_shape)
        val completed: View = view.findViewById(R.id.completed_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_shape, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = items[position]
        holder.tvName.text = s.name
        holder.tvEnglishName.text = s.englishName

        val resId = holder.itemView.context.resources.getIdentifier(
            s.drawable, "drawable", holder.itemView.context.packageName
        )
        if (resId != 0) {
            holder.ivShape.setImageResource(resId)
        }

        val colors = intArrayOf(
            R.color.bg_section_yellow,
            R.color.bg_section_blue,
            R.color.bg_section_green,
            R.color.bg_section_pink,
            R.color.bg_section_purple,
            R.color.bg_section_orange
        )
        holder.cv.setCardBackgroundColor(
            androidx.core.content.ContextCompat.getColor(
                holder.itemView.context, colors[position % colors.size]
            )
        )

        val lessonId = "shape_$position"
        val isDone = ProgressManager.getInstance(holder.itemView.context).isLessonCompleted(lessonId)
        holder.completed.visibility = if (isDone) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener { onClick(s, position) }
    }

    override fun getItemCount() = items.size
}
