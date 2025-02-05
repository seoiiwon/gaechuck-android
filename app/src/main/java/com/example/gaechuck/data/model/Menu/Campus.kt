package com.example.gaechuck.data.model.Menu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Campus(
    val campus: String,
    val date: String,
    val cafeteria: Map<String, Cafeteria>
) : Parcelable
