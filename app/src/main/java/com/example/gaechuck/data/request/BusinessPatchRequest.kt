package com.example.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class BusinessPatchRequest(
    @SerializedName("coalitionId") val coalitionId: Int,
    @SerializedName("category")val category: String,
    @SerializedName("coalitionName") val coalitionName: String,
    @SerializedName("benefit") val benefit: String,
)