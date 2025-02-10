package com.example.gaechuck.ui.lose

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R
import com.example.gaechuck.databinding.ActivityLoseWriteBinding

class LoseWriteActivity : AppCompatActivity() {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityLoseWriteBinding
    private lateinit var photoCountTextView: TextView
    private lateinit var photoBtn : View
    private val selectedImages = mutableListOf<Uri>()

    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages.addAll(uris)
            updateUI()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoseWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun updateUI() {
        // photoAddBtn 숨기기
        photoBtn.visibility = if(selectedImages.isEmpty()) View.VISIBLE else View.GONE

        binding.photoContainer.removeAllViews()

        // 사진 추가
        selectedImages.forEachIndexed { index, uri ->
            val photoView = layoutInflater.inflate(R.layout.fragment_photo_view, binding.photoContainer, false)

            val imageView = photoView.findViewById<ImageView>(R.id.photo_view)
            val deleteBtn = photoView.findViewById<ImageView>(R.id.delete_btn)

            // 이미지 설정
            imageView.setImageURI(uri)

            // 삭제 버튼 클릭 시 리스트에서 제거 후 UI 업데이트
            deleteBtn.setOnClickListener {
                selectedImages.removeAt(index)
                updateUI()
            }

            // 컨테이너에 추가
            binding.photoContainer.addView(photoView, 0)
        }

        // 개수 버튼과 추가 버튼 설정
        binding.photoAddBtn2.root.visibility = if (selectedImages.isEmpty()) View.GONE else View.VISIBLE
        binding.photoAddBtn2.photoCount.text = "${selectedImages.size}"

        binding.photoAddBtn2.root.setOnClickListener {
            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }


}