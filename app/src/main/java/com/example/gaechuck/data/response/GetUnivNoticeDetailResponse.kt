package com.example.gaechuck.data.response

data class GetUnivNoticeDetailResponse(
        val body: String,
        val images: List<String>,
        val time: String,
        val title: String
)