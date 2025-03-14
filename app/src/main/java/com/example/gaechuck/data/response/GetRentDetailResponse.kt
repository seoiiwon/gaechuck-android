package com.example.gaechuck.data.response

data class GetRentDetailResponse(
        val images: List<String>,
        val rentItemId: Int,
        val rentItemName: String,
        val rentItemCount: Int
    )