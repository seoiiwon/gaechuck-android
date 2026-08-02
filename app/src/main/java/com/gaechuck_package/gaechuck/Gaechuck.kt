package com.gaechuck_package.gaechuck

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.gaechuck_package.gaechuck.api.ApiConnection
import com.gaechuck_package.gaechuck.api.AppPreferences
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.repository.SettingRepository

class Gaechuck : Application() {
    override fun onCreate() {
        super.onCreate()
        val isDarkModeEnabled = SettingRepository(this).isEnabled(SettingRepository.KEY_DARK_MODE)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        AuthManager.init(this)
        AppPreferences.init(this)
        ApiConnection.create(this)
    }
}