package com.gaechuck_package.gaechuck.ui.auth

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.ActivityAuthBinding
import com.gaechuck_package.gaechuck.ui.auth.signup.SignupActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyWindowInsets()
        setupFieldWatchers()
        setupButtons()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            binding.formContainer.setPadding(
                binding.formContainer.paddingLeft,
                binding.formContainer.paddingTop,
                binding.formContainer.paddingRight,
                (navBarHeight + resources.displayMetrics.density * 32).toInt()
            )
            insets
        }
    }

    private fun setupFieldWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = updateLoginButton()
        }
        binding.etId.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
    }

    private fun updateLoginButton() {
        val idFilled = binding.etId.text.isNotEmpty()
        val pwFilled = binding.etPassword.text.isNotEmpty()
        val enabled = idFilled && pwFilled
        binding.btnLogin.setBackgroundResource(
            if (enabled) R.drawable.bg_signup_btn_primary else R.drawable.bg_signup_btn_disabled
        )
        binding.btnLogin.setTextColor(
            resources.getColor(if (enabled) R.color.white else R.color.grey, null)
        )
        binding.btnLogin.isEnabled = enabled
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnLogin.setOnClickListener {
            navigateToMain()
        }

        binding.tvGuest.setOnClickListener {
            navigateToMain()
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val view = currentFocus
        if (view is EditText) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
