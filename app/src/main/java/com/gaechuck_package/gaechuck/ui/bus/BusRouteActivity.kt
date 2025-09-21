package com.gaechuck_package.gaechuck.ui.bus

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.ui.bus.viewmodel.BusRouteViewModel
import com.gaechuck_package.gaechuck.ui.bus.viewmodel.BusStop

class BusRouteActivity : AppCompatActivity() {

    private val busRouteViewModel: BusRouteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_route)

        // 뒤로가기
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        // Spinner 초기화
        val spinner = findViewById<Spinner>(R.id.categorySpinner)
        val categories = listOf("캠퍼스(오전)", "캠퍼스(오후)", "진주역", "시외")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Spinner 선택 이벤트 처리
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                val selectedRoute = busRouteViewModel.getBusRouteByType(selectedCategory)

                val routeTitleTextView = findViewById<TextView>(R.id.routeTitleTextView)
                selectedRoute?.let {
                    routeTitleTextView.text = "캠퍼스간 셔틀버스(${it.type_detail})"
                }

                val serviceAreaTextView = findViewById<TextView>(R.id.serviceAreaTextView)
                selectedRoute?.let {
                    serviceAreaTextView.text = it.serviceArea?.let { area -> "운행구간 : $area" } ?: ""
                }

                // UI 업데이트
                if (selectedRoute != null) {
                    val busRouteContainer = findViewById<LinearLayout>(R.id.busRouteContainer)
                    busRouteContainer.removeAllViews()

                    selectedRoute.serviceTime.forEach { (key, departures) ->
                        addBusRoute(busRouteContainer, key, departures)
                    }
                }

                val scrollView = findViewById<ScrollView>(R.id.scrollView)
                scrollView.post {
                    scrollView.scrollTo(0, 0)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun addBusRoute(container: LinearLayout, title: String, departures: List<BusStop>) {
        val titleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val titleTextView = TextView(this).apply {
            text = "$title 출발"
            textSize = 10f
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.gnu_blue))
            setPadding(0, 16, 0, 8)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val swipeLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val swipeTextView = TextView(this).apply {
            text = "스와이프"
            textSize = 8f
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.lables_secondary))
        }

        val swipeImageView = ImageView(this).apply {
            setImageResource(R.drawable.swipe) // swipe.png 사용
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(8), // 너비
                dpToPx(8)  // 높이
            ).apply {
                setMargins(dpToPx(2), 0, 0, 0)
            }
        }

        swipeLayout.addView(swipeTextView)
        swipeLayout.addView(swipeImageView)

        titleLayout.addView(titleTextView)
        titleLayout.addView(swipeLayout)
        container.addView(titleLayout)


        val scrollView = HorizontalScrollView(this)
        val gridLayout = GridLayout(this)
        gridLayout.setPadding(8, 8, 8, 8)
        gridLayout.columnCount = departures.size

        // 정류장 정보
        departures.forEach { busStop ->
            val stationTextView = createTextView(busStop.name).apply {
                textSize = 10f
            }
            val layoutParams = GridLayout.LayoutParams().apply {
                setGravity(Gravity.CENTER)
            }
            stationTextView.layoutParams = layoutParams
            gridLayout.addView(stationTextView)
        }

        // 시간 정보
        departures.forEach { busStop ->
            val timeTextView = createTextView(busStop.time ?: "-")
            val layoutParams = GridLayout.LayoutParams().apply {
                setGravity(Gravity.CENTER)
            }
            timeTextView.layoutParams = layoutParams
            gridLayout.addView(timeTextView)
        }

        scrollView.addView(gridLayout)
        container.addView(scrollView)

        scrollView.isHorizontalScrollBarEnabled = false
        scrollView.isVerticalScrollBarEnabled = false

        // 스크롤이 필요한 경우에만 "스와이프" 표시
        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (scrollView.getChildAt(0).width > scrollView.width) {
                    swipeLayout.visibility = View.VISIBLE
                } else {
                    swipeLayout.visibility = View.GONE
                }
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }


    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }


    private fun createTextView(text: String): TextView {
        val textView = LayoutInflater.from(this).inflate(R.layout.fragment_bus_route_item, null) as TextView
        textView.text = text
        val params = GridLayout.LayoutParams()
        params.setMargins(16, 16, 16, 16)
        textView.layoutParams = params
        return textView
    }
}
