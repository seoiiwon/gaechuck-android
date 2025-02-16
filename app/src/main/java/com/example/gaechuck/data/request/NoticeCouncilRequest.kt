package com.example.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class NoticeCouncilRequest(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)
