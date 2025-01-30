package com.example.gaechuck.data.response

data class GetLoseDetailResponse(
        val description: String,
        val images: List<String>,
        val isResolved: String,
        val lostDate: String,
        val lostItemId: Int,
        val title: String
    )