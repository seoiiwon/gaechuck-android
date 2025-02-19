package com.example.gaechuck.ui.rent

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
import com.example.gaechuck.databinding.ActivityRentWriteBinding
import com.example.gaechuck.repository.RentRepository
import com.example.gaechuck.ui.rent.viewmodel.RentViewModel
import kotlinx.coroutines.launch

class RentWriteActivity : AppCompatActivity(R.layout.activity_rent_write) {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var photoCountTextView: TextView
    private lateinit var binding: ActivityRentWriteBinding
    private lateinit var photoBtn : View
    private lateinit var viewModel: RentViewModel


    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addImages(uris)
        } else {
            Log.w("getContent", "No URIs selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        val repository = RentRepository()
        val viewModelFactory = RentViewModel.RentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RentViewModel::class.java)

        // photo_count TextView 찾기
        photoBtn = binding.photoAddBtn.root
        photoCountTextView = binding.photoAddBtn.photoCount
        photoCountTextView.text = "3"

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
            sendRentData()
        }

        viewModel.postResult.observe(this) { result ->
            result.onSuccess {
                Log.d("RentWriteActivity", "전송 성공: ${it.message}")
                finishAndGoToRentActivity() // 성공하면 이동
            }.onFailure { error ->
                Log.e("RentWriteActivity", "전송 실패: ${error.message}")
            }
        }
    }

    private fun finishAndGoToRentActivity() {
        val intent = Intent(this, RentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun sendRentData() {
        val token = "Bearer ${com.example.gaechuck.api.AuthManager.getToken()}" // 토큰 가져오기
        val rentItemName = binding.fieldTitle.text.toString()
        val rentItemCount = binding.fieldCount.text.toString()

        Log.d("RentWriteActivity", "전송할 데이터: name=$rentItemName, count=$rentItemCount")

        val imageUris = viewModel.selectedImages.value
        if (imageUris.isEmpty()) {
            Log.e("sendRentData", "이미지가 없습니다.")
            return
        }

        if (rentItemName.isBlank() || rentItemCount.isBlank()) {
            Log.e("sendBusinessData", "입력값이 부족합니다.")
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.sendData(token, rentItemName, rentItemCount.toInt(), imageUris, applicationContext)
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