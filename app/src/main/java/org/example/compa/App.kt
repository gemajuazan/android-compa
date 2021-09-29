package org.example.compa

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import org.example.compa.preferences.AppPreference

class App : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var preferences: AppPreference
        private var instance: Application? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@App)
        AppPreference.sharedPreferences = myPreferences

        preferences = AppPreference()
    }
}