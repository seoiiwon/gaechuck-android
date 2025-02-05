package com.example.gaechuck.data.model.Menu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItem(
    val name: String,
    val details: List<String>
) : Parcelable