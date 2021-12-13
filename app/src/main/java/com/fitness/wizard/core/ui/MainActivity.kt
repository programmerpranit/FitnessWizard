package com.fitness.wizard.core.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.fitness.wizard.R
import com.fitness.wizard.core.ReminderNotificationManager
import com.fitness.wizard.core.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myAlarm()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT){
            findNavController(R.id.navFragment).navigate(R.id.action_mainFragment_to_trackingFragment)
        }
    }


    fun myAlarm(){
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, 19)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)

        if (calender.time < Date()){
            calender.add(Calendar.DAY_OF_MONTH, 1)
        }

        val intent = Intent(applicationContext, ReminderNotificationManager::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP, calender.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

    }

}