package com.gaechuck_package.gaechuck.data.model

data class ClubScheduleItem(
    val label: String,
    val dateRange: String,
    val isCurrent: Boolean = false
)
