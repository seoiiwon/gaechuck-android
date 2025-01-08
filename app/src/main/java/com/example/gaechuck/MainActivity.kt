package com.example.gaechuck

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gaechuck.ui.bus.viewmodel.BusRoute
import com.example.gaechuck.ui.main.adaptor.ViewPagerAdapter
import com.example.gaechuck.ui.menu.viewmodel.CafeteriaMenu
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncil
import com.example.gaechuck.ui.noticeuniv.viewmodel.GridClickListener
import com.example.gaechuck.ui.noticeuniv.viewmodel.NoticeUniv
import com.example.gaechuck.ui.setting.SettingActivity
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class MainActivity : AppCompatActivity(), GridClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val layouts = listOf(R.layout.fragment_main1, R.layout.fragment_main2)
        val adapter = ViewPagerAdapter(this, layouts)
        viewPager.adapter = adapter

        // WormDotsIndicator 설정
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dotsIndicator)
        dotsIndicator.attachTo(viewPager)
    }

    override fun onGridClick(gridLayout: GridLayout, position: Int) {
        val activityClasses = listOf(
            NoticeUniv::class.java,
            NoticeCouncil::class.java,
            CafeteriaMenu::class.java,
            BusRoute::class.java,
            Sub5Activity::class.java,
            Sub6Activity::class.java,
            Sub7Activity::class.java
        )

        // 이미지 클릭 이벤트 설정
        val settingImageView = findViewById<ImageView>(R.id.setting_icon)
        settingImageView.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val margin = (screenWidth * 0.05).toInt()

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        if (gridLayout == null) {
            Log.e("MainActivity", "GridLayout not found. Ensure the correct layout is loaded.")
            return
        }


        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i) as LinearLayout

            // LayoutParams 생성 및 설정
            val params = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(margin, margin, margin, margin)
            }
            child.layoutParams = params

            // 클릭 리스너 설정
            child.setOnClickListener {
                val targetIndex = i + (position * gridLayout.childCount)
                if (targetIndex in activityClasses.indices) {
                    val intent = Intent(this, activityClasses[targetIndex])
                    startActivity(intent)
                }
            }
        }
    }
}
