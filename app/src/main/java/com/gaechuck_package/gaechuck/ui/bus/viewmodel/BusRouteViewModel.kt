package com.gaechuck_package.gaechuck.ui.bus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class BusTimeEntry(
    val departureTime: String,
    val arrivalTime: String,
    val isFridayCancelled: Boolean = false
)

data class CampusRoute(
    val fromCampus: String,
    val toCampus: String,
    val boardingLocation: String,
    val amSchedule: List<BusTimeEntry>,
    val pmSchedule: List<BusTimeEntry>,
    val operationNote: String = ""
)

class BusRouteViewModel : ViewModel() {

    private val _route = MutableLiveData<CampusRoute>()
    val route: LiveData<CampusRoute> = _route

    private val _isAm = MutableLiveData(true)
    val isAm: LiveData<Boolean> = _isAm

    private var isGajwaFirst = true

    private val gajwaToChilam = CampusRoute(
        fromCampus = "가좌캠퍼스",
        toCampus = "칠암캠퍼스",
        boardingLocation = "교양학관 앞 승차",
        amSchedule = listOf(
            BusTimeEntry("08:20", "08:45"),
            BusTimeEntry("09:00", "09:25"),
            BusTimeEntry("09:30", "09:55"),
            BusTimeEntry("09:50", "10:15"),
            BusTimeEntry("10:00", "10:25", isFridayCancelled = true),
            BusTimeEntry("10:30", "10:55"),
            BusTimeEntry("10:40", "11:05"),
            BusTimeEntry("11:00", "11:25")
        ),
        pmSchedule = listOf(
            BusTimeEntry("13:00", "13:25"),
            BusTimeEntry("14:00", "14:25"),
            BusTimeEntry("15:00", "15:25"),
            BusTimeEntry("16:00", "16:25"),
            BusTimeEntry("17:00", "17:25")
        ),
        operationNote = "1. 운행기간: 2026. 4. 3 ~"
    )

    private val chilamToGajwa = CampusRoute(
        fromCampus = "칠암캠퍼스",
        toCampus = "가좌캠퍼스",
        boardingLocation = "교양학관 앞 승차",
        amSchedule = listOf(
            BusTimeEntry("08:05", "08:30"),
            BusTimeEntry("08:10", "08:35"),
            BusTimeEntry("08:15", "08:40"),
            BusTimeEntry("08:20", "08:45"),
            BusTimeEntry("08:25", "08:50"),
            BusTimeEntry("08:30", "08:55", isFridayCancelled = true),
            BusTimeEntry("08:40", "09:05"),
            BusTimeEntry("09:30", "09:55", isFridayCancelled = true),
            BusTimeEntry("10:00", "10:25"),
            BusTimeEntry("10:20", "10:45")
        ),
        pmSchedule = listOf(
            BusTimeEntry("13:00", "13:25"),
            BusTimeEntry("14:00", "14:25"),
            BusTimeEntry("15:00", "15:25"),
            BusTimeEntry("16:00", "16:25"),
            BusTimeEntry("17:00", "17:25"),
            BusTimeEntry("17:30", "17:55")
        ),
        operationNote = "1. 운행기간: 2026. 4. 3 ~"
    )

    init {
        _route.value = gajwaToChilam
    }

    fun swap() {
        isGajwaFirst = !isGajwaFirst
        _route.value = if (isGajwaFirst) gajwaToChilam else chilamToGajwa
    }

    fun setAm() { _isAm.value = true }
    fun setPm() { _isAm.value = false }

    fun getCurrentSchedule(): List<BusTimeEntry> {
        val r = _route.value ?: return emptyList()
        return if (_isAm.value == true) r.amSchedule else r.pmSchedule
    }
}
