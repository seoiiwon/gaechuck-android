package com.gaechuck_package.gaechuck.data.response

data class PatchNoticeResponse(
    val id: Int,
    val title: String,
    val body: String,
    val images: List<String>,
    val time: String
)
