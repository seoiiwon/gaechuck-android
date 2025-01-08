package com.example.gaechuck.noticecouncil.viewmodel

data class Notice(
    val title: String,
    val body: String,
    val date: String,
    val image: String? // 이미지가 없는 경우 null
)