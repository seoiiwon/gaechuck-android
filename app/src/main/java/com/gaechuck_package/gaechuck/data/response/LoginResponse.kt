package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class LoginResponse(
        val accessToken: String,
        val grantType: String,
        val refreshToken: String,
        val refreshTokenExpirationTime: Int
    )