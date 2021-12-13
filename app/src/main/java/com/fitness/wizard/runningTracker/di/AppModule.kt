package com.fitness.wizard.runningTracker.di

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.room.Room
import com.fitness.wizard.core.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.fitness.wizard.core.util.Constants.KEY_NAME
import com.fitness.wizard.core.util.Constants.KEY_WEIGHT
import com.fitness.wizard.core.util.Constants.RUNNING_DATABASE_NAME
import com.fitness.wizard.core.util.Constants.USER_SHARED_PREFERENCE
import com.fitness.wizard.runningTracker.data.RunningDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db:RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideUsersSharedPreferences(
        @ApplicationContext app: Context
    ) = app.getSharedPreferences(USER_SHARED_PREFERENCE, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideUserName(sharedPreferences: SharedPreferences) = sharedPreferences.getString(KEY_NAME, "")?: ""

    @Singleton
    @Provides
    fun provideUserWeight(sharedPreferences: SharedPreferences) = sharedPreferences.getFloat(KEY_WEIGHT, 60f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

    @Singleton
    @Provides
    fun provideApplicationInfo(
        @ApplicationContext app:Context
    ) = app.packageManager.getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
}