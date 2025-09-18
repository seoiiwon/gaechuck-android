package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class GetLoseDataResponse(
        val content: List<LoseList>,
        val first: Boolean,
        val last: Boolean,
        val totalPages: Int
    )
@Keep
data class LoseList(
    val description: String,
    val image: String,
    val images: String?,
    val isResolved: String,
    val lostDate: String,
    val lostItemId: Int,
    val title: String
)