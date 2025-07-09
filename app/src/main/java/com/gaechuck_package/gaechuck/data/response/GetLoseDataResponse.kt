package com.gaechuck_package.gaechuck.data.response

data class GetLoseDataResponse(
        val content: List<LoseList>,
        val first: Boolean,
        val last: Boolean,
        val totalPages: Int
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