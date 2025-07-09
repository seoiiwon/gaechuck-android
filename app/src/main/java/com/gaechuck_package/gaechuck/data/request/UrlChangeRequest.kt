package com.gaechuck_package.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class UrlChangeRequest(
    @SerializedName("chatName") val chatName: String,
    @SerializedName("chatUrl")val chatUrl: String,
)