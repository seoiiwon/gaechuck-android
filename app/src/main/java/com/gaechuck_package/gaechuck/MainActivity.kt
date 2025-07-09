package com.gaechuck_package.gaechuck

//import com.example.gaechuck.ui.bus.viewmodel.BusRoute
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gaechuck_package.gaechuck.ui.bus.BusRouteActivity
import com.gaechuck_package.gaechuck.ui.business.BusinessActivity
import com.gaechuck_package.gaechuck.ui.lose.LoseActivity
import com.gaechuck_package.gaechuck.ui.main.adaptor.ViewPagerAdapter
import com.gaechuck_package.gaechuck.ui.menu.CafeteriaMenuActivity
import com.gaechuck_package.gaechuck.ui.noticecouncil.NoticeCouncilActivity
import com.gaechuck_package.gaechuck.ui.noticeuniv.NoticeUnivActivity
import com.gaechuck_package.gaechuck.ui.rent.RentActivity
import com.gaechuck_package.gaechuck.ui.setting.SettingActivity
import com.gaechuck_package.gaechuck.ui.util.DialogFragment
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.getBooleanExtra("refreshNotice", false)) {
            val noticeIntent = Intent(this, NoticeCouncilActivity::class.java)
            startActivity(noticeIntent)
            finish()
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.isNestedScrollingEnabled = true
        val layouts = listOf(R.layout.fragment_main1, R.layout.fragment_main2)
        val adapter = ViewPagerAdapter(layouts) { gridLayout, position ->
            val isFirstPage = position == 0
            val activities = if (isFirstPage) {
                listOf(
                    CafeteriaMenuActivity::class.java,
                    RentActivity::class.java,
                    LoseActivity::class.java,
                    BusinessActivity::class.java,
                    NoticeUnivActivity::class.java,
                    NoticeCouncilActivity::class.java
                )
            } else {
                listOf(
                    BusRouteActivity::class.java
                )
            }
            setupGridClickListener(gridLayout, activities, isFirstPage)
        }
        viewPager.adapter = adapter

        // WormDotsIndicator 설정
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dotsIndicator)
        dotsIndicator.attachTo(viewPager)

        // 설정 버튼
        val settingImageView = findViewById<ImageView>(R.id.setting_icon)
        settingImageView.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        val dialogFragment = DialogFragment(this)

        // 알림 버튼
        val alarmButton = findViewById<ImageView>(R.id.alarm_icon)
        alarmButton.setOnClickListener{
            dialogFragment.show()
        }

    }



    private fun setupGridClickListener(gridLayout: GridLayout, activities: List<Class<*>>, isFirstPage: Boolean) {
        val maxItems = 6 // 2열 3행 유지

        gridLayout.removeAllViews() // 기존 항목 제거
        gridLayout.columnCount = 2
        gridLayout.rowCount = 3

        val screenWidth = resources.displayMetrics.widthPixels // 디바이스 전체 너비
        val padding = dpToPx(16) * 2 // 좌우 패딩 합산
        val availableWidth = screenWidth - padding // 가용 너비 (전체 너비 - 패딩)
        val itemSize = (availableWidth - dpToPx(16)) / 2 // 각 아이템 크기 설정 (2열 기준)

        for (i in 0 until maxItems) {
            val itemView = if (isFirstPage) {
                // 🔹 첫 번째 페이지 (6개 아이템 배치)
                if (i < activities.size) {
                    LinearLayout(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = itemSize
                            height = itemSize
                            setMargins(dpToPx(4), dpToPx(0), dpToPx(4), dpToPx(8)) // 🔹 각 아이템 여백 설정
                            columnSpec = GridLayout.spec(i % 2) // 2열 맞추기
                            rowSpec = GridLayout.spec(i / 2) // 3행 맞추기
                        }

                        setBackgroundResource(R.drawable.rounded_border) // 배경 추가
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER
                        setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

                        setOnClickListener {
                            startActivity(Intent(this@MainActivity, activities[i]))
                        }

                        val imageView = ImageView(context).apply {
                            setImageResource(getImageResource(i))
                        }

                        val textView = TextView(context).apply {
                            text = getTextResource(i)
                            textSize = 14f
                            gravity = Gravity.CENTER
                        }

                        addView(imageView)
                        addView(textView)
                    }
                } else {
                    View(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = itemSize
                            height = itemSize
                            setMargins(dpToPx(4), dpToPx(0), dpToPx(4), dpToPx(8))
                            columnSpec = GridLayout.spec(i % 2)
                            rowSpec = GridLayout.spec(i / 2)
                        }
                        setBackgroundColor(android.graphics.Color.TRANSPARENT) // 빈칸을 투명하게 유지
                    }
                }
            } else {
                // 🔹 두 번째 페이지 (1개의 아이템만 1열 1행(0,0)에 배치)
                if (i == 0) {
                    LinearLayout(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = itemSize
                            height = itemSize
                            setMargins(dpToPx(4), dpToPx(0), dpToPx(4), dpToPx(8))
                            columnSpec = GridLayout.spec(0) // 1열 (첫 번째 열)
                            rowSpec = GridLayout.spec(0) // 1행 (첫 번째 행)
                        }

                        setBackgroundResource(R.drawable.rounded_border) // 배경 추가
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER
                        setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

                        setOnClickListener {
                            if (activities.isNotEmpty()) {
                                startActivity(Intent(this@MainActivity, activities[0]))
                            }
                        }

                        val imageView = ImageView(context).apply {
                            setImageResource(R.drawable.gnu_main_btn_7) // 아이콘 변경
                        }

                        val textView = TextView(context).apply {
                            text = getString(R.string.main_button_7) // 텍스트 변경
                            textSize = 14f
                            gravity = Gravity.CENTER
                        }

                        addView(imageView)
                        addView(textView)
                    }
                } else {
                    View(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = itemSize
                            height = itemSize
                            setMargins(dpToPx(4), dpToPx(0), dpToPx(4), dpToPx(8))
                            columnSpec = GridLayout.spec(i % 2)
                            rowSpec = GridLayout.spec(i / 2)
                        }
                        setBackgroundColor(android.graphics.Color.TRANSPARENT) // 빈칸을 투명하게 유지
                    }
                }
            }

            gridLayout.addView(itemView)
        }
    }

    private fun getImageResource(index: Int): Int {
        val imageList = listOf(
            R.drawable.gnu_main_btn_1,
            R.drawable.gnu_main_btn_2,
            R.drawable.gnu_main_btn_3,
            R.drawable.gnu_main_btn_4,
            R.drawable.gnu_main_btn_5,
            R.drawable.gnu_main_btn_6
        )
        return imageList.getOrElse(index) { R.drawable.gnu_main_btn_1 }
    }

    private fun getTextResource(index: Int): String {
        val textList = listOf(
            getString(R.string.main_button_1),
            getString(R.string.main_button_2),
            getString(R.string.main_button_3),
            getString(R.string.main_button_4),
            getString(R.string.main_button_5),
            getString(R.string.main_button_6)
        )
        return textList.getOrElse(index) { "" }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


}
