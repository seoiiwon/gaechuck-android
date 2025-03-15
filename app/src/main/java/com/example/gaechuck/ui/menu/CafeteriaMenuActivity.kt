package com.example.gaechuck.ui.menu
import DayButtonAdapter
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gaechuck.R
import com.example.gaechuck.data.response.FoodMenuItem
import com.example.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModdel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity

class CafeteriaMenuActivity : AppCompatActivity() {

    private lateinit var campusSpinner: Spinner
    private lateinit var restaurantLayout: LinearLayout
    private lateinit var leftArrow: ImageView
    private lateinit var rightArrow: ImageView
    private lateinit var selectedCafeteriaSeq: List<Int>
    private lateinit var dayRecyclerView: RecyclerView
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

        dayRecyclerView = findViewById(R.id.dayRecyclerView)
        setupDayRecyclerView()

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        campusSpinner = findViewById(R.id.campusSpinner)
        restaurantLayout = findViewById(R.id.restaurantLayout)
        leftArrow = findViewById(R.id.leftArrow)
        rightArrow = findViewById(R.id.rightArrow)

        setupCampusSpinner()

        viewModel.menuList.observe(this, Observer { menuList ->
            updateMenuUI(menuList)
        })

        val days = listOf("월", "화", "수", "목", "금", "토", "일")

        val today = getDayOfWeek(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time))

        dayRecyclerView = findViewById(R.id.dayRecyclerView)
        dayRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dayRecyclerView.adapter = DayButtonAdapter(this, days, today) { selectedDay ->
            filterMenuByDay(selectedDay)
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

    }

    private fun setupDayRecyclerView() {
        val days = listOf("월", "화", "수", "목", "금", "토", "일")
        val today = getDayOfWeek(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time))

        dayRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dayRecyclerView.adapter = DayButtonAdapter(this, days, today) { selectedDay ->
            filterMenuByDay(selectedDay)
        }

        // 항목 간 간격 추가 (선택 사항)
        dayRecyclerView.addItemDecoration(SpacingItemDecoration(8))
    }

    class SpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.right = space
        }
    }

    private fun setupCampusSpinner() {
        val campusList = campusMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, campusList)
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
    }


    private fun filterMenuByDay(selectedDay: String): Boolean {
        val filteredMenu = viewModel.menuList.value?.filter { menuItem ->
            val dayOfWeek = getDayOfWeek(menuItem.date)
            dayOfWeek == selectedDay
        } ?: emptyList()

        updateMenuUI(filteredMenu)

        return filteredMenu.isNotEmpty()
    }

    private fun getDayOfWeek(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString)
        val calendar = Calendar.getInstance().apply { time = date!! }

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
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
