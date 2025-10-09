package com.example.fitmind

import android.app.Application
import com.google.firebase.FirebaseApp

class FitMindApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // OPT: Inicialización segura de Firebase
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase ya inicializado o error, continuar
        }
    }
}
