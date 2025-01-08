package com.example.gaechuck.bus.viewmodel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class BusRoute : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub4)

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

        // Spinner 초기화
        val spinner = findViewById<Spinner>(R.id.categorySpinner)
        val categories = listOf("캠퍼스긴 셔틀버스(오전)", "캠퍼스간 셔틀버스(오후)", "진주역 셔틀버스", "통학노선버스(시외)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // JSON 데이터 로드
        val jsonString = loadJSONFromAsset()
        val jsonArray = JSONArray(jsonString)

        // Spinner 선택 이벤트 처리
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // 선택된 JSON 데이터 가져오기
                val selectedCategory = categories[position]
                val selectedRoute = jsonArray.find { it.getString("type") == selectedCategory }

                // 버스 노선 데이터 갱신
                if (selectedRoute != null) {
                    val busRouteContainer = findViewById<LinearLayout>(R.id.busRouteContainer)
                    busRouteContainer.removeAllViews() // 기존 항목 제거
                    val serviceTime = selectedRoute.getJSONObject("serviceTime")
                    for (key in serviceTime.keys()) {
                        val departures = serviceTime.getJSONArray(key)
                        addBusRoute(busRouteContainer, key, departures)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun addBusRoute(container: LinearLayout, title: String, departures: JSONArray) {
        // "n회 출발" 제목 추가
        val titleTextView = TextView(this)
        titleTextView.text = "$title 출발"
        titleTextView.textSize = 14f
        titleTextView.setTextColor(resources.getColor(android.R.color.holo_blue_dark))
        titleTextView.setPadding(0, 16, 0, 8)
        container.addView(titleTextView)

        // ScrollView 및 GridLayout 생성
        val scrollView = HorizontalScrollView(this)
        val gridLayout = GridLayout(this)
        gridLayout.setPadding(8, 8, 8, 8)

        // 동적으로 열의 개수를 데이터 수에 맞게 설정
        gridLayout.columnCount = departures.length()

        // 첫 번째 행 (정류장 정보)
        for (i in 0 until departures.length()) {
            val station = departures.getJSONArray(i).getString(0)
            val stationTextView = createTextViewFromXml(station)
            gridLayout.addView(stationTextView)
        }

        // 두 번째 행 (시간 정보)
        for (i in 0 until departures.length()) {
            val time = departures.getJSONArray(i).optString(1, "-")
            val timeTextView = createTextViewFromXml(time)
            gridLayout.addView(timeTextView)
        }

        // ScrollView에 GridLayout 추가 후 컨테이너에 추가
        scrollView.addView(gridLayout)
        container.addView(scrollView)

        scrollView.isHorizontalScrollBarEnabled = false
        scrollView.isVerticalScrollBarEnabled = false
    }


    private fun createTextViewFromXml(text: String?): TextView {
        // XML로부터 TextView 인플레이트
        val textView = LayoutInflater.from(this).inflate(R.layout.fragment_bus_route_item, null) as TextView
        textView.text = text ?: "-" // null 값일 경우 "-" 표시
        val params = GridLayout.LayoutParams()
        params.setMargins(16, 16, 16, 16)
        textView.layoutParams = params
        return textView
    }



    private fun loadJSONFromAsset(): String {
        val inputStream: InputStream = assets.open("suttle_bus_route.json")
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun JSONArray.find(predicate: (JSONObject) -> Boolean): JSONObject? {
        for (i in 0 until this.length()) {
            val obj = this.getJSONObject(i)
            if (predicate(obj)) {
                return obj
            }
        }
        return null
    }
}
