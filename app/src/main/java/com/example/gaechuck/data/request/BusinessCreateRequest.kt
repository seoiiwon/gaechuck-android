package com.example.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class BusinessCreateRequest(
    @SerializedName("coalitionName") val coalitionName: String,
    @SerializedName("benefit") val benefit: String,
    @SerializedName("category") val category: String,
)