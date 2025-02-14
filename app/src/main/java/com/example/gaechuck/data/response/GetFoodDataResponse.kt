package com.example.gaechuck.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GetFoodDataResponse(
        val menu: String,        // ✅ 식단 메뉴 (현재 String)
        val menuDivision: String, // ✅ 메뉴 구분 (주식, 국류, 찬류 등)
        val date: String,        // ✅ 제공 날짜
        val menuSeq: Int         // ✅ 메뉴 고유 번호
)


@Parcelize
data class FoodMenuItem(
        val menu: String,
        val menuDivision: String,
        val date: String,
        val menuSeq: Int
) : Parcelable