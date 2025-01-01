package com.example.gaechuck.ui.business

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R

class BusinessActivity : AppCompatActivity(R.layout.activity_business) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 첫 번째 프래그먼트가 없으면 추가
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BusinessMainFragment()) // BusinessMainFragment를 삽입할 컨테이너 뷰 지정
                .commit()
        }
    }
}
