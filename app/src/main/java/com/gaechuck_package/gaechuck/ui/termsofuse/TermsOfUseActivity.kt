package com.gaechuck_package.gaechuck.ui.termsofuse

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.ui.login.LoginActivity

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

        val token = AuthManager.getToken()
        val isLoggedIn = !token.isNullOrEmpty()

        if (isLoggedIn) {
            // 로그인 상태일 때: 링크 비활성화 및 색상 변경
            termOfUseLink.isEnabled = false
            termOfUseLink.setTextColor(ContextCompat.getColor(this, R.color.gnu_grey)) // 회색으로 변경 (colors.xml에 정의된 색상 사용)
            // 또는 직접 색상 지정: termOfUseLink.setTextColor(Color.GRAY)
        } else {
            // 로그아웃 상태일 때: 링크 활성화 및 클릭 리스너 설정
            termOfUseLink.isEnabled = true
            termOfUseLink.setTextColor(ContextCompat.getColor(this, R.color.gnu_blue)) // 원래 색상 (또는 적절한 링크 색상)

            termOfUseLink.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

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
