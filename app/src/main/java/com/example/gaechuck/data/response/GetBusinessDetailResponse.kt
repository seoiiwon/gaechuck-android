package com.example.gaechuck.data.response

data class GetBusinessDetailResponse(
        val benefit: String,
        val category: String,
        val coalitionId: Int,
        val coalitionName: String,
        val image: Any,
        val images: List<String>
    )