package com.example.gaechuck.data.response

data class GetBusinessDataResponse(
        val content: List<BusinessList>,
        val first: Boolean,
        val last: Boolean
)

data class BusinessList(
            val benefit: String,
            val category: String,
            val coalitionId: Int,
            val coalitionName: String,
            val image: String,
            val images: Any
)