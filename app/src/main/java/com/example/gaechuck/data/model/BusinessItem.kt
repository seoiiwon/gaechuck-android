package com.example.gaechuck.data.model

data class BusinessItem(
    val name: String,
    val category: String,
    val summary: String,
    val info: String,
    val images: List<Int> // 이미지 배열 (대표 이미지, 지도 이미지)
)