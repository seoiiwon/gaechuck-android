package com.gaechuck_package.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class LosePatchRequest (
    @SerializedName("lostItemId") val lostItemId: Int,
    @SerializedName("title")val title: String,
    @SerializedName("lostDate") val lostDate: String,
    @SerializedName("description") val description : String,
    @SerializedName("lostLocation") val lostLocation : String,
)