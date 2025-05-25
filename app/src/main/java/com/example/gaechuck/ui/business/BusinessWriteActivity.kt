package com.example.gaechuck.ui.business

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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.databinding.ActivityBusinessWriteBinding
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.example.gaechuck.ui.util.ImageDialogFragment
import com.example.gaechuck.ui.util.ImageFragment
import com.example.gaechuck.ui.util.WriteDialogFragment
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
    private val dialogFragment = WriteDialogFragment(this)
    private val imageDialogFragment = ImageFragment(this)
    private var isSending = false


    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uris ->
        if (uris.isNotEmpty()) {
            Log.d("getContent", "Selected URIs: $uris")
            viewModel.addImages(uris)
        } else {
            Log.w("getContent", "No URIs selected")
        }
    }

    private fun updateSendButtonColor() {
        val titleFilled = binding.fieldTitle.text.toString().isNotBlank()
        val infoFilled = binding.fieldInfo.text.toString().isNotBlank()

        val isReady = titleFilled && infoFilled
        val colorRes = if (isReady) R.color.gnu_blue else R.color.gnu_grey

        sendButton.setTextColor(ContextCompat.getColor(this, colorRes))
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

        binding.textViewTitle.text = "제휴 글 작성하기"

        chipGroup = findViewById(R.id.group_category)
        chipGroup.isSelectionRequired = true

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        photoBtn = binding.photoAddBtn.root
        binding.photoAddBtn2.totalCount.text = "/2"

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

        binding.fieldTitle.addTextChangedListener { updateSendButtonColor() }
        binding.fieldInfo.addTextChangedListener { updateSendButtonColor() }

        sendButton.setOnClickListener {
            if (isSending) return@setOnClickListener // 중복 방지
            // 유효성 검사를 통과한 경우에만 전송 시작
            if (validateForm()) {
                isSending = true
                sendButton.isEnabled = false
                sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_grey))
                sendBusinessData()
            }
        }

        viewModel.postResult.observe(this) { result ->
            isSending = false
            sendButton.isEnabled = true
            updateSendButtonColor() // 원래 색상으로 복구

            result.onSuccess {
                Log.d("BusinessWriteActivity", "전송 성공: ${it.message}")
                Toast.makeText(this, "작성 완료", Toast.LENGTH_SHORT).show()
                finishAndGoToBusinessActivity() // 성공하면 이동
            }.onFailure { error ->
                Toast.makeText(this, "작성 실패", Toast.LENGTH_SHORT).show()
                Log.e("BusinessWriteActivity", "전송 실패: ${error.message}")
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

    private fun sendBusinessData() {
//        val token = "Bearer ${AuthManager.getToken()}" // 🔥 토큰 가져오기
        val coalitionName = binding.fieldTitle.text.toString()
        val benefit = binding.fieldInfo.text.toString()

        chipGroup = findViewById(R.id.group_category)
        val selectedCategoryChip = chipGroup.findViewById<View>(chipGroup.checkedChipId) as? Chip
        val category = selectedCategoryChip?.text.toString()

        if (coalitionName.isBlank() || benefit.isBlank() || category.isBlank()) {
            dialogFragment.show()
            return
        }

        Log.d("BusinessWriteActivity", "전송할 데이터: name=$coalitionName, benefit=$benefit, category=$category")

        val imageUris = viewModel.selectedImages.value ?: emptyList()
        if (imageUris.isEmpty()) {
            dialogFragment.show()
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.sendData(coalitionName, benefit, category, imageUris, applicationContext)
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

            imageView.setOnClickListener{
                val dialog = ImageDialogFragment.newInstance(uri.toString())
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
        binding.photoAddBtn2.root.visibility = if (images.isEmpty() || images.size == 2) View.GONE else View.VISIBLE

        binding.photoAddBtn2.photoCount.text = "${images.size}"

        binding.photoAddBtn2.root.setOnClickListener {
            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun validateForm(): Boolean {
        val title = binding.fieldTitle.text.toString()
        val info = binding.fieldInfo.text.toString()
        val selectedChip = chipGroup.findViewById<View>(chipGroup.checkedChipId) as? Chip
        val category = selectedChip?.text.toString()
        val imageUris = viewModel.selectedImages.value ?: emptyList()

        return if (title.isBlank() || info.isBlank() || category.isBlank()) {
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