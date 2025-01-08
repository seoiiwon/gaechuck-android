package com.example.gaechuck.data.models

data class SettingItem(
    val title: String,
    val description: String,
    var isNotificationEnabled: Boolean = false, // 기본값 false
    val hasToggle: Boolean = false // Toggle을 표시할지 여부
)