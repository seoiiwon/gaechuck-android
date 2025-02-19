package com.example.gaechuck.data.model

data class NoticeUnivModel(
    val id: Int,
    val title: String,
    val body: String?,
    val representationImages: String?,
    val time: String?,
    val departmentName: String?,
    val bbsId: String?
)
