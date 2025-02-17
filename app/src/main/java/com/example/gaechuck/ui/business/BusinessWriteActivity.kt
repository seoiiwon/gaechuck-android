package com.example.gaechuck.ui.business

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gaechuck.R
import com.example.gaechuck.databinding.ActivityBusinessWriteBinding
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class BusinessWriteActivity : AppCompatActivity(R.layout.activity_business_write) {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityBusinessWriteBinding
    private lateinit var photoBtn : View
    private lateinit var viewModel: BusinessViewModel
    private lateinit var chipGroup : ChipGroup


    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uris ->
        if (uris.isNotEmpty()) {
            Log.d("getContent", "Selected URIs: $uris")
            viewModel.addImages(uris)  // ViewModel에 직접 이미지 추가
        } else {
            Log.w("getContent", "No URIs selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        val repository = BusinessRepository()
        val viewModelFactory = BusinessViewModel.BusinessViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BusinessViewModel::class.java)

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)

        chipGroup = findViewById(R.id.group_category)
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedImages.collect { images ->
                    Log.d("Activity", "Observer received new images: $images")
                    updateUI(images)
                }
            }
        }

        sendButton.setOnClickListener {
            sendBusinessData()
        }

        viewModel.postResult.observe(this) { result ->
            result.onSuccess {
                Log.d("BusinessWriteActivity", "전송 성공: ${it.message}")
                finishAndGoToBusinessActivity() // 성공하면 이동
            }.onFailure { error ->
                Log.e("BusinessWriteActivity", "전송 실패: ${error.message}")
            }
        }

    }

    private fun sendBusinessData() {
        val token = "Bearer ${com.example.gaechuck.api.AuthManager.getToken()}" // 토큰 가져오기
        val coalitionName = binding.fieldTitle.text.toString()
        val benefit = binding.fieldInfo.text.toString()

        chipGroup = findViewById(R.id.group_category)
        val selectedCategoryChip = chipGroup.findViewById<View>(chipGroup.checkedChipId) as? Chip
        val category = selectedCategoryChip?.text.toString()

        Log.d("BusinessWriteActivity", "전송할 데이터: name=$coalitionName, benefit=$benefit, category=$category")

        val imageUris = viewModel.selectedImages.value ?: emptyList()
        if (imageUris.isEmpty()) {
            Log.e("sendBusinessData", "이미지가 없습니다.")
            return
        }

        if (coalitionName.isBlank() || benefit.isBlank() || category.isBlank()) {
            Log.e("sendBusinessData", "입력값이 부족합니다.")
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.sendData(token, coalitionName, benefit, category, imageUris, applicationContext)
    }

    private fun finishAndGoToBusinessActivity() {
        val intent = Intent(this, BusinessActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // 현재 액티비티 종료
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
//                if (index < (viewModel.selectedImages.value?.size ?: 0)) {
//                    Log.d("updateUI", "이미지 삭제 요청: index=$index")
//                    viewModel.removeImages(index)
//                } else {
//                    Log.e("updateUI", "삭제할 이미지가 존재하지 않습니다: index=$index")
//                }
                viewModel.removeImages(index)  // ViewModel에서 이미지 제거

            }

            // 컨테이너에 추가
            binding.photoContainer.addView(photoView, 0)
        }
    }
}