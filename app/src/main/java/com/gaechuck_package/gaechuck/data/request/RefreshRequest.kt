package com.gaechuck_package.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class RefreshRequest (
    @SerializedName("accessToken") val accessToken : String,
    @SerializedName("refreshToken") val refreshToken : String,
)