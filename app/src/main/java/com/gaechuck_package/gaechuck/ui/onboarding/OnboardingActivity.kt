package com.gaechuck_package.gaechuck.ui.onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.viewpager2.widget.ViewPager2
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.api.AppPreferences
import com.gaechuck_package.gaechuck.databinding.ActivityOnboardingBinding
import com.gaechuck_package.gaechuck.ui.auth.AuthActivity
import com.gaechuck_package.gaechuck.ui.auth.signup.SignupActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val dotViews get() = listOf(binding.dot1, binding.dot2, binding.dot3, binding.dot4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        applyWindowInsets()
        setupViewPager()
        setupButtons()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            binding.bottomBar.updatePadding(bottom = navBarHeight + dpToPx(24))
            insets
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = OnboardingAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                updateBottomBar(position)
            }
        })
    }

    private fun updateDots(position: Int) {
        dotViews.forEachIndexed { index, dot ->
            val isActive = index == position
            dot.setBackgroundResource(if (isActive) R.drawable.dot_active else R.drawable.dot_inactive)
            dot.layoutParams.width = if (isActive) dpToPx(24) else dpToPx(8)
            dot.layoutParams.height = dpToPx(8)
            dot.requestLayout()
        }
    }

    private fun updateBottomBar(position: Int) {
        val isLast = position == OnboardingItem.ITEMS.lastIndex
        binding.dotsContainer.visibility = if (isLast) View.GONE else View.VISIBLE
        binding.btnStart.visibility = if (isLast) View.VISIBLE else View.GONE
        binding.btnSignup.visibility = if (isLast) View.VISIBLE else View.GONE
    }

    private fun setupButtons() {
        binding.btnStart.setOnClickListener {
            AppPreferences.setOnboardingShown()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnSignup.setOnClickListener {
            AppPreferences.setOnboardingShown()
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val current = binding.viewPager.currentItem
        if (current > 0) {
            binding.viewPager.currentItem = current - 1
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}
