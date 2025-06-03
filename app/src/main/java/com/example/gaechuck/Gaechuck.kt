package com.example.gaechuck

import android.app.Application
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager

class Gaechuck : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthManager.init(this)
        ApiConnection.create(this)
    }
}