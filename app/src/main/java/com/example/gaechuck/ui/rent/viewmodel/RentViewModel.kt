package com.example.gaechuck.ui.rent.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gaechuck.R
import com.example.gaechuck.data.model.RentItem

class RentViewModel: ViewModel() {
    // 대여물품 데이터를 관리할 LiveData
    private val _rentlist = MutableLiveData<List<RentItem>>()
    val rentList: LiveData<List<RentItem>> get() = _rentlist

    // 데이터 초기화
    init {
        loadRentData()
    }

    private fun loadRentData(){
        val dummyData = listOf(
            RentItem(
                id = 1,
                name = "축구공",
                count = 4,
                images = listOf(R.drawable.rt_item_main_1, R.drawable.rt_item_main_1),
                info = "사용 후 학생회실로 반납부탁드립니다."
            ),
            RentItem(
                id = 2,
                name = "야구배트",
                count = 29,
                images = listOf(R.drawable.rt_item_main_2, R.drawable.rt_item_main_2),
                info = "사용 후 학생회실로 반납부탁드립니다."
            ),
            RentItem(
                id = 3,
                name = "농구공",
                count = 2,
                images = listOf(R.drawable.rt_item_main_3, R.drawable.rt_item_main_3),
                info = "사용 후 학생회실로 반납부탁드립니다."
            ),
            RentItem(
                id = 4,
                name = "L카드",
                count = 3,
                images = listOf(R.drawable.rt_item_main_4, R.drawable.rt_item_main_4),
                info = "사용 후 학생회실로 반납부탁드립니다."
            ),
            RentItem(
                id = 5,
                name = "자전거",
                count = 30,
                images = listOf(R.drawable.rt_item_main_5, R.drawable.rt_item_main_5),
                info = "사용 후 학생회실로 반납부탁드립니다."
            ),
            RentItem(
                id = 6,
                name = "칠판",
                count = 5,
                images = listOf(R.drawable.rt_item_main_6, R.drawable.rt_item_main_6),
                info = "사용 후 학생회실로 반납부탁드립니다."
            ),
            RentItem(
                id = 7,
                name = "천막",
                count = 10,
                images = listOf(R.drawable.rt_item_main_7, R.drawable.rt_item_main_7),
                info = "사용 후 학생회실로 반납부탁드립니다."
            )
        )
        _rentlist.value = dummyData
    }
}