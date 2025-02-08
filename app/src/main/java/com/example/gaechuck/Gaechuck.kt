package com.example.gaechuck

import android.app.Application
import com.example.gaechuck.api.AuthManager

class Gaechuck : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthManager.init(this)
    }
}