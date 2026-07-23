package com.gaechuck_package.gaechuck.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gaechuck_package.gaechuck.BuildConfig
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.api.AppPreferences
import com.gaechuck_package.gaechuck.databinding.ActivitySplashBinding
import com.gaechuck_package.gaechuck.ui.auth.AuthActivity
import com.gaechuck_package.gaechuck.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        binding.versionText.text = "버전 ${BuildConfig.VERSION_NAME}"

        Handler(Looper.getMainLooper()).postDelayed({
            val destination = if (AppPreferences.isOnboardingShown()) {
                AuthActivity::class.java
            } else {
                OnboardingActivity::class.java
            }
            startActivity(Intent(this, destination))
            finish()
        }, 2000L)
    }
}
