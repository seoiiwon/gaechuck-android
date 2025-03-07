package com.example.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class RentCreateRequest (
    @SerializedName("rentItemName") val rentItemName: String,
    @SerializedName("rentItemCount") val rentItemCount: Int,
)