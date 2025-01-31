package com.example.gaechuck.data.response

data class GetAllNoticeDataResponse(
        val bbsId: String,
        val categoryName: String,
        val dataId: String,
        val departmentName: String,
        val notiNum: String,
        val notiSeq: Int,
        val regiDate: String,
        val title: String,
        val url: String
    )