package com.example.gaechuck

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.ui.business.BusinessActivity
import com.example.gaechuck.ui.rent.RentActivity
import com.example.gaechuck.ui.setting.SettingActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 이미지 클릭 이벤트 설정
        val settingImageView = findViewById<ImageView>(R.id.setting_icon)
        settingImageView.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            val intent2 = Intent(this, BusinessActivity::class.java)
            val intent3 = Intent(this, RentActivity::class.java)
            startActivity(intent3)
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val margin = (screenWidth * 0.05).toInt()
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(margin, margin, margin, margin)
            }
            child.layoutParams = params
        }
    }
}