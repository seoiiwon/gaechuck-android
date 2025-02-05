package com.example.gaechuck.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoticeUnivModel(
    val id: Int,
    val title: String,
    val body: String,
    val representationImages: String,
    val time: String
) : Parcelable


//data class NoticeUnivModel(
//    val title: String,
//    val category: String,
//    val date: String,
//    val department: String,
//    val body: String
//) : Parcelable {
//
//    constructor(parcel: Parcel) : this(
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readString() ?: ""
//    )
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(title)
//        parcel.writeString(category)
//        parcel.writeString(date)
//        parcel.writeString(department)
//        parcel.writeString(body)
//    }
//
//    override fun describeContents(): Int = 0
//
//    companion object {
//        @JvmField
//        val CREATOR: Parcelable.Creator<NoticeUnivModel> = object : Parcelable.Creator<NoticeUnivModel> {
//            override fun createFromParcel(parcel: Parcel): NoticeUnivModel {
//                return NoticeUnivModel(parcel)
//            }
//
//            override fun newArray(size: Int): Array<NoticeUnivModel?> {
//                return arrayOfNulls(size)
//            }
//        }
//    }
//}
