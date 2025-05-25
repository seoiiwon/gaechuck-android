package com.example.gaechuck.ui.lose

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gaechuck.R
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.request.UrlChangeRequest
import com.example.gaechuck.databinding.ActivityLoseUrlBinding
import com.example.gaechuck.ui.rent.RentActivity
import kotlinx.coroutines.launch

class LoseUrlChangeActivity : AppCompatActivity(R.layout.activity_lose_url) {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityLoseUrlBinding
    private lateinit var chatName: String  // chatName 값을 저장할 변수


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

        // Intent에서 chatName 가져오기
        chatName = intent.getStringExtra("chatName") ?: ""

        // 초기 버튼 비활성화
        sendButton.isEnabled = false
        sendButton.setTextColor(getColor(R.color.gnu_grey))

        // url 변화 감지
        binding.fieldChangeUrl.addTextChangedListener(textWatcher)

        loadUrlFromServer()

        // 완료 버튼 누르면 url 정보 저장
        sendButton.setOnClickListener{
            val newUrl = binding.fieldChangeUrl.text.toString().trim()
            if (newUrl.isNotEmpty()) {
                updateUrl(newUrl)
            }
        }
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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val view = currentFocus
        if (view is EditText) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                // EditText 외부를 클릭하면 키보드 숨기기
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    // 초기값
    private fun loadUrlFromServer() {
        lifecycleScope.launch {
            try {
                val response = ApiConnection.getRetrofitService.getUrl(chatName)
                if (response.isSuccessful) {
                    val url = response.body()?.result?.chatUrl ?: "https://www.naver.com"
                    binding.fieldCurrentUrl.setText(url)
                } else {
                    Log.e("LoseUrlChangeActivity", "URL 불러오기 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // url 업데이트
    private fun updateUrl(newUrl: String) {
        lifecycleScope.launch {
            try {
//                val token = "Bearer ${AuthManager.getToken()}"
                val request = UrlChangeRequest(chatName, newUrl)
                val response = ApiConnection.getRetrofitService.postUrl(request)
                if (response.isSuccessful) {
                    Toast.makeText(this@LoseUrlChangeActivity, "URL이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    // chatName 값에 따라 이동할 Activity 결정
                    val intent = when (chatName) {
                        "렌트" -> Intent(this@LoseUrlChangeActivity, RentActivity::class.java)
                        "분실물" -> Intent(this@LoseUrlChangeActivity, LoseActivity::class.java)
                        else -> null
                    }
                    // Intent가 null이 아닐 때만 실행
                    intent?.let {
                        startActivity(it)
                    }

                    finish()
                } else {
                    Log.e("LoseUrlChangeActivity", "URL 변경 실패")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}