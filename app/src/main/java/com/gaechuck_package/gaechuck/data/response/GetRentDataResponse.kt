package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class GetRentDataResponse(
        val content: List<RentList>,
        val first: Boolean,
        val last: Boolean
    )
@Keep
data class RentList(
    val rentItemCount: Int,
    val rentItemId: Int,
    val image: String,
    val rentItemName: String,
    val rentCategory: String? = null
)