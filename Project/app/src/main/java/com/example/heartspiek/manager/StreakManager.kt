package com.example.heartspiek.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.heartspiek.R
import java.text.SimpleDateFormat
import java.util.*

class StreakManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("streak_prefs", Context.MODE_PRIVATE)

    fun updateStreak(): Int {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val lastOpenDate = sharedPreferences.getString("last_open_date", "")
        var streak = sharedPreferences.getInt("streak_count", -1)

        if (streak == -1) {
            streak = 1
        } else if (lastOpenDate == today) {
            return streak
        } else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = dateFormat.format(calendar.time)

            streak = if (lastOpenDate == yesterday) {
                streak + 1
            } else {
                1
            }
        }

        sharedPreferences.edit()
            .putString("last_open_date", today)
            .putInt("streak_count", streak)
            .apply()

        return streak
    }

    fun getStreak(): Int {
        return sharedPreferences.getInt("streak_count", 0)
    }

}
