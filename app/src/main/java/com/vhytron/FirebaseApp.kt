package com.vhytron

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vhytron.database.appModule
import kotlinx.coroutines.DelicateCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FirebaseApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.database.setPersistenceEnabled(true)

        startKoin {
            androidContext(this@FirebaseApp)
            modules(listOf(appModule))
        }
    }
}