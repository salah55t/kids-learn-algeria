package com.salah.kidslearn.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salah.kidslearn.R
import com.salah.kidslearn.data.Badge
import com.salah.kidslearn.data.ContentProvider
import com.salah.kidslearn.utils.ProgressManager

/**
 * نشاط الإنجازات - يعرض نقاط XP، نجوم، سلسلة، ودروس مكتملة
 * + شبكة الشارات (مفتوحة/مغلقة)
 */
class AchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        val pm = ProgressManager.getInstance(this)
        findViewById<TextView>(R.id.tv_xp_value).text = pm.getXp().toString()
        findViewById<TextView>(R.id.tv_stars_value).text = pm.getStars().toString()
        findViewById<TextView>(R.id.tv_streak_value).text = "${pm.getStreak()} يوم"
        findViewById<TextView>(R.id.tv_lessons_value).text = pm.getCompletedLessons().size.toString()

        val rv = findViewById<RecyclerView>(R.id.rv_badges)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = BadgesAdapter(ContentProvider.badges, pm.getUnlockedBadges())
    }
}

class BadgesAdapter(
    private val badges: List<Badge>,
    private val unlocked: Set<String>
) : RecyclerView.Adapter<BadgesAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_badge)
        val title: TextView = view.findViewById(R.id.tv_badge_title)
        val desc: TextView = view.findViewById(R.id.tv_badge_desc)
        val lockOverlay: View = view.findViewById(R.id.lock_overlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val b = badges[position]
        holder.title.text = b.title
        holder.desc.text = b.description

        val resId = holder.itemView.context.resources.getIdentifier(
            b.drawable, "drawable", holder.itemView.context.packageName
        )
        if (resId != 0) holder.icon.setImageResource(resId)

        val isUnlocked = unlocked.contains(b.id)
        holder.lockOverlay.visibility = if (isUnlocked) View.GONE else View.VISIBLE
        holder.icon.alpha = if (isUnlocked) 1.0f else 0.3f
    }

    override fun getItemCount() = badges.size
}
