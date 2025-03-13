package com.example.gaechuck.ui.bus.viewmodel

import androidx.lifecycle.ViewModel

data class BusStop(
    val name: String,
    val time: String?
)

data class BusRoute(
    val type: String,
    val serviceArea: String?,
    val serviceTime: Map<String, List<BusStop>>
)

class BusRouteViewModel : ViewModel() {

    val busRoute = listOf(
        BusRoute(
            type =  "캠퍼스(오전)",
            serviceArea = "내동 - 가좌 - 칠암 - 가좌 - 내동",
            serviceTime = mapOf(
                "1회" to listOf(
                    BusStop("가좌캠퍼스\n(본부)", "08:10"),
                    BusStop("칠암캠퍼스\n(대학본부)", "08:30"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "08:45"),
                    BusStop("내동캠퍼스", "08:50\n(도착시간 유동적)")
                ),
                "2회" to listOf(
                    BusStop("가좌캠퍼스\n(본부)", "08:15"),
                    BusStop("칠암캠퍼스\n(대학본부)", "08:35"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "08:50"),
                    BusStop("내동캠퍼스", "08:55\n(도착시간 유동적)")
                ),
                "3회" to listOf(
                    BusStop("내동캠퍼스", "09:10"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "09:15"),
                    BusStop("칠암캠퍼스\n(대학본부)", "09:30"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "09:45"),
                    BusStop("내동캠퍼스", "09:50")
                ),
                "4회" to listOf(
                    BusStop("내동캠퍼스", "09:30"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "09:35"),
                    BusStop("칠암캠퍼스\n(대학본부)", "09:50"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "10:05"),
                    BusStop("내동캠퍼스", "10:10")
                ),
                "5회" to listOf(
                    BusStop("내동캠퍼스", "10:10"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "10:15"),
                    BusStop("칠암캠퍼스\n(대학본부)", "10:30"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "10:45"),
                    BusStop("내동캠퍼스", "10:50")
                ),
                "6회" to listOf(
                    BusStop("내동캠퍼스", "11:00"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "11:05"),
                    BusStop("칠암캠퍼스\n(대학본부)", "11:20"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "11:35"),
                    BusStop("내동캠퍼스", "11:45")
                ),
                "7회" to listOf(
                    BusStop("내동캠퍼스\n(출발)", "11:50"),
                    BusStop("가좌캠퍼스\n(도착) [학생회관]", "12:00")
                ),
                "8회" to listOf(
                    BusStop("가좌캠퍼스\n(학생회관)", "12:50"),
                    BusStop("내동캠퍼스", "13:00"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "13:05"),
                    BusStop("칠암캠퍼스\n(대학본부)", "13:20"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "13:35"),
                    BusStop("내동캠퍼스", "13:40")
                )
            )
        ),
        BusRoute(
            type = "캠퍼스(오후)",
            serviceArea = "칠암 - 가좌 - 내동 - 가좌 - 칠암",
            serviceTime = mapOf(
                "1회" to listOf(
                    BusStop("가좌캠퍼스", "13:20"),
                    BusStop("내동캠퍼스", null),
                    BusStop("가좌캠퍼스", null),
                    BusStop("칠암캠퍼스\n(대학본부)", "13:40"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "13:55"),
                    BusStop("내동캠퍼스", "14:00")
                ),
                "2회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "14:00"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "14:15"),
                    BusStop("내동캠퍼스", "14:20"),
                    BusStop("내동캠퍼스", "14:22"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "14:25"),
                    BusStop("칠암캠퍼스\n(대학본부)", "14:40")
                ),
                "3회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "14:20"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "14:35"),
                    BusStop("내동캠퍼스", "14:40"),
                    BusStop("내동캠퍼스", "14:42"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "14:45"),
                    BusStop("칠암캠퍼스\n(대학본부)", "15:00")
                ),
                "4회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "15:00"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "15:15"),
                    BusStop("내동캠퍼스", "15:20"),
                    BusStop("내동캠퍼스", "15:22"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "15:25"),
                    BusStop("칠암캠퍼스\n(대학본부)", "15:40")
                ),
                "5회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "15:20"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "15:35"),
                    BusStop("내동캠퍼스", "15:40"),
                    BusStop("내동캠퍼스", "15:42"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "15:45"),
                    BusStop("칠암캠퍼스\n(대학본부)", "16:00")
                ),
                "6회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "16:00"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "16:15"),
                    BusStop("내동캠퍼스", "16:20"),
                    BusStop("내동캠퍼스", "16:22"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "16:25"),
                    BusStop("칠암캠퍼스\n(대학본부)", "16:40")
                ),
                "7회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "16:20"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "16:35"),
                    BusStop("내동캠퍼스", "16:40"),
                    BusStop("내동캠퍼스", "16:42"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "16:45"),
                    BusStop("칠암캠퍼스\n(대학본부)", "17:00")
                ),
                "8회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "17:00"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "17:15"),
                    BusStop("내동캠퍼스", "17:20"),
                    BusStop("내동캠퍼스", "17:22"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "17:25"),
                    BusStop("칠암캠퍼스\n(대학본부)", "17:40")
                ),
                "9회" to listOf(
                    BusStop("칠암캠퍼스\n(대학본부)", "17:25"),
                    BusStop("가좌캠퍼스\n(본부, 학생회관, 수의대)", "17:40"),
                    BusStop("내동캠퍼스", "17:45"),
                    BusStop("내동캠퍼스", "17:47"),
                    BusStop("가좌캠퍼스\n(수의대, 학생회관, 본부)", "17:50"),
                    BusStop("칠암캠퍼스\n(대학본부)", "18:10")
                )
            )
        ),
        BusRoute(
            type = "진주역",
            serviceArea = "가좌캠퍼스 - 진주역 - 가좌캠퍼스 - 내동캠퍼스",
            serviceTime = mapOf(
                "1회" to listOf(
                    BusStop("가좌캠퍼스 출발", "08:30"),
                    BusStop("진주역", "08:30\n(기차 도착시간 유동적)"),
                    BusStop("가좌캠퍼스", "08:50"),
                    BusStop("내동캠퍼스", "도착시간 유동적")
                )
            )
        ),
        BusRoute(
            type = "시외",
            serviceArea = null,
            serviceTime = mapOf(
                "1회" to listOf(
                    BusStop("(창원) 대방중학교", "07:00"),
                    BusStop("사파동 시내정류장", "07:02"),
                    BusStop("법원", "07:04"),
                    BusStop("도청지하도", "07:06"),
                    BusStop("창원시청", "07:08"),
                    BusStop("트리비앙 APT", "07:10"),
                    BusStop("노블파크", "07:12"),
                    BusStop("더시티세븐", "07:13"),
                    BusStop("명서 트리비앙", "07:14"),
                    BusStop("명서초등학교", "07:16"),
                    BusStop("서부경찰서", "07:18"),
                    BusStop("도계주유소", "07:20"),
                    BusStop("39사단", "07:22"),
                    BusStop("임진각", "07:24"),
                    BusStop("창원역", "07:26"),
                    BusStop("한국전력 맞은 편", "07:28"),
                    BusStop("구암", "07:30"),
                    BusStop("마산역", "07:40"),
                    BusStop("마산우체국 앞", "07:42"),
                    BusStop("북성초등학교(마산 석전)", "07:45"),
                    BusStop("경상국립대학교(가좌)", "08:35"),
                    BusStop("경상국립대학교(칠암)", "08:55")
                ),
                "2회" to listOf(
                    BusStop("경상국립대학교(칠암)", "18:15")
                )
            )
        )
    )

    fun getBusRouteByType(type: String): BusRoute? {
        return busRoute.find { it.type == type }
    }
}