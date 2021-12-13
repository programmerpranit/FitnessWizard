package com.fitness.wizard.runningTracker.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RunTable")
data class Run(
    var img: Bitmap? = null,
    var timeStamp:Long = 0L,
    var avgSpeedInKM:Float = 0F,
    var distanceInMeter:Int = 0,
    var timeInMil:Long = 0L,
    var caloriesBurned:Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

