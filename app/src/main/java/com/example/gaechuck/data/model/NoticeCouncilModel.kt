package com.example.gaechuck.data.model

import android.os.Parcel
import android.os.Parcelable

data class NoticeCouncilModel(
    val title: String,
    val body: String,
    val date: String,
    val image: String? // 이미지가 없는 경우 null
) : Parcelable {

    // Parcelable을 생성하는 데 필요한 생성자
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", // title
        parcel.readString() ?: "", // body
        parcel.readString() ?: "", // date
        parcel.readString() // image
    )

    // 객체의 데이터를 Parcel에 기록하는 메서드
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeString(date)
        parcel.writeString(image)
    }

    // describeContents()는 보통 0을 반환
    override fun describeContents(): Int {
        return 0
    }

    // CREATOR는 Parcelable.Creator를 통해 Parcelable 객체를 생성하는 역할
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<NoticeCouncilModel> = object : Parcelable.Creator<NoticeCouncilModel> {
            override fun createFromParcel(parcel: Parcel): NoticeCouncilModel {
                return NoticeCouncilModel(parcel)
            }

            override fun newArray(size: Int): Array<NoticeCouncilModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}