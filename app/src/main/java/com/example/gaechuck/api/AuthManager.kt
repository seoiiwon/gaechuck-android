package com.example.gaechuck.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        Log.d("AuthManager", "새 토큰 저장 시작: AccessToken = ${accessToken.take(10)}..., RefreshToken = ${refreshToken.take(10)}...")
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        val token = prefs.getString(KEY_REFRESH_TOKEN, null)
        Log.d("AuthManager", "RefreshToken 조회: ${if (token.isNullOrEmpty()) "없음" else "있음"}")
        return token
    }

    fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }



}