package com.example.gaechuck

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gaechuck.bus.viewmodel.BusRoute
import com.example.gaechuck.main.adaptor.ViewPagerAdapter
import com.example.gaechuck.menu.viewmodel.CafeteriaMenu
import com.example.gaechuck.noticecouncil.viewmodel.NoticeCouncil
import com.example.gaechuck.noticeuniv.viewmodel.GridClickListener
import com.example.gaechuck.noticeuniv.viewmodel.NoticeUniv
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

        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i) as LinearLayout
            child.setOnClickListener {
                // targetIndex 계산
                val targetIndex = i + (position * gridLayout.childCount)
                if (targetIndex in activityClasses.indices) {
                    val intent = Intent(this, activityClasses[targetIndex])
                    startActivity(intent)
                }
            }
        }
    }

}
