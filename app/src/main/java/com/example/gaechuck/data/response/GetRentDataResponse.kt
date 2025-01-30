package com.example.gaechuck.data.response

data class GetRentDataResponse(
        val content: List<RentList>,
        val first: Boolean,
        val last: Boolean
    )

data class RentList(
    val rentItemCount: Int,
    val rentItemId: Int,
    val rentItemImage: String,
    val rentItemName: String
)