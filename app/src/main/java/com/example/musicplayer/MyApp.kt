package com.example.musicplayer

import android.app.Application
import com.example.musicplayer.di.vmModule
import com.iamsdt.androidextension.DebugLogTree
import com.rohitss.uceh.UCEHandler
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            UCEHandler.Builder(applicationContext).build()
            Timber.plant(DebugLogTree())
        }

        startKoin {
            // Android context
            androidContext(this@MyApp)
            modules(vmModule)
        }
    }

}