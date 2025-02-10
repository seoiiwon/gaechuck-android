package com.example.gaechuck.ui.business

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R
import com.example.gaechuck.databinding.ActivityBusinessWriteBinding
import com.google.android.material.chip.ChipGroup

class BusinessWriteActivity : AppCompatActivity(R.layout.activity_business_write) {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityBusinessWriteBinding
    private lateinit var photoBtn : View
    private val selectedImages = mutableListOf<Uri>()

    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages.addAll(uris)
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)

        val chipGroup = findViewById<ChipGroup>(R.id.group_category)
        chipGroup.isSelectionRequired = true

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        photoBtn = binding.photoAddBtn.root

        //photoBtn 클릭 > 포토피커 열기
        photoBtn.setOnClickListener {
            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
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
//        binding.photoAddBtn2.root.visibility = if (selectedImages.isEmpty()) View.GONE else View.VISIBLE
//        binding.photoAddBtn2.photoCount.text = "${selectedImages.size}"
//
//        binding.photoAddBtn2.root.setOnClickListener {
//            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        }

    }
}