package com.gaechuck_package.gaechuck

import android.app.Application
import com.gaechuck_package.gaechuck.api.ApiConnection
import com.gaechuck_package.gaechuck.api.AuthManager

class Gaechuck : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthManager.init(this)
        ApiConnection.create(this)
    }
}