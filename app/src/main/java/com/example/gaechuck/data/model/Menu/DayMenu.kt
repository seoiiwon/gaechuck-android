package com.example.gaechuck.data.model.Menu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayMenu(
    val 한식: List<MenuItem>,
    val 베이커리: List<MenuItem>,
    val 죽식: List<MenuItem>,
    val 테이크아웃: List<MenuItem>
) : Parcelable
