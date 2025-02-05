package com.example.gaechuck.ui.menu.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gaechuck.data.model.Menu.Cafeteria
import com.example.gaechuck.data.model.Menu.Campus
import com.example.gaechuck.data.model.Menu.DayMenu
import com.example.gaechuck.data.model.Menu.MenuItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class CafeteriaMenuViewModel : ViewModel() {

    private val _cafeteriaData = MutableLiveData<List<Campus>>()
    val cafeteriaData: LiveData<List<Campus>> = _cafeteriaData

    // JSON 파일 로드 및 LiveData 업데이트
    fun loadCafeteriaData(context: Context) {
        val jsonData = loadJSONFromAsset(context)
        val campusList = parseJsonData(jsonData)
        _cafeteriaData.value = campusList
    }

    // JSON을 파싱하여 Campus 객체로 변환
    private fun parseJsonData(jsonData: String): List<Campus> {
        val jsonArray = JSONArray(jsonData)
        val campusList = mutableListOf<Campus>()

        for (i in 0 until jsonArray.length()) {
            val campusJson = jsonArray.getJSONObject(i)
            val campusName = campusJson.getString("campus")
            val date = campusJson.getString("date")
            val cafeteriaJson = campusJson.getJSONObject("cafeteria")

            val cafeteriaMap = mutableMapOf<String, Cafeteria>()

            // 각 식당의 데이터를 파싱
            cafeteriaJson.keys().forEach { restaurantName ->
                val dayMenuJson = cafeteriaJson.getJSONObject(restaurantName)

                val 월 = parseDayMenu(dayMenuJson.getJSONObject("월"))
                val 화 = parseDayMenu(dayMenuJson.getJSONObject("화"))
                val 수 = parseDayMenu(dayMenuJson.getJSONObject("수"))
                val 목 = parseDayMenu(dayMenuJson.getJSONObject("목"))
                val 금 = parseDayMenu(dayMenuJson.getJSONObject("금"))

                val cafeteria = Cafeteria(restaurantName, 월, 화, 수, 목, 금)
                cafeteriaMap[restaurantName] = cafeteria
            }

            campusList.add(Campus(campusName, date, cafeteriaMap))
        }
        return campusList
    }

    private fun parseDayMenu(dayMenuJson: JSONObject): DayMenu {
        val 한식 = parseMenuItems(dayMenuJson.getJSONArray("한식"))
        val 베이커리 = parseMenuItems(dayMenuJson.getJSONArray("베이커리"))
        val 죽식 = parseMenuItems(dayMenuJson.getJSONArray("죽식"))
        val 테이크아웃 = parseMenuItems(dayMenuJson.getJSONArray("테이크아웃"))

        return DayMenu(한식, 베이커리, 죽식, 테이크아웃)
    }

    // MenuItem을 List<MenuItem> 형태로 전달하도록 개선
    private fun parseMenuItems(menuJsonArray: JSONArray): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()
        for (i in 0 until menuJsonArray.length()) {
            val menuDetails = menuJsonArray.getString(i).split("\n")
            val menuName = menuDetails.first() // 첫 번째 항목을 이름으로 취급
            menuItems.add(MenuItem(menuName, menuDetails)) // 각 메뉴 항목을 MenuItem 객체로 변환하여 리스트에 추가
        }
        return menuItems
    }


    // JSON 파일을 읽어오는 메소드 (assets 폴더에서 파일 읽기)
    private fun loadJSONFromAsset(context: Context): String {
        val inputStream: InputStream = context.assets.open("meal_data.json")
        return inputStream.bufferedReader().use { it.readText() }
    }
}
