package com.example.gaechuck.data.response

data class GetLoseDataResponse(
        val content: List<LoseList>,
        val first: Boolean,
        val last: Boolean
    )

data class LoseList(
    val description: String,
    val image: String,
    val images: Any,
    val isResolved: String,
    val lostDate: String,
    val lostItemId: Int,
    val title: String
)