package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
data class PostUrlResponse(
    val chatId: Int,
    val chatName: String,
    val chatUrl: String
)