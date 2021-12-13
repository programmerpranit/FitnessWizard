package com.fitness.wizard.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.fitness.wizard.R
import com.fitness.wizard.core.ui.MainActivity
import com.fitness.wizard.core.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ReminderNotificationManager: BroadcastReceiver() {

    lateinit var notification:Notification

    override fun onReceive(context: Context, intent: Intent) {

        val sp = context.getSharedPreferences(Constants.USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        val lastDay = sp.getLong(Constants.KEY_LAST_DAY,0L)
        val today = Calendar.getInstance().timeInMillis


        val pintent = Intent(context, MainActivity::class.java)
        pintent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        pintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(context,2,pintent,0)


            if (lastDay == TimeUnit.MILLISECONDS.toDays(today)){
                notification = NotificationCompat.Builder(context, Constants.REMINDER_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_run_tracker)
                    .setContentTitle("Hey, Checkout Some New Exercises")
                    .setContentText("Click to enter in app")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build()
            }else{
                notification= NotificationCompat.Builder(context, Constants.REMINDER_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_run_tracker)
                    .setContentTitle("Hey, Don't forget to complete your running goal")
                    .setContentText("Click to enter in app")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build()

            }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                Constants.REMINDER_NOTIFICATION_CHANNEL_ID,
                Constants.REMINDER_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager = getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val manager:NotificationManagerCompat = NotificationManagerCompat.from(context)
        manager.notify(Constants.REMINDER_NOTIFICATION_ID, notification)

    }
}