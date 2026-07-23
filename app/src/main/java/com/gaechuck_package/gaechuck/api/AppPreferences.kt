package com.gaechuck_package.gaechuck.api

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_ONBOARDING_SHOWN = "onboarding_shown"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isOnboardingShown(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_SHOWN, false)
    }

    fun setOnboardingShown() {
        prefs.edit().putBoolean(KEY_ONBOARDING_SHOWN, true).apply()
    }

    fun resetOnboarding() {
        prefs.edit().remove(KEY_ONBOARDING_SHOWN).apply()
    }
}
