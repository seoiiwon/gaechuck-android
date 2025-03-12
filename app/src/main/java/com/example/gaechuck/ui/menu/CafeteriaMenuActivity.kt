package com.example.gaechuck.ui.menu
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.lifecycle.Observer
import com.example.gaechuck.R
import com.example.gaechuck.data.response.FoodMenuItem
import com.example.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModdel
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CafeteriaMenuActivity : AppCompatActivity() {

    private lateinit var campusSpinner: Spinner
    private lateinit var restaurantLayout: LinearLayout
    private lateinit var leftArrow: ImageView
    private lateinit var rightArrow: ImageView
    private lateinit var selectedCafeteriaSeq: List<Int>
    private var currentIndex = 0
    private val viewModel: CafeteriaMenuViewModdel by viewModels()


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafeteria_menu)

        val oneWeekPeriodTextView = findViewById<TextView>(R.id.oneWeekPeriod)
        oneWeekPeriodTextView.text = getCurrentWeekRange()

        val buttonMonday: Button = findViewById(R.id.buttonMonday)
        val buttonTuesday: Button = findViewById(R.id.buttonTuesday)
        val buttonWednesday: Button = findViewById(R.id.buttonWednesday)
        val buttonThursday: Button = findViewById(R.id.buttonThursday)
        val buttonFriday: Button = findViewById(R.id.buttonFriday)
        val buttonSaturday: Button = findViewById(R.id.buttonSaturday)
        val buttonSunday: Button = findViewById(R.id.buttonSunday)

        val dayButtons = mapOf(
            "월요일" to buttonMonday,
            "화요일" to buttonTuesday,
            "수요일" to buttonWednesday,
            "목요일" to buttonThursday,
            "금요일" to buttonFriday,
            "토요일" to buttonSaturday,
            "일요일" to buttonSunday
        )

        dayButtons.forEach { (day, button) ->
            button.setOnClickListener {
                filterMenuByDay(day)
            }
        }

        viewModel.menuList.observe(this, Observer { menuList ->
            updateMenuUI(menuList) // 초기 전체 메뉴 표시
        })

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener { /* 홈 이동 구현 */ }

        campusSpinner = findViewById(R.id.campusSpinner)
        restaurantLayout = findViewById(R.id.restaurantLayout)
        leftArrow = findViewById(R.id.leftArrow)
        rightArrow = findViewById(R.id.rightArrow)

        val campusList = campusMap.keys.toList()

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, campusList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getView(position, convertView, parent) as TextView
                textView.textSize = 14f
                textView.setTypeface(null, android.graphics.Typeface.BOLD) // ✅ Bold 적용
                textView.gravity = android.view.Gravity.CENTER // ✅ 중앙 정렬 적용
                return textView
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getDropDownView(position, convertView, parent) as TextView
                textView.textSize = 14f
                textView.setTypeface(null, android.graphics.Typeface.NORMAL) // ✅ Bold 적용 가능
                textView.gravity = android.view.Gravity.CENTER // ✅ 중앙 정렬 적용

                // ✅ 부모의 너비를 match_parent로 설정하여 중앙 정렬 적용
                textView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // 너비를 MATCH_PARENT로 설정
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                return textView
            }
        }

        campusSpinner.adapter = adapter
        campusSpinner.adapter = adapter

        campusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedCafeteriaSeq = cafeteriaSeqMap[campusList[position]] ?: emptyList()
                currentIndex = 0

                updateRestaurantDisplay(campusMap[campusList[position]] ?: emptyList())
                viewModel.fetchFoodMenu(selectedCafeteriaSeq[currentIndex])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        leftArrow.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateRestaurantDisplay(campusMap[campusSpinner.selectedItem] ?: emptyList())
                viewModel.fetchFoodMenu(selectedCafeteriaSeq[currentIndex])
            }
        }

        rightArrow.setOnClickListener {
            if (currentIndex < selectedCafeteriaSeq.size - 1) {
                currentIndex++
                updateRestaurantDisplay(campusMap[campusSpinner.selectedItem] ?: emptyList())
                viewModel.fetchFoodMenu(selectedCafeteriaSeq[currentIndex])
            }
        }

        viewModel.menuList.observe(this, Observer { menuList ->
            updateMenuUI(menuList)
        })
    }



    private fun filterMenuByDay(selectedDay: String) {
        val filteredMenu = viewModel.menuList.value?.filter { menuItem ->
            val dayOfWeek = getDayOfWeek(menuItem.date)
            dayOfWeek == selectedDay
        } ?: emptyList()

        updateMenuUI(filteredMenu)
    }

    private fun getDayOfWeek(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val calendar = Calendar.getInstance().apply { time = date!! }

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일요일"
            Calendar.MONDAY -> "월요일"
            Calendar.TUESDAY -> "화요일"
            Calendar.WEDNESDAY -> "수요일"
            Calendar.THURSDAY -> "목요일"
            Calendar.FRIDAY -> "금요일"
            Calendar.SATURDAY -> "토요일"
            else -> ""
        }
    }

    private fun updateMenuUI(menuList: List<FoodMenuItem>) {
        val menuItemLayout = findViewById<LinearLayout>(R.id.menuItemLayout)
        menuItemLayout.removeAllViews()

        menuList.forEach { menu ->
            val textView = TextView(this).apply {
                text = menu.menu
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            menuItemLayout.addView(textView)
        }
    }

    private fun updateRestaurantDisplay(restaurantList: List<String>) {
        restaurantLayout.removeAllViews()
        val textView = TextView(this)
        textView.text = restaurantList.getOrElse(currentIndex) { "식당 정보 없음" }
        textView.setPadding(16, 16, 16, 16)
        textView.textSize = 14f
        textView.gravity = android.view.Gravity.CENTER
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        restaurantLayout.addView(textView)
    }

    private fun getCurrentWeekRange(): String {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday = calendar.time

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val sunday = calendar.time

        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

        return "${dateFormat.format(monday)} ~ ${dateFormat.format(sunday)}"
    }
}
