package com.example.gaechuck

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gaechuck.ui.bus.BusRouteActivity
import com.example.gaechuck.ui.business.BusinessActivity
import com.example.gaechuck.ui.main.adaptor.ViewPagerAdapter
import com.example.gaechuck.ui.menu.CafeteriaMenuActivity
import com.example.gaechuck.ui.noticecouncil.NoticeCouncilActivity
import com.example.gaechuck.ui.noticeuniv.NoticeUnivActivity
import com.example.gaechuck.ui.rent.RentActivity
import com.example.gaechuck.ui.setting.SettingActivity
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val layouts = listOf(R.layout.fragment_main1, R.layout.fragment_main2)
        val adapter = ViewPagerAdapter(layouts) { gridLayout, position ->
            setupGridClickListener(gridLayout, position)
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
    }

    private fun setupGridClickListener(gridLayout: GridLayout, position: Int) {
        val activityClasses = listOf(
            CafeteriaMenuActivity::class.java,
            RentActivity::class.java,
            NoticeCouncilActivity::class.java,
            BusinessActivity::class.java,
            NoticeUnivActivity::class.java,
            BusRouteActivity::class.java,
        )

        val secondPageActivity = SubPage2Activity::class.java

        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i) as? LinearLayout ?: continue

            child.setOnClickListener {
                val targetIndex = if (position == 0) i else 0
                val targetClass = if (position == 0) {
                    activityClasses.getOrNull(targetIndex)
                } else {
                    secondPageActivity
                }

                if (targetClass != null) {
                    val intent = Intent(this, targetClass)
                    startActivity(intent)
                } else {
                    Log.e("MainActivity", "Invalid targetIndex: $targetIndex")
                }
            }
        }
    }
}
