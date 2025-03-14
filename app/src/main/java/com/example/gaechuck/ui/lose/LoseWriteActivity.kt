package com.example.gaechuck.ui.lose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.databinding.ActivityLoseWriteBinding
import com.example.gaechuck.repository.LoseRepository
import com.example.gaechuck.ui.lose.viewmodel.LoseViewModel
import kotlinx.coroutines.launch

class LoseWriteActivity : AppCompatActivity() {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityLoseWriteBinding
    private lateinit var photoCountTextView: TextView
    private lateinit var photoBtn : View
    private lateinit var viewModel: LoseViewModel


    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addImages(uris)
        }else {
            Log.w("getContent", "No URIs selected")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoseWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

        // photo_count TextView 찾기
        photoBtn = binding.photoAddBtn.root
        photoCountTextView = photoBtn.findViewById(R.id.photo_count)
        photoCountTextView.text = "3"

        // Toolbar 설정
        toolbar = binding.toolbarMain
        backButton = binding.buttonBack
        sendButton = binding.formSend

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //photoBtn 클릭 > 포토피커 열기
        photoBtn.setOnClickListener {
            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedImages.collect { images ->
                    Log.d("Activity", "Observer received new images: $images")
                    updateUI(images)
                }
            }
        }

        sendButton.setOnClickListener {
            sendLoseData()
        }

        viewModel.postResult.observe(this) { result ->
            result.onSuccess {
                Log.d("LoseWriteActivity", "전송 성공: ${it.message}")
                Toast.makeText(this, "작성 완료", Toast.LENGTH_SHORT).show()
                finishAndGoToLoseActivity() // 성공하면 이동
            }.onFailure { error ->
                Log.e("LoseWriteActivity", "전송 실패: ${error.message}")
                Toast.makeText(this, "작성 실패", Toast.LENGTH_SHORT).show()

            }
        }

        // 날짜 변환
        val fieldDate = binding.fieldDate

        fieldDate.addTextChangedListener(object : TextWatcher {
            private var updating = false
            private var beforeText: String = "" // 이전 텍스트 저장
            private var cursorPosition: Int = 0

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                beforeText = p0.toString()
                cursorPosition = p1
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (updating) return

                updating = true
                var input = p0?.toString() ?: ""

                // 점(.) 제거
                input = input.replace(".", "")

                // 8자리 초과 입력 방지
                if (input.length > 8) {
                    input = input.substring(0, 8)
                }

                // 날짜 형식 변환
                var formattedDate = formatRawDate(input)

                // 텍스트가 변경되었으면 setText 호출
                if (formattedDate != beforeText) {
                    // setText() 호출 전에 커서 위치 계산
                    fieldDate.setText(formattedDate)

                    // 커서 위치 계산
                    val newPosition = if (cursorPosition < formattedDate.length) {
                        formattedDate.length
                    } else {
                        cursorPosition
                    }
                    fieldDate.setSelection(newPosition)
                }

                updating = false
            }
        })


    }

    private fun finishAndGoToLoseActivity() {
        val intent = Intent(this, LoseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun sendLoseData() {
        val token = "Bearer ${AuthManager.getToken()}" // 토큰 가져오기
        val title = binding.fieldTitle.text.toString()
        val lostDate = binding.fieldDate.text.toString()
        val description = binding.fieldInfo.text.toString()
        val lostLocation = binding.fieldLocation.text.toString()

        if (title.isBlank() || lostDate.isBlank() || description.isBlank() || lostLocation.isBlank()) {
            Log.e("sendBusinessData", "입력값이 부족합니다.")
            Toast.makeText(this, "모든 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("LoseWriteActivity", "전송할 데이터: name=$title, lostDate=$lostDate,description=$description, lostLocation=$lostLocation")

        val imageUris = viewModel.selectedImages.value
        if (imageUris.isEmpty()) {
            Log.e("sendLoseData", "이미지가 없습니다.")
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.sendData(token, title, lostDate, description, lostLocation,imageUris, applicationContext)
    }

    // 날짜 형식 변환 함수
    private fun formatRawDate(rawDate: String): String {
        var formatted = ""

        for (i in rawDate.indices) {
            formatted += rawDate[i]
            if (i == 3 || i == 5) {
                formatted += "."
            }
        }

        return formatted
    }

    private fun updateUI(images: List<Uri>) {
        // photoAddBtn 숨기기
        photoBtn.visibility = if(images.isEmpty()) View.VISIBLE else View.GONE
        binding.photoContainer.removeAllViews()

        // 사진 추가
        images.forEachIndexed { index, uri ->
            val photoView = layoutInflater.inflate(R.layout.fragment_photo_view, binding.photoContainer, false)

            val imageView = photoView.findViewById<ImageView>(R.id.photo_view)
            val deleteBtn = photoView.findViewById<ImageView>(R.id.delete_btn)

            // 이미지 설정
            imageView.setImageURI(uri)

            // 삭제 버튼 클릭 시 리스트에서 제거 후 UI 업데이트
            deleteBtn.setOnClickListener {
                viewModel.removeImages(index)  // ViewModel에서 이미지 제거
            }

            // 컨테이너에 추가
            binding.photoContainer.addView(photoView, 0)
        }

        // 개수 버튼과 추가 버튼 설정
        binding.photoAddBtn2.root.visibility = if (images.isEmpty()) View.GONE else View.VISIBLE
        binding.photoAddBtn2.photoCount.text = "${images.size}"

        binding.photoAddBtn2.root.setOnClickListener {
            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }


}