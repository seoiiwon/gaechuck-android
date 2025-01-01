package com.example.gaechuck.ui.termsofuse

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R
import com.example.gaechuck.ui.setting.SettingActivity

class TermsOfUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termsofuse) // 레이아웃을 설정합니다

        // findViewById는 setContentView 이후에 호출해야 합니다
        val termOfUseLink = findViewById<TextView>(R.id.terms_bottom)

        termOfUseLink.setOnClickListener {
            // TODO : 관리자모드 Activity 생성 후 수정
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

}
