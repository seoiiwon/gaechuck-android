package com.example.gaechuck.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoseItem(
    val name: String,
    val location: String,
    val date: String,
    val info: String,
    val images: List<Int> // 이미지 배열 (대표 이미지, 지도 이미지)
): Parcelable