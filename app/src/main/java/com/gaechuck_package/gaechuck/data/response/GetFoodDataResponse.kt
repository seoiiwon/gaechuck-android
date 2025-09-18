package com.gaechuck_package.gaechuck.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.annotation.Keep

@Keep
data class GetFoodDataResponse(
        val menu: String,
        val menuDivision: String,
        val date: String,
        val menuSeq: Int
)

@Keep
@Parcelize
data class FoodMenuItem(
        val menu: String,
        val menuDivision: String,
        val date: String,
        val menuSeq: Int
) : Parcelable