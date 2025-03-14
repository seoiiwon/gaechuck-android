package com.example.gaechuck.data.request

import com.google.gson.annotations.SerializedName

data class RentPatchRequest (
    @SerializedName("rentItemId") val rentItemId: Int,
    @SerializedName("rentItemCount")val rentItemCount: Int,
    @SerializedName("rentItemName") val rentItemName: String
)