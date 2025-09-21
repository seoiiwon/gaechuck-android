package com.gaechuck_package.gaechuck

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.gaechuck_package.gaechuck.api.ApiConnection
import com.gaechuck_package.gaechuck.api.AuthManager

class Gaechuck : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AuthManager.init(this)
        ApiConnection.create(this)
    }
}