package com.salah.kidslearn.ui.numbers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salah.kidslearn.R
import com.salah.kidslearn.data.ContentProvider
import com.salah.kidslearn.data.NumberItem
import com.salah.kidslearn.utils.ProgressManager
import com.salah.kidslearn.utils.SoundUtils
import com.salah.kidslearn.utils.TtsManager

/**
 * قسم الأرقام (0-10) - كل بطاقة تعرض رقماً كبيراً + اسمه بالعربية والإنجليزية
 * الضغط على البطاقة: ينطق الرقم + يمنح نقاط XP
 */
class NumbersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rv_numbers)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = NumbersAdapter(ContentProvider.numbers) { number, position ->
            SoundUtils.getInstance(this).playClick()
            TtsManager.getInstance(this).speak(number.name, "ar")
            TtsManager.getInstance(this).speak(number.englishName, "en")

            val lessonId = "number_$position"
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

class NumbersAdapter(
    private val items: List<NumberItem>,
    private val onClick: (NumberItem, Int) -> Unit
) : RecyclerView.Adapter<NumbersAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tv_number)
        val tvName: TextView = view.findViewById(R.id.tv_number_name)
        val tvEnglishName: TextView = view.findViewById(R.id.tv_number_english_name)
        val cv: CardView = view.findViewById(R.id.card_number)
        val completed: View = view.findViewById(R.id.completed_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_number, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = items[position]
        holder.tvNumber.text = n.value.toString()
        holder.tvName.text = n.name
        holder.tvEnglishName.text = n.englishName

        // ألوان البطاقات - تدوير
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

        val lessonId = "number_$position"
        val isDone = ProgressManager.getInstance(holder.itemView.context).isLessonCompleted(lessonId)
        holder.completed.visibility = if (isDone) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener { onClick(n, position) }
    }

    override fun getItemCount() = items.size
}
