package com.salah.kidslearn.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * مدير التقدم - يحفظ نقاط XP، النجوم، الملصقات، السلسلة اليومية، والشارات
 * يستخدم SharedPreferences للتخزين المحلي (لا يحتاج صلاحيات إنترنت)
 *
 * نظام التحفيز على غرار Duolingo:
 * - نقاط XP: تتراكم مع كل درس مكتمل
 * - نجوم: نجمة لكل درس مكتمل
 * - سلسلة يومية (Streak): عدد الأيام المتتالية
 * - شارات: تُفتح عند الوصول لعدد معين من XP
 * - ملصقات تشجيعية: تظهر بعد كل 3 دروس متتالية
 */
class ProgressManager private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ================== النقاط (XP) ==================

    fun addXp(amount: Int) {
        val current = getXp()
        prefs.edit().putInt(KEY_XP, current + amount).apply()
        checkBadgesUnlock(current + amount)
    }

    fun getXp(): Int = prefs.getInt(KEY_XP, 0)

    // ================== النجوم ==================

    fun addStar() {
        val current = getStars()
        prefs.edit().putInt(KEY_STARS, current + 1).apply()
    }

    fun getStars(): Int = prefs.getInt(KEY_STARS, 0)

    // ================== السلسلة اليومية (Streak) ==================

    fun updateStreak(): Int {
        val today = todayStr()
        val lastDate = prefs.getString(KEY_LAST_DATE, null)

        if (lastDate == null) {
            // أول استخدام على الإطلاق
            prefs.edit()
                .putInt(KEY_STREAK, 1)
                .putString(KEY_LAST_DATE, today)
                .apply()
            return 1
        }

        if (lastDate == today) {
            // استخدام في نفس اليوم - لا تغيير
            return getStreak()
        }

        // حساب الفرق بالأيام
        val diff = daysBetween(lastDate, today)
        val newStreak = if (diff == 1L) {
            getStreak() + 1  // يوم متتالي
        } else {
            1  // انقطعت السلسلة، نبدأ من جديد
        }

        prefs.edit()
            .putInt(KEY_STREAK, newStreak)
            .putString(KEY_LAST_DATE, today)
            .apply()
        return newStreak
    }

    fun getStreak(): Int = prefs.getInt(KEY_STREAK, 0)

    // ================== الدروس المكتملة ==================

    fun markLessonCompleted(lessonId: String) {
        val completed = getCompletedLessons().toMutableSet()
        if (completed.add(lessonId)) {
            prefs.edit().putStringSet(KEY_COMPLETED_LESSONS, completed).apply()
        }
    }

    fun isLessonCompleted(lessonId: String): Boolean =
        getCompletedLessons().contains(lessonId)

    fun getCompletedLessons(): Set<String> =
        prefs.getStringSet(KEY_COMPLETED_LESSONS, emptySet()) ?: emptySet()

    // ================== الملصقات التشجيعية ==================

    fun checkStickerUnlock(): String? {
        val completedCount = getCompletedLessons().size
        val nextStickerMilestone = prefs.getInt(KEY_NEXT_STICKER, 3)
        if (completedCount >= nextStickerMilestone) {
            prefs.edit().putInt(KEY_NEXT_STICKER, nextStickerMilestone + 3).apply()
            return when (nextStickerMilestone) {
                3 -> "ic_sticker_bear"
                6 -> "ic_sticker_rabbit"
                9 -> "ic_sticker_owl"
                12 -> "ic_sticker_fox"
                15 -> "ic_sticker_lion"
                else -> "ic_sticker_trophy"
            }
        }
        return null
    }

    // ================== الشارات (Badges) ==================

    fun getUnlockedBadges(): Set<String> {
        return prefs.getStringSet(KEY_BADGES, emptySet()) ?: emptySet()
    }

    private fun checkBadgesUnlock(newXp: Int) {
        val unlocked = getUnlockedBadges().toMutableSet()
        val toUnlock = mutableListOf<String>()

        if (newXp >= 10 && unlocked.add("first_letter")) toUnlock.add("first_letter")
        if (newXp >= 50 && unlocked.add("five_letters")) toUnlock.add("five_letters")
        if (newXp >= 260 && unlocked.add("alphabet_master")) toUnlock.add("alphabet_master")
        if (newXp >= 280 && unlocked.add("number_rookie")) toUnlock.add("number_rookie")
        if (newXp >= 350 && unlocked.add("color_blind")) toUnlock.add("color_blind")
        if (newXp >= 420 && unlocked.add("zoo_keeper")) toUnlock.add("zoo_keeper")
        if (newXp >= 100 && getStreak() >= 7 && unlocked.add("streak_7")) toUnlock.add("streak_7")
        if (newXp >= 500 && getStreak() >= 30 && unlocked.add("streak_30")) toUnlock.add("streak_30")

        if (toUnlock.isNotEmpty()) {
            prefs.edit().putStringSet(KEY_BADGES, unlocked).apply()
            // يمكن إضافة منطق لإظهار notification هنا
        }
    }

    // ================== دوال مساعدة ==================

    private fun todayStr(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    private fun daysBetween(oldDate: String, newDate: String): Long {
        return try {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val old = fmt.parse(oldDate) ?: return 1L
            val new = fmt.parse(newDate) ?: return 1L
            val diff = new.time - old.time
            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            1L
        }
    }

    // ================== إعادة تعيين (للاختبار) ==================

    fun reset() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "kids_learn_prefs"
        private const val KEY_XP = "xp"
        private const val KEY_STARS = "stars"
        private const val KEY_STREAK = "streak"
        private const val KEY_LAST_DATE = "last_date"
        private const val KEY_COMPLETED_LESSONS = "completed_lessons"
        private const val KEY_BADGES = "badges"
        private const val KEY_NEXT_STICKER = "next_sticker"

        @Volatile private var instance: ProgressManager? = null
        fun getInstance(context: Context): ProgressManager {
            return instance ?: synchronized(this) {
                instance ?: ProgressManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
