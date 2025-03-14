package com.example.gaechuck.data.response

data class GetAllNoticeDataResponse(
        val notiSeq: Int,
        val notiNum: String,
        val title: String,
        val regiDate: String,
        val categoryName: String?,
        val departmentName: String?,
        val url: String,
        val bbsId: String,
        val dataId: String
)