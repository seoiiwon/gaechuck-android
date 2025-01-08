package com.example.gaechuck.ui.business.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gaechuck.R
import com.example.gaechuck.data.model.BusinessItem

class BusinessViewModel : ViewModel(){
    // 비즈니스 데이터를 관리할 LiveData
    private val _businessList = MutableLiveData<List<BusinessItem>>()
    val businessList: LiveData<List<BusinessItem>> get() = _businessList

    // 데이터 초기화
    init {
        loadBusinessData()
    }
    private fun loadBusinessData(){
        val dummyData = listOf(
            BusinessItem(
                name = "다드림 경상국립대학교점",
                category = "음식점",
                summary = "학생증 제시 시 음료수 1개 or 김가루 밥 증정",
                info = "학생증 제시 시\n음료수 1개 or 김가루 밥 증정",
                images = listOf(R.drawable.bs_item_1_main, R.drawable.bs_item_1_sub)
            ),
            BusinessItem(
                name = "79대포",
                category = "주점",
                summary = "학생증 제시 시 테이블당 떡꼬치 1개 제공",
                info = "학생증 제시 시 \n테이블당떡꼬치 1개 제공",
                images = listOf(R.drawable.bs_item_2_main, R.drawable.bs_item_2_sub)
            ),
            BusinessItem(
                name = "월하주",
                category = "주점",
                summary = "학생증 제시 시 혜택 제공",
                info = "1. 19시 이전 방문 시 테이블 당 음료수 1개\n2. 50,000원 이상 결제 시 서비스 안주 제공(금,토 제외)",
                images = listOf(R.drawable.bs_item_1_main, R.drawable.bs_item_1_sub)
            ),
            BusinessItem(
                name = "코어짐",
                category = "헬스&뷰티",
                summary = "1. 개월 수 상관없이 10% 할인가로 제공\n2.라커룸 무료로 제공",
                info = "학생증 제시 시 10% 할인",
                images = listOf(R.drawable.bs_item_2_main, R.drawable.bs_item_2_sub)
            ),
            BusinessItem(
                name = "컴포즈커피",
                category = "카페",
                summary = "1. 음료류 10,000원 이상 결제 시 2000원 할인쿠폰 제공\n" +
                        "2. 음료류 15,000원 이상 결제 시 3000원 할인쿠폰 제공\n"+
                        "3. 음료류 50,000원 이상 결제 시 10,000원 할인쿠폰 제공",
                info = "학생증 제시 시 할인쿠폰 제공",
                images = listOf(R.drawable.bs_item_1_main, R.drawable.bs_item_1_sub)
            ),
            BusinessItem(
                name = "알톤자전거 경상대점",
                category = "기타",
                summary = "자전거 가격을 인터넷 최저가로 제공\n" +
                        "자물쇠, 물통케이스 야간 안전등 무료 제공\n" +
                        "자전거 구입 후 3년간 무료 A/S 제공",
                info = "학생증 제시 시 혜택 제공",
                images = listOf(R.drawable.bs_item_2_main, R.drawable.bs_item_2_sub)
            ),
            BusinessItem(
                name = "동아반점",
                category = "음식점",
                summary = "4인 이상 방문 시 만두 또는 음료수 1개 무료 제공",
                info = "학생증 제시 시 혜택 제공",
                images = listOf(R.drawable.bs_item_1_main, R.drawable.bs_item_1_sub)
            ),
            BusinessItem(
                name = "동아반점",
                category = "음식점",
                summary = "4인 이상 방문 시 만두 또는 음료수 1개 무료 제공",
                info = "학생증 제시 시 혜택 제공",
                images = listOf(R.drawable.bs_item_1_main, R.drawable.bs_item_1_sub)
        )
        )
        _businessList.value = dummyData
    }

}