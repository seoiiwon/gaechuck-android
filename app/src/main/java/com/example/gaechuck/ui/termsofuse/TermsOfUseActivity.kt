package com.example.gaechuck.ui.termsofuse

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.ui.login.LoginActivity

class TermsOfUseActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var homeButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termsofuse) // 레이아웃을 설정합니다

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        titleTextView = toolbar.findViewById(R.id.textView_title)
        backButton = toolbar.findViewById(R.id.button_back)
        homeButton = toolbar.findViewById(R.id.button_home)

        // findViewById는 setContentView 이후에 호출해야 합니다
        val termOfUseLink = findViewById<TextView>(R.id.terms_bottom)

        termOfUseLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 뒤로가기 버튼 동작 설정
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        homeButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

}
