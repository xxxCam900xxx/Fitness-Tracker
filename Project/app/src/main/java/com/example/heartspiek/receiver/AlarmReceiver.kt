package com.example.heartspiek.receiver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.annotation.RequiresApi
import com.example.heartspiek.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private var ringtone: Ringtone? = null
        private var vibrator: Vibrator? = null

        fun stopAlarm(context: Context) {
            ringtone?.stop()
            vibrator?.cancel()
            val manager = NotificationManagerCompat.from(context)
            manager.cancel(1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val sharedPref = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("alarm_active", true)
                apply()
            }

            showNotification(context)
            startVibration(context)
            playAlarmSound(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context) {
        val openIntent = Intent(context, AlarmActivity::class.java)
        openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, SnoozeReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, 1, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Alarm!")
            .setContentText("Tippe zum Stoppen oder Snooze!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(openPendingIntent)
            .addAction(android.R.drawable.ic_delete, "Snooze", snoozePendingIntent)
            .build()

        val manager = NotificationManagerCompat.from(context)
        manager.notify(1, notification)
    }

    private fun startVibration(context: Context) {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 1000, 500)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun playAlarmSound(context: Context) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone?.isLooping = true
        ringtone?.play()
    }
}
