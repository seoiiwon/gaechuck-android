package com.gaechuck_package.gaechuck.data.response
import androidx.annotation.Keep

@Keep
class PostRentDeleteResponse (
    val rentItemCount: Int,
    val rentItemId: Int,
    val rentItemImage: String?,
    val rentItemName: String
    )