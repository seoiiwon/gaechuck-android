package com.example.gaechuck.ui.lose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gaechuck.R
import com.example.gaechuck.data.model.LoseItem

class LoseViewModel:ViewModel() {

    // 분실물 데이터를 관리할 LiveData
    private val _loselist = MutableLiveData<List<LoseItem>>()
    val loseList: LiveData<List<LoseItem>> get() = _loselist

    // 데이터 초기화
    init {
        loadLoseData()
    }
    private fun loadLoseData(){
        val dummyData = listOf(
            LoseItem(
                name = "나이키 신발",
                location = "경영대학교 201호",
                date = "2024-11-11",
                info = "흰색과 검정색이 섞인 나이키 신발. 신발 밑 창에 파란색 얼룩이 있음.",
                images = listOf(R.drawable.rt_item_main_1, R.drawable.rt_item_main_1)
            ),
            LoseItem(
                name = "교통카드",
                location = "교육문화센터",
                date = "2024-11-12",
                info = "어쩌구저쩌구",
                images = listOf(R.drawable.rt_item_main_2, R.drawable.rt_item_main_2)
            ),
            LoseItem(
                name = "지갑",
                location = "사범대학교 302동",
                date = "2024-11-14",
                info = "어쩌구저쩌구",
                images = listOf(R.drawable.rt_item_main_3, R.drawable.rt_item_main_3)
            ),
            LoseItem(
                name = "고양이 키링",
                location = "중앙도서관 1층",
                date = "2024-11-15",
                info = "귀여운 고양이",
                images = listOf(R.drawable.rt_item_main_4, R.drawable.rt_item_main_4)
            ),
            LoseItem(
                name = "나이키 신발",
                location = "경영대학교 201호",
                date = "2024-11-11",
                info = "흰색과 검정색이 섞인 나이키 신발. 신발 밑 창에 파란색 얼룩이 있음.",
                images = listOf(R.drawable.rt_item_main_1, R.drawable.rt_item_main_1)
            ),
            LoseItem(
                name = "교통카드",
                location = "교육문화센터",
                date = "2024-11-12",
                info = "어쩌구저쩌구",
                images = listOf(R.drawable.rt_item_main_2, R.drawable.rt_item_main_2)
            ),
            LoseItem(
                name = "지갑",
                location = "사범대학교 302동",
                date = "2024-11-14",
                info = "어쩌구저쩌구",
                images = listOf(R.drawable.rt_item_main_3, R.drawable.rt_item_main_3)
            ),
            LoseItem(
                name = "고양이 키링",
                location = "중앙도서관 1층",
                date = "2024-11-15",
                info = "귀여운 고양이",
                images = listOf(R.drawable.rt_item_main_4, R.drawable.rt_item_main_4)
            ),
            LoseItem(
                name = "나이키 신발",
                location = "경영대학교 201호",
                date = "2024-11-11",
                info = "흰색과 검정색이 섞인 나이키 신발. 신발 밑 창에 파란색 얼룩이 있음.",
                images = listOf(R.drawable.rt_item_main_1, R.drawable.rt_item_main_1)
            ),
            LoseItem(
                name = "교통카드",
                location = "교육문화센터",
                date = "2024-11-12",
                info = "어쩌구저쩌구",
                images = listOf(R.drawable.rt_item_main_2, R.drawable.rt_item_main_2)
            ),
            LoseItem(
                name = "지갑",
                location = "사범대학교 302동",
                date = "2024-11-14",
                info = "어쩌구저쩌구",
                images = listOf(R.drawable.rt_item_main_3, R.drawable.rt_item_main_3)
            ),
            LoseItem(
                name = "고양이 키링",
                location = "중앙도서관 1층",
                date = "2024-11-15",
                info = "귀여운 고양이",
                images = listOf(R.drawable.rt_item_main_4, R.drawable.rt_item_main_4)
            ),

        )
        _loselist.value = dummyData
    }
}