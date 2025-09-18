package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class GetRentDetailResponse(
        val images: List<String>,
        val rentItemId: Int,
        val rentItemName: String,
        val rentItemCount: Int
    )