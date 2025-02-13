package com.example.gaechuck.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GetFoodDataResponse(
        val menu: String
)

@Parcelize
data class FoodMenuItem(
        val menu: String,
        val menuDivision: String,
        val date: String,
        val menuSeq: Int
) : Parcelable