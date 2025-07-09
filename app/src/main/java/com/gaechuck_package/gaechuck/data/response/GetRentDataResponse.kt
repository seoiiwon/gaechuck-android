package com.gaechuck_package.gaechuck.data.response

data class GetRentDataResponse(
        val content: List<RentList>,
        val first: Boolean,
        val last: Boolean
    )

data class RentList(
    val rentItemCount: Int,
    val rentItemId: Int,
    val image: String,
    val rentItemName: String
)