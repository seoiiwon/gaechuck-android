package com.example.gaechuck.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class CafeteriaMenuActivity : AppCompatActivity() {
    private val restaurants = listOf("교직원식당", "중앙식당", "기숙사식당")
    private var currentRestaurantIndex = 0
    private lateinit var jsonData: JSONArray  // JSONArray로 변경
//    private lateinit var jsonData: JSONObject  // JSONArray로 변경


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafeteria_menu)

        // 뒤로가기
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        // 홈으로 이동하기
        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // JSON 데이터 로드
        jsonData = loadJSONFromAsset()


        // UI 요소 초기화
        val restaurantTextView = findViewById<TextView>(R.id.restaurantTextView)
        val previousButton = findViewById<View>(R.id.previousButton)
        val nextButton = findViewById<View>(R.id.nextButton)
        val dayButtons = listOf(
            findViewById<Button>(R.id.day1),
            findViewById<Button>(R.id.day2),
            findViewById<Button>(R.id.day3),
            findViewById<Button>(R.id.day4),
            findViewById<Button>(R.id.day5),
            findViewById<Button>(R.id.day6),
            findViewById<Button>(R.id.day7)
        )
        val menuContainer = findViewById<LinearLayout>(R.id.menuContainer)

        // 레스토랑 초기화
        restaurantTextView.text = restaurants[currentRestaurantIndex]
        updateMenu(menuContainer, restaurants[currentRestaurantIndex], "월")

        // 요일 버튼 클릭 이벤트
        dayButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                dayButtons.forEach { it.setBackgroundResource(R.drawable.default_background) }
                button.setBackgroundResource(R.drawable.selected_background)
                val selectedDay = button.text.toString()
                updateMenu(menuContainer, restaurants[currentRestaurantIndex], selectedDay)
            }
        }

        // 이전 버튼 클릭
        previousButton.setOnClickListener {
            currentRestaurantIndex = (currentRestaurantIndex - 1 + restaurants.size) % restaurants.size
            restaurantTextView.text = restaurants[currentRestaurantIndex]
            updateMenu(menuContainer, restaurants[currentRestaurantIndex], "월")
        }

        // 다음 버튼 클릭
        nextButton.setOnClickListener {
            currentRestaurantIndex = (currentRestaurantIndex + 1) % restaurants.size
            restaurantTextView.text = restaurants[currentRestaurantIndex]
            updateMenu(menuContainer, restaurants[currentRestaurantIndex], "월")
        }
    }


    // JSON 데이터 로드
    private fun loadJSONFromAsset(): JSONArray {
        val inputStream: InputStream = assets.open("meal_data.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return JSONArray(jsonString)  // JSONArray로 변경
    }


    // 메뉴 업데이트 부분에서 각 카테고리 데이터를 처리할 때 null 체크를 강화합니다.
    private fun updateMenu(container: LinearLayout, restaurant: String, day: String) {
        container.removeAllViews()

        // "campus"는 객체임으로 getJSONObject로 처리
        val campusData = jsonData.getJSONObject(0)

        val campus = campusData.optString("campus", null)

        if (campus != null) {
            val cafeteriaData = campusData.optJSONObject("cafeteria")
            if (cafeteriaData != null) {
            } else {
                // cafeteria 데이터가 없는 경우
            }
        } else {
            // campus가 null인 경우
        }

        val cafeteriaData = campusData.getJSONObject("cafeteria")
            .getJSONObject(restaurant)
            .optJSONObject(day)

        if (cafeteriaData != null) {
            val inflater = LayoutInflater.from(this)

            // 각 식사 메뉴 추가
            listOf("한식", "베이커리", "죽식", "테이크아웃").forEach { category ->
                val categoryData = cafeteriaData.optJSONArray(category)

                if (categoryData != null && categoryData.length() > 0) {
                    // 카테고리 제목 뷰 생성
                    val categoryTitle = inflater.inflate(R.layout.fragment_menu_category, container, false) as TextView
                    categoryTitle.text = category
                    container.addView(categoryTitle)

                    for (i in 0 until categoryData.length()) {
                        // 메뉴 항목 뷰 생성
                        val menuItem = categoryData.optString(i, null)  // null 값 처리를 위해 optString 사용
                        if (menuItem != null) {
                            val menuText = inflater.inflate(R.layout.fragment_menu_item, container, false) as TextView
                            menuText.text = menuItem
                            container.addView(menuText)
                        }
                    }
                } else {
                    // 메뉴가 없으면 "메뉴 정보가 없습니다." 표시
                    val noDataText = TextView(this).apply {
                        text = "$category 메뉴 정보가 없습니다."
                        textSize = 16f
                        setTextColor(ContextCompat.getColor(this@CafeteriaMenuActivity, android.R.color.holo_red_dark))
                        setPadding(16, 16, 16, 16)
                    }
                    container.addView(noDataText)
                }
            }
        } else {
            val noDataText = TextView(this).apply {
                text = "메뉴 정보가 없습니다."
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@CafeteriaMenuActivity, android.R.color.holo_red_dark))
                setPadding(16, 16, 16, 16)
            }
            container.addView(noDataText)
        }
    }


    // cafeteria 데이터를 찾아서 반환하는 함수
    private fun getCafeteriaData(campusData: JSONArray, restaurant: String, day: String): JSONObject? {
        for (i in 0 until campusData.length()) {
            val campus = campusData.getJSONObject(i)
            val cafeterias = campus.optJSONObject("cafeteria")
            cafeterias?.let {
                val restaurantData = it.optJSONObject(restaurant)
                return restaurantData?.optJSONObject(day)
            }
        }
        return null
    }

}
