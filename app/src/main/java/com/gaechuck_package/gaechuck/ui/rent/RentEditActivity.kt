package com.gaechuck_package.gaechuck.ui.rent

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.ActivityRentWriteBinding
import com.gaechuck_package.gaechuck.repository.RentRepository
import com.gaechuck_package.gaechuck.ui.rent.viewmodel.RentViewModel
import com.gaechuck_package.gaechuck.ui.util.ImageFragment
import com.gaechuck_package.gaechuck.ui.util.WriteDialogFragment
import com.gaechuck_package.gaechuck.ui.util.ZoomImageDialogFragment
import kotlinx.coroutines.launch

class RentEditActivity : AppCompatActivity(R.layout.activity_rent_write) {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var photoCountTextView: TextView
    private lateinit var binding: ActivityRentWriteBinding
    private lateinit var photoBtn : View
    private lateinit var viewModel: RentViewModel
    private val dialogFragment = WriteDialogFragment(this)
    private val imageDialogFragment = ImageFragment(this)
    private var isSending = false



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
        binding.textViewTitle.text = "수정하기"

        // RentActivity에서 전달된 데이터 받기
        val rentItemId = intent.getIntExtra("rentItemId", -1)
        val rentItemName = intent.getStringExtra("rentItemName") ?: ""
        val rentItemCount = intent.getIntExtra("rentItemCount", 0)
        val rentItemImages = intent.getStringArrayListExtra("rentItemImage") ?: arrayListOf()

        // UI 설정
        findViewById<TextView>(R.id.field_title).text = rentItemName
        findViewById<TextView>(R.id.field_count).text = rentItemCount.toString()

        lifecycleScope.launch {
            viewModel.selectedImages.collect { images ->
                updateUI(images)
            }
        }

        val imageUris = rentItemImages.map { Uri.parse(it) }
        viewModel.addImages(imageUris)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //photoBtn 클릭 > 포토피커 열기
        photoBtn.setOnClickListener {
            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_blue))
        sendButton.setOnClickListener {
            if (isSending) return@setOnClickListener // 중복 방지
            // 유효성 검사를 통과한 경우에만 전송 시작
            if (validateForm()) {
                isSending = true
                sendButton.isEnabled = false
                sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_grey))
                patchRentData(rentItemId)
            }
        }

        // PatchResult로 바꾸기
        viewModel.patchResult.observe(this) { result ->
            isSending = false
            sendButton.isEnabled = true
            sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_grey))

            result.onSuccess {
                Log.d("RentEditActivity", "전송 성공: ${it.message}")
                Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show()
                finishAndGoToRentActivity() // 성공하면 이동
            }.onFailure { error ->
                Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
                Log.e("RentEditActivity", "전송 실패: ${error.message}")
            }
        }
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

    private fun finishAndGoToRentActivity() {
        val intent = Intent(this, RentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun patchRentData(rentItemId:Int) {
//        val token = "Bearer ${AuthManager.getToken()}" // 🔥 토큰 가져오기
        val rentItemName = binding.fieldTitle.text.toString()
        val rentItemCount = binding.fieldCount.text.toString()

        if (rentItemName.isBlank() || rentItemCount.isBlank()) {
            dialogFragment.show()
            return
        }

        val imageUris = viewModel.selectedImages.value
        if (imageUris.isEmpty()) {
            imageDialogFragment.show()
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.patchData(rentItemId, rentItemName, rentItemCount.toInt(), imageUris, applicationContext)
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

            // Glide를 사용하여 이미지 로드
            Glide.with(this)
                .load(uri.toString())  // 원격 이미지 URL
                .into(imageView)  // 이미지 뷰에 로드된 이미지 설정

            imageView.setOnClickListener{
                val dialog = ZoomImageDialogFragment(uri.toString())
                dialog.show(supportFragmentManager, "ImageDialog")
            }

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

    private fun validateForm(): Boolean {
        val title = binding.fieldTitle.text.toString()
        val count = binding.fieldCount.text.toString()
        val imageUris = viewModel.selectedImages.value

        return if (title.isBlank() || count.isBlank()) {
            if(imageUris.isEmpty()) {
                imageDialogFragment.show()
            } else {
                dialogFragment.show()
            }
            false
        } else {
            true
        }
    }

}