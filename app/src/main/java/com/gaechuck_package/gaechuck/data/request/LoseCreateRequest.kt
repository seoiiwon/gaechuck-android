package com.gaechuck_package.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class LoseCreateRequest (
    @SerializedName("title") val title: String,
    @SerializedName("lostDate") val lostDate: String,
    @SerializedName("description") val description: String,
    @SerializedName("lostLocation") val lostLocation: String,
)