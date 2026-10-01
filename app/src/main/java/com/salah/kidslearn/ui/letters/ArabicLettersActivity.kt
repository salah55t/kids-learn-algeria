package com.salah.kidslearn.ui.letters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salah.kidslearn.R
import com.salah.kidslearn.data.ContentProvider
import com.salah.kidslearn.data.Letter
import com.salah.kidslearn.utils.ProgressManager
import com.salah.kidslearn.utils.SoundUtils

/**
 * شبكة الحروف العربية - 28 حرف في شبكة 4 أعمدة
 * كل حرف يفتح LetterDetailActivity لكتابته وتعلمه
 */
class ArabicLettersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letters_grid)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_title).text = getString(R.string.section_arabic_letters)
        findViewById<TextView>(R.id.tv_subtitle).text = getString(R.string.arabic_letters_count)

        val rv = findViewById<RecyclerView>(R.id.rv_letters)
        rv.layoutManager = GridLayoutManager(this, 4)
        rv.adapter = LettersAdapter(ContentProvider.arabicLetters) { letter, position ->
            SoundUtils.getInstance(this).playClick()
            val intent = Intent(this, LetterDetailActivity::class.java).apply {
                putExtra(LetterDetailActivity.EXTRA_LANGUAGE, "ar")
                putExtra(LetterDetailActivity.EXTRA_LETTER_INDEX, position)
            }
            startActivity(intent)
        }
    }
}

class EnglishLettersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letters_grid)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_title).text = getString(R.string.section_english_letters)
        findViewById<TextView>(R.id.tv_subtitle).text = getString(R.string.english_letters_count)

        val rv = findViewById<RecyclerView>(R.id.rv_letters)
        rv.layoutManager = GridLayoutManager(this, 4)
        rv.adapter = LettersAdapter(ContentProvider.englishLetters) { letter, position ->
            SoundUtils.getInstance(this).playClick()
            val intent = Intent(this, LetterDetailActivity::class.java).apply {
                putExtra(LetterDetailActivity.EXTRA_LANGUAGE, "en")
                putExtra(LetterDetailActivity.EXTRA_LETTER_INDEX, position)
            }
            startActivity(intent)
        }
    }
}

class LettersAdapter(
    private val letters: List<Letter>,
    private val onClick: (Letter, Int) -> Unit
) : RecyclerView.Adapter<LettersAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvLetter: TextView = view.findViewById(R.id.tv_letter)
        val cv: androidx.cardview.widget.CardView = view.findViewById(R.id.card_letter)
        val completed: View = view.findViewById(R.id.completed_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_letter, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val l = letters[position]
        holder.tvLetter.text = l.letter

        // ألوان مختلفة لكل بطاقة (دوران عبر لوحة الألوان)
        val colors = intArrayOf(
            R.color.bg_section_pink,
            R.color.bg_section_yellow,
            R.color.bg_section_blue,
            R.color.bg_section_green,
            R.color.bg_section_purple,
            R.color.bg_section_orange
        )
        holder.cv.setCardBackgroundColor(
            androidx.core.content.ContextCompat.getColor(
                holder.itemView.context, colors[position % colors.size]
            )
        )

        // إظهار علامة "مكتمل" إذا أتمّ الطفل الحرف
        val lessonId = "letter_${l.language}_${position}"
        val isDone = ProgressManager.getInstance(holder.itemView.context).isLessonCompleted(lessonId)
        holder.completed.visibility = if (isDone) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener { onClick(l, position) }
    }

    override fun getItemCount() = letters.size
}
