package com.gaechuck_package.gaechuck.ui.menu

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.FoodMenuItem
import com.gaechuck_package.gaechuck.repository.CafeteriaMenuRepository
import com.gaechuck_package.gaechuck.ui.menu.adaptor.CafeteriaSectionAdapter
import com.gaechuck_package.gaechuck.ui.menu.adaptor.MenuSection
import com.gaechuck_package.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModel
import com.gaechuck_package.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class CafeteriaMenuActivity : AppCompatActivity() {

    private lateinit var campusDropdown: TextView
    private lateinit var restaurantChipContainer: LinearLayout
    private lateinit var dayButtonContainer: LinearLayout
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var todayDateLabel: TextView
    private lateinit var sectionAdapter: CafeteriaSectionAdapter

    private var currentCampus = "가좌캠퍼스"
    private var currentSeq = 2
    private var selectedDayIndex = -1
    private var weekMenuData: List<FoodMenuItem> = emptyList()

    private val viewModel: CafeteriaMenuViewModel by viewModels {
        CafeteriaMenuViewModelFactory(CafeteriaMenuRepository())
    }

    private val campusMap = mapOf(
        "가좌캠퍼스" to listOf("중앙식당", "교직원 식당", "교육문화 1층", "학생생활관"),
        "칠암캠퍼스" to listOf("교직원 식당", "학생식당", "학생생활관1", "학생생활관2"),
        "통영캠퍼스" to listOf("교직원 식당", "학생식당", "학생생활관")
    )
    private val seqMap = mapOf(
        "가좌캠퍼스" to listOf(2, 1, 3, 8),
        "칠암캠퍼스" to listOf(4, 5, 9, 10),
        "통영캠퍼스" to listOf(6, 7, 11)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafeteria_menu)

        campusDropdown = findViewById(R.id.campusDropdown)
        restaurantChipContainer = findViewById(R.id.restaurantChipContainer)
        dayButtonContainer = findViewById(R.id.dayButtonContainer)
        menuRecyclerView = findViewById(R.id.menuGridRecyclerView)
        todayDateLabel = findViewById(R.id.todayDateLabel)

        sectionAdapter = CafeteriaSectionAdapter()
        menuRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CafeteriaMenuActivity)
            adapter = sectionAdapter
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            itemAnimator = null   // 아코디언 토글 시 깜빡임 방지
        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.homeBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        setupCampusDropdown()
        setupDayButtons()
        observeViewModel()

        updateRestaurantChips()
        viewModel.loadMenu(currentSeq)
    }

    private fun setupCampusDropdown() {
        val campusList = campusMap.keys.toList()
        campusDropdown.setOnClickListener {
            val popup = ListPopupWindow(this)
            popup.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, campusList))
            popup.anchorView = campusDropdown
            popup.width = ListPopupWindow.WRAP_CONTENT
            popup.isModal = true
            popup.setOnItemClickListener { _, _, position, _ ->
                val selected = campusList[position]
                currentCampus = selected
                campusDropdown.text = "$selected ▾"
                currentSeq = seqMap[selected]?.firstOrNull() ?: 2
                popup.dismiss()
                updateRestaurantChips()
                viewModel.loadMenu(currentSeq)
            }
            popup.show()
        }
        campusDropdown.text = "$currentCampus ▾"
    }

    private fun updateRestaurantChips() {
        restaurantChipContainer.removeAllViews()
        val restaurants = campusMap[currentCampus] ?: return
        val seqs = seqMap[currentCampus] ?: return

        restaurants.forEachIndexed { idx, name ->
            val chip = TextView(this).apply {
                text = name
                textSize = 14f
                typeface = ResourcesCompat.getFont(this@CafeteriaMenuActivity, R.font.pretendard_medium)
                setTextColor(Color.parseColor("#999999"))
                setPadding(dp(16), dp(8), dp(16), dp(8))
                gravity = android.view.Gravity.CENTER
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.marginEnd = dp(4)
                layoutParams = lp
                setBackgroundResource(R.drawable.bg_restaurant_unselected)
                isClickable = true
                isFocusable = true
            }

            if (seqs[idx] == currentSeq) applyChipSelected(chip)

            chip.setOnClickListener {
                currentSeq = seqs[idx]
                updateRestaurantChips()
                viewModel.loadMenu(currentSeq)
            }
            restaurantChipContainer.addView(chip)
        }
    }

    private fun applyChipSelected(chip: TextView) {
        chip.setBackgroundResource(R.drawable.bg_restaurant_selected)
        chip.setTextColor(Color.parseColor("#005478"))
        chip.typeface = ResourcesCompat.getFont(this, R.font.pretendard_semibold)
    }

    private fun setupDayButtons() {
        val days = listOf("일", "월", "화", "수", "목", "금", "토")
        val dates = getWeekDates()
        dayButtonContainer.removeAllViews()

        val todayIdx = getDayOfWeekIndex(getTodayDate())
        selectedDayIndex = todayIdx

        days.forEachIndexed { idx, day ->
            val dateNum = dates[idx].split("-").last().trimStart('0').ifEmpty { "0" }

            val itemView = LayoutInflater.from(this).inflate(R.layout.item_day_button, dayButtonContainer, false)
            val root = itemView.findViewById<LinearLayout>(R.id.dayButtonRoot)
            val labelTv = itemView.findViewById<TextView>(R.id.dayLabel)
            val numberTv = itemView.findViewById<TextView>(R.id.dayNumber)

            labelTv.text = day
            numberTv.text = dateNum

            val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            itemView.layoutParams = lp

            applyDayState(root, labelTv, numberTv, idx == todayIdx)

            itemView.setOnClickListener {
                for (i in 0 until dayButtonContainer.childCount) {
                    val child = dayButtonContainer.getChildAt(i)
                    applyDayState(
                        child.findViewById(R.id.dayButtonRoot),
                        child.findViewById(R.id.dayLabel),
                        child.findViewById(R.id.dayNumber),
                        i == idx
                    )
                }
                selectedDayIndex = idx
                updateMenuForDate(dates[idx])
                updateTodayLabel(dates[idx])
            }

            dayButtonContainer.addView(itemView)
        }

        if (todayIdx in dates.indices) updateTodayLabel(dates[todayIdx])
    }

    private fun applyDayState(root: LinearLayout, label: TextView, number: TextView, selected: Boolean) {
        if (selected) {
            root.setBackgroundResource(R.drawable.bg_day_selected)
            label.typeface = ResourcesCompat.getFont(this, R.font.pretendard_semibold)
            label.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            number.background = null
            number.typeface = ResourcesCompat.getFont(this, R.font.pretendard_semibold)
            number.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        } else {
            root.background = null
            label.typeface = ResourcesCompat.getFont(this, R.font.pretendard_regular)
            label.setTextColor(Color.parseColor("#767676"))
            number.background = null
            number.typeface = ResourcesCompat.getFont(this, R.font.pretendard_regular)
            number.setTextColor(Color.parseColor("#767676"))
        }
    }

    private fun updateTodayLabel(isoDate: String) {
        val cal = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(isoDate)!!
        }
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dayName = arrayOf("일", "월", "화", "수", "목", "금", "토")[cal.get(Calendar.DAY_OF_WEEK) - 1]
        todayDateLabel.text = "${month}월 ${day}일 ${dayName}요일"
    }

    private fun observeViewModel() {
        viewModel.menuList.observe(this, Observer { list ->
            weekMenuData = list
            val dates = getWeekDates()
            val idx = selectedDayIndex.takeIf { it in 0..6 } ?: getDayOfWeekIndex(getTodayDate())
            updateMenuForDate(dates[idx])
        })
        viewModel.errorMessage.observe(this, Observer { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
    }

    // 고정 메뉴 → 아침밥 → 중식 → 석식 → 나머지
    private val sectionPriority = listOf("고정메뉴", "천원의아침밥", "아침", "중식", "점심", "석식", "저녁")

    private fun sectionOrder(title: String): Int {
        val key = title.replace(" ", "")
        val idx = sectionPriority.indexOfFirst { key.contains(it) }
        return if (idx >= 0) idx else sectionPriority.size
    }

    private fun updateMenuForDate(date: String) {
        val raw = viewModel.getMenuForDate(date).filter { it.menu.isNotBlank() }
        if (raw.isEmpty()) {
            sectionAdapter.submitSections(listOf(MenuSection("메뉴 정보가 없습니다.", "", "")))
            return
        }

        val sections = raw
            .groupBy { it.menuDivision }
            .map { (division, items) ->
                val combinedMenu = items
                    .flatMap { item ->
                        item.menu.split("/", "\n").map { it.trim() }.filter { it.isNotBlank() }
                    }
                    .distinct()
                    .joinToString("\n")
                val (title, time) = parseDivision(division)
                MenuSection(title = title, time = time, menuText = combinedMenu, isExpanded = true)
            }
            .sortedBy { sectionOrder(it.title) }

        sectionAdapter.submitSections(sections)
    }

    private fun parseDivision(division: String): Pair<String, String> {
        val timeRegex = "\\d{1,2}:\\d{2}".toRegex()
        val match = timeRegex.find(division)
        return if (match != null) {
            val title = division.substring(0, match.range.first).trim().trimEnd('(').trim()
            val time = division.substring(match.range.first).trim()
            Pair(title.ifBlank { division }, time)
        } else {
            Pair(division, "")
        }
    }

    private fun getWeekDates(): List<String> {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }
        return List(7) { fmt.format(cal.time).also { cal.add(Calendar.DAY_OF_MONTH, 1) } }
    }

    private fun getTodayDate() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun getDayOfWeekIndex(isoDate: String): Int {
        val cal = Calendar.getInstance().apply {
            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(isoDate)!!
        }
        return cal.get(Calendar.DAY_OF_WEEK) - 1  // 1=Sun→0, 2=Mon→1 ... 7=Sat→6
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
