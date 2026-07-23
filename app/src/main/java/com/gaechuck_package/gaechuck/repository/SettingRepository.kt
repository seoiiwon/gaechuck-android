package com.gaechuck_package.gaechuck.repository

import android.content.Context
import android.content.SharedPreferences

class SettingRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isEnabled(key: String, default: Boolean = false): Boolean =
        prefs.getBoolean(key, default)

    fun setEnabled(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun enabledQuickInfoCount(): Int = QUICK_INFO_KEYS.count { isEnabled(it) }

    companion object {
        private const val PREFS_NAME = "settings_prefs"

        const val KEY_COUNCIL_NOTICE = "council_notice"
        const val KEY_UNIV_NOTICE = "univ_notice"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_GAJWA_STAFF = "gajwa_staff_cafeteria"
        const val KEY_GAJWA_CULTURE = "gajwa_culture_cafeteria"
        const val KEY_GAJWA_CENTRAL = "gajwa_central_cafeteria"
        const val KEY_CHIRAM_STAFF = "chiram_staff_cafeteria"
        const val KEY_CHIRAM_CENTRAL = "chiram_central_cafeteria"

        val QUICK_INFO_KEYS = listOf(
            KEY_GAJWA_STAFF,
            KEY_GAJWA_CULTURE,
            KEY_GAJWA_CENTRAL,
            KEY_CHIRAM_STAFF,
            KEY_CHIRAM_CENTRAL
        )

        const val QUICK_INFO_MAX = 2
    }
}
