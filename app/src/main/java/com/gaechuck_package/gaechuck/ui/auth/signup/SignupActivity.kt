package com.gaechuck_package.gaechuck.ui.auth.signup

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_signup) as NavHostFragment
        navController = navHostFragment.navController

        binding.btnBack.setOnClickListener {
            if (!navController.navigateUp()) finish()
        }
    }

    fun updateStep(step: Int, totalSteps: Int = 4) {
        binding.headerContainer.isVisible = true
        val track = binding.headerContainer
        track.post {
            val fraction = step.toFloat() / totalSteps.toFloat()
            binding.progressFill.layoutParams.width = (track.width * fraction).toInt()
            binding.progressFill.requestLayout()
        }
    }

    fun hideStepHeader() {
        binding.headerContainer.isVisible = false
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
