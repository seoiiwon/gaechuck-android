package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class PatchNoticeResponse(
    val id: Int,
    val title: String,
    val body: String,
    val images: List<String>,
    val time: String
)
