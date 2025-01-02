package com.example.gaechuck.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RentItem (
    val name : String, // 물품 이름
    val count : Int, // 잔여갯수
    val images : List<Int>, //이미지배열 (대표,기본)
    val info : String,
) : Parcelable