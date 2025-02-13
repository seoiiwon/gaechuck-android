package com.example.gaechuck.ui.menu

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R

class CafeteriaMenuActivity : AppCompatActivity() {

    private lateinit var campusSpinner: Spinner
    private lateinit var restaurantLayout: LinearLayout
    private lateinit var leftArrow: ImageView
    private lateinit var rightArrow: ImageView

    private val campusMap = mapOf(
        "가좌캠퍼스" to listOf("가좌 교직원식당", "가좌 중앙1식당", "가좌 교육문화1층식당"),
        "칠암캠퍼스" to listOf("칠암 교직원식당", "칠암 학생식당"),
        "통영캠퍼스" to listOf("통영 교직원식당", "통영 학생식당")
    )

    private val cafeteriaSeqMap = mapOf(
        "가좌캠퍼스" to listOf(1, 2, 3),
        "칠암캠퍼스" to listOf(4, 5),
        "통영캠퍼스" to listOf(6, 7)
    )

    private lateinit var selectedCafeteriaSeq: List<Int>
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafeteria_menu)

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            // 홈 이동 구현
        }

        campusSpinner = findViewById(R.id.campusSpinner)
        val campusList = campusMap.keys.toList()

        val campusAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, campusList) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.gravity = android.view.Gravity.CENTER
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.gravity = android.view.Gravity.CENTER
                return view
            }
        }
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        campusSpinner.adapter = campusAdapter

        restaurantLayout = findViewById(R.id.restaurantLayout)
        leftArrow = findViewById(R.id.leftArrow)
        rightArrow = findViewById(R.id.rightArrow)

        campusSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCampus = campusList[position]
                val restaurantList = campusMap[selectedCampus] ?: emptyList()
                selectedCafeteriaSeq = cafeteriaSeqMap[selectedCampus] ?: emptyList()

                if (selectedCafeteriaSeq.isNotEmpty()) {
                    currentIndex = 0
                    updateRestaurantDisplay(restaurantList)
                    updateMenuItems()
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })

        leftArrow.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateRestaurantDisplay(campusMap[campusSpinner.selectedItem] ?: emptyList())
                updateMenuItems()
            }
        }

        rightArrow.setOnClickListener {
            if (currentIndex < selectedCafeteriaSeq.size - 1) {
                currentIndex++
                updateRestaurantDisplay(campusMap[campusSpinner.selectedItem] ?: emptyList())
                updateMenuItems()
            }
        }

        val menuItemLayout = findViewById<LinearLayout>(R.id.menuItemLayout)

        if (savedInstanceState == null) {
            val fragment = MenuItemFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.menuItemLayout, fragment)
                .commit()
        }
    }

    private fun updateRestaurantDisplay(restaurantList: List<String>) {
        restaurantLayout.removeAllViews()
        val textView = TextView(this@CafeteriaMenuActivity)
        textView.text = restaurantList.getOrElse(currentIndex) { "식당 정보 없음" }
        textView.setPadding(16, 16, 16, 16)
        textView.setTextSize(16f)
        textView.tag = selectedCafeteriaSeq.getOrElse(currentIndex) { 0 }
        textView.gravity = android.view.Gravity.CENTER
        restaurantLayout.addView(textView)
    }

    private fun updateMenuItems() {
        val fragment = MenuItemFragment()
        val args = Bundle()
        args.putInt("cafeteriaSeq", selectedCafeteriaSeq[currentIndex]) // 현재 선택된 식당의 seq 전달
        fragment.arguments = args

        Log.d("CafeteriaMenuActivity", "Updating menu with cafeteriaSeq: ${selectedCafeteriaSeq[currentIndex]}")

        supportFragmentManager.beginTransaction()
            .replace(R.id.menuItemLayout, fragment) // 기존 Fragment를 새로운 Fragment로 교체
            .commit()
    }

}
