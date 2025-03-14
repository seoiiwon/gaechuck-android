package com.example.gaechuck.data.response

data class PatchLoseResponse (
    val description: String,
    val image: Any,
    val images: List<String>,
    val isResolved: String,
    val lostDate: String,
    val lostItemId: Int,
    val title: String
)