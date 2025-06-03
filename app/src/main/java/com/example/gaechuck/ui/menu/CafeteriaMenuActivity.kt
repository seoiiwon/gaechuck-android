package com.example.gaechuck.ui.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gaechuck.R
import com.example.gaechuck.data.response.FoodMenuItem
import com.example.gaechuck.ui.menu.adaptor.GridCafeteriaAdapter
import com.example.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.MainActivity
import com.example.gaechuck.repository.CafeteriaMenuRepository
import com.example.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModelFactory
import org.w3c.dom.Text
import java.util.*

class CafeteriaMenuActivity : AppCompatActivity() {

    private lateinit var campusSpinner: Spinner
    private lateinit var restaurantLayout: LinearLayout
    private lateinit var leftArrow: ImageView
    private lateinit var rightArrow: ImageView
    private lateinit var dayButtonContainer: LinearLayout
    private lateinit var menuGridRecyclerView: RecyclerView
    private lateinit var adapter: GridCafeteriaAdapter

    // 선택된 캠퍼스 식당 seq 관리, 인덱스 관리
    private var selectedSeqList: List<Int> = listOf(1)
    private var currentIndex = 0

    // 일주일 식단 데이터
    private var weekMenuData: List<FoodMenuItem> = emptyList()

    private var selectedDayIndex: Int = -1


    private val viewModel: CafeteriaMenuViewModel by viewModels {
        CafeteriaMenuViewModelFactory(CafeteriaMenuRepository())
    }

    private val campusMap = mapOf(
        "가좌캠퍼스" to listOf("가좌 교직원식당", "가좌 중앙1식당", "가좌 교육문화1층식당"),
        "칠암캠퍼스" to listOf("칠암 교직원식당", "칠암 학생식당", "칠암기숙사1", "칠암기숙사2"),
        "통영캠퍼스" to listOf("통영 교직원식당", "통영 학생식당", "통영기숙사")
    )

    private val seqMap = mapOf(
        "가좌캠퍼스" to listOf(1, 2, 3),
        "칠암캠퍼스" to listOf(4, 5, 9, 10),
        "통영캠퍼스" to listOf(6, 7, 11)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafeteria_menu)


