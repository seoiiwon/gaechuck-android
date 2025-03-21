package com.example.gaechuck.ui.menu

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gaechuck.R
import com.example.gaechuck.data.response.FoodMenuItem
import com.example.gaechuck.ui.menu.adaptor.GridCafeteriaAdapter
import com.example.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModdel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import java.util.*

class CafeteriaMenuActivity : AppCompatActivity() {

    private lateinit var campusSpinner: Spinner
    private lateinit var restaurantLayout: LinearLayout
    private lateinit var leftArrow: ImageView
    private lateinit var rightArrow: ImageView
    private lateinit var buttonContainer: LinearLayout
    private lateinit var menuGridRecyclerView: RecyclerView
    private lateinit var gridAdapter: GridCafeteriaAdapter

    private var selectedCafeteriaSeq: List<Int> = listOf(1)
    private var currentIndex = 0
    private var weekMenuData: List<FoodMenuItem> = emptyList()
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

        // UI 요소 초기화
        setupViews()

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        // 캠퍼스 선택 UI 초기화
        setupCampusSpinner()

        // 초기 데이터 요청
        fetchInitialData()
    }

    private fun setupViews() {
        findViewById<TextView>(R.id.oneWeekPeriod).text = getCurrentWeekRange()

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        menuGridRecyclerView = findViewById(R.id.menuGridRecyclerView)
        menuGridRecyclerView.layoutManager = LinearLayoutManager(this)
        gridAdapter = GridCafeteriaAdapter(emptyList())
        menuGridRecyclerView.adapter = gridAdapter

        campusSpinner = findViewById(R.id.campusSpinner)
        restaurantLayout = findViewById(R.id.restaurantLayout)
        leftArrow = findViewById(R.id.leftArrow)
        rightArrow = findViewById(R.id.rightArrow)

        buttonContainer = findViewById(R.id.dayButtonContainer)
        buttonContainer.orientation = LinearLayout.HORIZONTAL
        leftArrow.setOnClickListener { navigateCafeteria(-1) }
        rightArrow.setOnClickListener { navigateCafeteria(1) }
    }

    private fun setupCampusSpinner() {
        val campusList = campusMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, campusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        campusSpinner.adapter = adapter

        campusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCafeteriaSeq = cafeteriaSeqMap[campusList[position]] ?: emptyList()
                currentIndex = 0
                updateRestaurantDisplay()
                fetchInitialData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchInitialData() {
        val startDate = getWeekDates().first()
        viewModel.fetchFoodMenuByDate(selectedCafeteriaSeq[currentIndex], startDate)

        viewModel.menuList.removeObservers(this)
        viewModel.menuList.observe(this, Observer { menuList ->
            weekMenuData = menuList
            updateMenuForSelectedDay(getTodayDate())
        })

        setupDayButtons()
    }


    private fun setupDayButtons() {
        val days = listOf("월", "화", "수", "목", "금", "토", "일")
        val weekDates = getWeekDates()
        val container = findViewById<LinearLayout>(R.id.dayButtonContainer)
        container.removeAllViews()
        container.orientation = LinearLayout.HORIZONTAL
        container.weightSum = days.size.toFloat()

        fun dpToPx(dp: Int): Int {
            return (dp * resources.displayMetrics.density).toInt()
        }

        val todayIndex = getDayOfWeekIndex(getTodayDate())

        days.forEachIndexed { index, day ->
            val button = Button(this)
            val params = LinearLayout.LayoutParams(0, dpToPx(20), 1f)
            params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
            button.layoutParams = params

            button.text = day
            button.textSize = 8f
            button.setTypeface(null, Typeface.BOLD)
            button.background = ContextCompat.getDrawable(this, R.drawable.week_button)
            button.setTextColor(ContextCompat.getColor(this, R.color.black))
            button.setPadding(0, 0, 0, 0)

            button.isSelected = index == todayIndex
            button.setOnClickListener {
                for (i in 0 until container.childCount) {
                    container.getChildAt(i).isSelected = false
                }
                button.isSelected = true
                val selectedDate = weekDates[index]
                updateMenuForSelectedDay(selectedDate)
            }
            container.addView(button)
        }
    }

    private fun navigateCafeteria(direction: Int) {
        val newIndex = currentIndex + direction
        if (newIndex in selectedCafeteriaSeq.indices) {
            currentIndex = newIndex
            updateRestaurantDisplay()

            fetchInitialData()

            menuGridRecyclerView.adapter = null
            menuGridRecyclerView.layoutManager = null
            menuGridRecyclerView.layoutManager = GridLayoutManager(this, 2)
            menuGridRecyclerView.adapter = gridAdapter
        }
    }

    private fun updateMenuForSelectedDay(selectedDate: String) {
        val filteredMenu = weekMenuData.filter { it.date == selectedDate }
        Log.d("FilterData", "선택한 날짜: $selectedDate, 필터링된 데이터: $filteredMenu")

        if (filteredMenu.isEmpty()) {
            val noDataMessage = FoodMenuItem(
                menuDivision = "",
                menu = "식단 정보가 없습니다.",
                date = selectedDate,
                menuSeq = -1
            )
            updateMenuUI(listOf(noDataMessage))
        } else {
            updateMenuUI(filteredMenu)
        }
    }

    private fun updateRestaurantDisplay() {
        restaurantLayout.removeAllViews()
        val restaurantText = campusMap[campusSpinner.selectedItem]?.getOrElse(currentIndex) { "식당 정보 없음" }
        val textView = TextView(this).apply {
            text = restaurantText
            setPadding(16, 16, 16, 16)
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        restaurantLayout.addView(textView)

        // 만약 현재 선택된 cafeteriaSeq가 2, 5, 7이면 subTitle (즉, "중식" 텍스트)를 숨깁니다.
        val subTitle = findViewById<TextView>(R.id.subTitle)
        if (selectedCafeteriaSeq[currentIndex] in listOf(2, 5, 7)) {
            subTitle.visibility = View.GONE
        } else {
            subTitle.visibility = View.VISIBLE
        }
    }

    private fun updateMenuUI(menuList: List<FoodMenuItem>) {
        when (selectedCafeteriaSeq[currentIndex]) {
            1 -> gridLayoutCafeteriaSeq1(menuList, 1)
            2 -> gridLayoutCafeteriaSeq2(menuList)
            3 -> gridLayoutCafeteriaSeq3(menuList)
            4 -> gridLayoutCafeteriaSeq1(menuList, 4)
            5 -> gridLayoutCafeteriaSeq1(menuList, 5)
            6 -> gridLayoutCafeteriaSeq1(menuList, 6)
            7 -> gridLayoutCafeteriaSeq1(menuList, 7)
            else -> {
                gridAdapter.updateMenuList(emptyList()) // 기본 UI 처리
            }
        }
    }


    private fun getCurrentWeekRange(): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val sunday = sdf.format(calendar.time)
        return "$monday ~ $sunday"
    }

    private fun getWeekDates(): List<String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return List(7) {
            val date = sdf.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            date
        }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getDayOfWeekIndex(dateString: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            time = sdf.parse(dateString)!!
        }
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> 6
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            else -> 0
        }
    }

    private fun gridLayoutCafeteriaSeq1(menuList: List<FoodMenuItem>, cafeteriaSeq: Int) {
        val categorizedMenu = mutableListOf<FoodMenuItem>()

        val sortedMenuList = if (cafeteriaSeq == 5 || cafeteriaSeq == 7) {
            val order = listOf("아침", "점심", "저녁")
            menuList.sortedBy { item ->
                order.indexOfFirst { item.menuDivision.contains(it) }.let { if (it == -1) Int.MAX_VALUE else it }
            }
        } else {
            menuList
        }

        val groupedMenu = sortedMenuList.filter { it.menu.isNotBlank() }
            .groupBy { it.menuDivision }

        if (groupedMenu.isEmpty()) {
            categorizedMenu.add(FoodMenuItem(menuDivision = "식단 정보가 없습니다.", menu = "", date = "", menuSeq = -2))
        } else {
            groupedMenu.forEach { (division, items) ->
                if (items.isNotEmpty()) {
                    categorizedMenu.add(FoodMenuItem(menuDivision = division, menu = "", date = "", menuSeq = -2))
                    categorizedMenu.addAll(items)
                }
            }
        }

        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (categorizedMenu[position].menuSeq == -2) 1 else 2
            }
        }

        menuGridRecyclerView.layoutManager = layoutManager
        menuGridRecyclerView.adapter = GridCafeteriaAdapter(categorizedMenu)
    }

    private fun gridLayoutCafeteriaSeq2(menuList: List<FoodMenuItem>) {
        val categoryOrder = listOf(
            "천원의아침밥",
            "중식",
            "석식",
            "고정메뉴",
            "더진국"
        )

        val categorizedMenu = mutableListOf<FoodMenuItem>()

        categoryOrder.forEach { category ->
            val filteredItems = menuList.filter { it.menuDivision.contains(category) }

            if (filteredItems.isNotEmpty()) {
                categorizedMenu.add(
                    FoodMenuItem(menuDivision = category, menu = "", date = "", menuSeq = -2)
                )

                filteredItems.forEach { item ->
                    categorizedMenu.add(
                        FoodMenuItem(menuDivision = "", menu = item.menu, date = item.date, menuSeq = item.menuSeq)
                    )
                }
            }
        }

        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (categorizedMenu[position].menuSeq == -2) 1 else 2
            }
        }

        menuGridRecyclerView.layoutManager = layoutManager
        menuGridRecyclerView.adapter = GridCafeteriaAdapter(categorizedMenu)
    }

    private fun gridLayoutCafeteriaSeq3(menuList: List<FoodMenuItem>) {
        val categorizedMenu = mutableListOf<FoodMenuItem>()

        menuList.filter { it.menu.isNotBlank() }.forEach { item ->
            val pattern = Regex("(\\S+)\\s*\\(\\d{1,3},?\\d{3}원\\)")
            val matches = pattern.findAll(item.menu).map { it.value }.toList()

            Log.d("MenuParsing", "원본 메뉴 데이터: ${item.menu}")

            if (matches.size < 2) {
                Log.w("MenuParsing", "카테고리(양식, 교육세트) 부족으로 스킵됨")
                return@forEach
            }

            val categories = matches.map { it.replace(Regex("\\(\\d{1,3},?\\d{3}원\\)"), "").trim() }
            val menuText = item.menu.replace(Regex("\\(\\d{1,3},?\\d{3}원\\)"), "").trim()
            Log.d("MenuParsing", "정제된 메뉴 텍스트: $menuText")

            val firstCategoryMenu = menuText
                .substringAfter(categories[0])
                .substringBefore(categories[1])
                .trim()

            val secondCategoryMenu = menuText
                .substringAfter(categories[1])
                .trim()

            Log.d("MenuParsing", "양식 메뉴 (한 단어): $firstCategoryMenu")
            Log.d("MenuParsing", "교육세트 메뉴 (전체): $secondCategoryMenu")

            categorizedMenu.add(FoodMenuItem(menuDivision = categories[0], menu = "", date = item.date, menuSeq = -2))
            if (firstCategoryMenu.isNotBlank())
                categorizedMenu.add(FoodMenuItem(menuDivision = "", menu = firstCategoryMenu, date = item.date, menuSeq = item.menuSeq))

            categorizedMenu.add(FoodMenuItem(menuDivision = categories[1], menu = "", date = item.date, menuSeq = -2))
            if (secondCategoryMenu.isNotBlank())
                categorizedMenu.add(FoodMenuItem(menuDivision = "", menu = secondCategoryMenu, date = item.date, menuSeq = item.menuSeq))
        }

        if (categorizedMenu.isEmpty()) {
            categorizedMenu.add(FoodMenuItem(menuDivision = "", menu = "식단 정보가 없습니다.", date = "", menuSeq = -1))
        }

        val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (categorizedMenu[position].menuSeq == -2) 1 else 2
            }
        }

        menuGridRecyclerView.layoutManager = layoutManager
        menuGridRecyclerView.adapter = GridCafeteriaAdapter(categorizedMenu)
    }
}