package com.example.heartspiek.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.*

class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        AlarmReceiver.stopAlarm(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5) // 5 Minuten später
        }

        val snoozeIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, newTime.timeInMillis, pendingIntent)
        Toast.makeText(context, "Snooze für 5 Minuten", Toast.LENGTH_LONG).show()
    }
}