        // UI 요소 초기화
        setupViews()
        setupCampusSpinner()
        setupDayButtons()
        observeViewModel()
    }

    private fun setupViews() {
        campusSpinner = findViewById(R.id.campusSpinner)
        restaurantLayout = findViewById(R.id.restaurantLayout)
        leftArrow = findViewById(R.id.leftArrow)
        rightArrow = findViewById(R.id.rightArrow)
        dayButtonContainer = findViewById(R.id.dayButtonContainer)
        menuGridRecyclerView = findViewById(R.id.menuGridRecyclerView)

        // TextView에 월요일 ~ 일요일 날짜 출력
        findViewById<TextView>(R.id.oneWeekPeriod).text = getCurrentWeekRange()

        // RecyclerView 그리드 설정 (디자인 수정되면 수정필요)
        adapter = GridCafeteriaAdapter()
        menuGridRecyclerView.layoutManager = GridLayoutManager(this, 2)
        menuGridRecyclerView.adapter = adapter

        // 뒤로 / 홈 버튼
        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.homeBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP })
        }

        leftArrow.setOnClickListener { navigateCafeteria(-1) }
        rightArrow.setOnClickListener { navigateCafeteria(+1) }

        updateArrowVisibility()

    }

    private var spinnerInitialized = false

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCampusSpinner() {
        val campuses = campusMap.keys.toList()

        val spinnerAdaptor = ArrayAdapter(
            this,
            R.layout.spinner_selected_item,
            campuses
        ).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        campusSpinner.adapter = spinnerAdaptor

        val popupWindow: ListPopupWindow? = try {
            val mPopupField = Spinner::class.java.getDeclaredField("mPopup")
            mPopupField.isAccessible = true
            mPopupField.get(campusSpinner) as? ListPopupWindow
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        popupWindow?.setOnDismissListener {
            campusSpinner.post {
                campusSpinner.background = ContextCompat.getDrawable(
                    this@CafeteriaMenuActivity,
                    R.drawable.spinner_bg_arrow_down
                )
            }
        }

        spinnerInitialized = false
        campusSpinner.setSelection(0, false)
        spinnerInitialized = true

        val firstCampus = campuses[0]
        selectedSeqList = seqMap[firstCampus] ?: emptyList()
        currentIndex = 0
        updateRestaurantTitle()
        loadMenuForCurrent()
        updateArrowVisibility()

        // 리스너 등록
        campusSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, pos: Int, id: Long
            ) {
                if (!spinnerInitialized) return

                val campusName = campuses[pos]
                selectedSeqList = seqMap[campusName] ?: emptyList()
                currentIndex = 0

                updateRestaurantTitle()
                loadMenuForCurrent()
                updateArrowVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        campusSpinner.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (popupWindow?.isShowing != true) {
                    campusSpinner.background = ContextCompat.getDrawable(
                        this@CafeteriaMenuActivity,
                        R.drawable.spinner_bg_arrow_up
                    )
                }
            }
            false
        }
    }

    // 요일버튼
    private fun setupDayButtons() {
        val days     = listOf("월","화","수","목","금","토","일")
        val dates    = getWeekDates()
        dayButtonContainer.removeAllViews()
        dayButtonContainer.weightSum = days.size.toFloat()

        // 오늘 인덱스
        val todayIdx = getDayOfWeekIndex(getTodayDate())
        selectedDayIndex = todayIdx
        val heightPx = (20 * resources.displayMetrics.density).toInt()

        days.forEachIndexed { idx, day ->
            val btn = Button(this).apply {
                text = day
                textSize = 10f
                typeface = Typeface.DEFAULT_BOLD
                background = ContextCompat.getDrawable(context, R.drawable.week_button)

                isSelected = (idx == todayIdx)

                layoutParams = LinearLayout.LayoutParams(0, heightPx, 1f)
                    .apply {
                        val margin = (4 * resources.displayMetrics.density).toInt()
                        setMargins(margin, 0 , margin, 0)
                        setPadding(0, 0, 0, 0)
                }

                setOnClickListener {
                    for(i in 0 until dayButtonContainer.childCount)
                        dayButtonContainer.getChildAt(i).isSelected = false

                    isSelected = true

                    selectedDayIndex = idx
//                    updateMenuForSelectedDay(dates[idx])

                    // “dates[idx]”는 List(“yyyy-MM-dd” 형태의 월요일~일요일) 중 한 날짜
                    val targetDate = dates[idx]
                    val filtered = viewModel.getMenuForDate(targetDate)
                    val toShow = if (filtered.isEmpty()) {
                        listOf(
                            FoodMenuItem(
                                menu="식단 정보가 없습니다.",
                                menuDivision="",
                                date=targetDate,
                                menuSeq=-1
                            )
                        )
                    } else {
                        filtered
                    }
                    adapter.submitList(toShow)
                }
            }
            dayButtonContainer.addView(btn)
        }
    }


    private fun observeViewModel() {
        // 일주일 데이터에서 자동으로 오늘 날짜 필터링
        viewModel.menuList.observe(this, Observer { list ->
            weekMenuData = list

            val dates = getWeekDates()

            val index = selectedDayIndex.takeIf { it in 0..6 } ?: getDayOfWeekIndex(getTodayDate())

            val targetDate = dates[index]

            // 기본 오늘 날짜 메뉴 표시
            updateMenuForSelectedDay(targetDate)
        })

        viewModel.errorMessage.observe(this, Observer { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
    }

    // 현재 캠퍼스 + 날짜 데이터 불러오기
    private fun loadMenuForCurrent() {
        adapter.submitList(emptyList())
        updateRestaurantTitle()
        if (selectedSeqList.isNotEmpty())
            viewModel.loadMenu(selectedSeqList[currentIndex])
    }


    private fun navigateCafeteria(dir: Int) {
        val newIdx = currentIndex + dir
        if (newIdx in selectedSeqList.indices) {
            currentIndex = newIdx
            loadMenuForCurrent()
            updateArrowVisibility()
        }
    }


    private fun updateRestaurantTitle() {
        restaurantLayout.removeAllViews()
        val campusName = (campusSpinner.selectedItem as? String) ?: campusMap.keys.firstOrNull() ?: "캠퍼스 정보 없음"
        val name = campusMap[campusName]?.getOrNull(currentIndex) ?: "식당 정보 없음"

        val tv = TextView(this).apply {
            text = name
            textSize = 16f
//            typeface = Typeface.DEFAULT_BOLD
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
            setPadding(16, 16, 16, 16)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        restaurantLayout.addView(tv)

        val subTitle = findViewById<TextView>(R.id.subTitle)
        subTitle.visibility =
            if (selectedSeqList[currentIndex] in listOf(2, 5, 7)) View.GONE else View.VISIBLE
    }

    // 날짜 받아서 필터링 + RecyclerView 갱신
    private fun updateMenuForSelectedDay(date: String) {
        val filtered = viewModel.getMenuForDate(date)
        val toShow = if (filtered.isEmpty()) {
            listOf(
                FoodMenuItem(
                    menu = "식단 정보가 없습니다.",
                    menuDivision = "",
                    date = date,
                    menuSeq = -1
                )
            )
        } else filtered

        adapter.submitList(toShow)
    }

    // 날짜 계산 유틸
    private fun getWeekDates(): List<String> {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK,
                Calendar.MONDAY)
        }

        return List(7) {
            fmt.format(cal.time).also{ cal.add(Calendar.DAY_OF_MONTH,1) }
        }
    }

    // 오늘 날짜
    private fun getTodayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // 일주일 날짜 텍스트
    private fun getCurrentWeekRange(): String {
        val fmt = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val mon = fmt.format(cal.time)
        cal.add(Calendar.DAY_OF_MONTH, 6)
        val sun = fmt.format(cal.time)
        return "$mon ~ $sun"
    }


    // 날짜 요일 매칭
    private fun getDayOfWeekIndex(isoDate: String): Int {
        val cal = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(isoDate)!!
        }
        return when(cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    private fun updateArrowVisibility() {
        val lastIdx = selectedSeqList.lastIndex

        when {
            // 식당이 0개 혹은 1개밖에 없으면: 양쪽 모두 숨김
            selectedSeqList.size <= 1 -> {
                leftArrow.visibility = View.INVISIBLE
                rightArrow.visibility = View.INVISIBLE
            }
            // 첫 번째 식당일 때: 왼쪽 숨기고, 오른쪽 보임
            currentIndex == 0 -> {
                leftArrow.visibility = View.INVISIBLE
                rightArrow.visibility = View.VISIBLE
            }
            // 마지막 식당일 때: 왼쪽 보이고, 오른쪽 숨김
            currentIndex == lastIdx -> {
                leftArrow.visibility = View.VISIBLE
                rightArrow.visibility = View.INVISIBLE
            }
            // 그 외(중간 인덱스)일 때: 양쪽 보임
            else -> {
                leftArrow.visibility = View.VISIBLE
                rightArrow.visibility = View.VISIBLE
            }
        }
    }
}