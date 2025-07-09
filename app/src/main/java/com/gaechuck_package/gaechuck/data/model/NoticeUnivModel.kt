package com.gaechuck_package.gaechuck.data.model

data class NoticeUnivModel(
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
