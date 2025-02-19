package com.example.gaechuck.data.response

data class GetAllNoticeDataResponse(
        val bbsId: String,
        val id: Int,
        val title: String,
        val body: String,
        val representationImages: String,
        val time: String,
        val departmentName: String?
)