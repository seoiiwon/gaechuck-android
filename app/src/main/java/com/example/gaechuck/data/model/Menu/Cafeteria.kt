package com.example.gaechuck.data.model.Menu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cafeteria(
    val name: String,
    val 월: DayMenu,
    val 화: DayMenu,
    val 수: DayMenu,
    val 목: DayMenu,
    val 금: DayMenu,
) : Parcelable
