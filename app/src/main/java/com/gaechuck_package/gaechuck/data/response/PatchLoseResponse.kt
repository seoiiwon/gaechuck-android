package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class PatchLoseResponse (
    val description: String,
    val image: String?,
    val images: List<String>,
    val isResolved: String,
    val lostDate: String,
    val lostItemId: Int,
    val title: String
)