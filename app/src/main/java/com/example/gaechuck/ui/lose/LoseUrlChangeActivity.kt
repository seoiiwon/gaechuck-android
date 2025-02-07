package com.example.gaechuck.ui.lose

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R
import com.example.gaechuck.databinding.ActivityLoseUrlBinding

class LoseUrlChangeActivity : AppCompatActivity(R.layout.activity_lose_url) {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityLoseUrlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoseUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 초기 버튼 비활성화
        sendButton.isEnabled = false
        sendButton.setTextColor(getColor(R.color.gnu_grey))

        // url 변화 감지
        binding.fieldChangeUrl.addTextChangedListener(textWatcher)

    }

    // url 입력값 확인
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            val inputText = p0.toString().trim()

            // 입력값이 있을 경우 색상 변경 및 버튼 활성화
            if (inputText.isNotEmpty()) {
                binding.dividerChangeUrl.setDividerColor(getColor(R.color.gnu_blue))
                sendButton.isEnabled = true
                sendButton.setTextColor(getColor(R.color.gnu_blue))
            } else {
                binding.dividerChangeUrl.setDividerColor(getColor(R.color.grey))
                sendButton.isEnabled = false
                sendButton.setTextColor(getColor(R.color.gnu_grey))
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }
}