package com.example.gaechuck.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var backButton: ImageView
    private lateinit var homeButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        homeButton = toolbar.findViewById(R.id.button_home)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // NavHostFragment에서 NavController 가져오기
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 뒤로가기 버튼 동작 실행
        backButton.setOnClickListener {
            if (!navController.popBackStack()) {
                if (navController.currentDestination?.id == R.id.loginFailFragment) {
                    navController.navigate(R.id.action_loginFailFragment_to_loginFragment)
                } else {
                    finish()
                }
            }
        }

        // 홈 버튼 동작 설정 : MainActivity로 이동
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // RentActivity 종료
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun updateToolbar(showBackButton: Boolean, showHomeButton: Boolean) {
        runOnUiThread {
            backButton.visibility = if (showBackButton) View.VISIBLE else View.GONE
            homeButton.visibility = if (showHomeButton) View.VISIBLE else View.GONE
        }
    }
}