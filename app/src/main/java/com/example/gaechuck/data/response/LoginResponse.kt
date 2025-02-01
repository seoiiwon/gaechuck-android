package com.example.gaechuck.data.response

data class LoginResponse(
        val accessToken: String,
        val grantType: String,
        val refreshToken: String,
        val refreshTokenExpirationTime: Int
    )