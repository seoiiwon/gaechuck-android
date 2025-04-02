package com.example.gaechuck.ui.business

import android.app.ActivityOptions
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.databinding.ActivityBusinessWriteBinding
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.example.gaechuck.ui.lose.LoseActivity
import com.example.gaechuck.ui.util.ImageDialogFragment
import com.example.gaechuck.ui.util.WriteDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class BusinessEditActivity : AppCompatActivity(R.layout.activity_business_write) {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var backButton: ImageView
    private lateinit var sendButton: TextView
    private lateinit var binding: ActivityBusinessWriteBinding
    private lateinit var photoBtn: View
    private lateinit var viewModel: BusinessViewModel
    private lateinit var chipGroup : ChipGroup
    private val dialogFragment = WriteDialogFragment(this)


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

        // photo_count TextView 찾기
        photoBtn = binding.photoAddBtn.root

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)

        chipGroup = findViewById(R.id.group_category)
        chipGroup.isSelectionRequired = true

        // LoseActivity에 전달된 데이터 받기
        val coalitionId = intent.getIntExtra("coalitionId", -1)
        val coalitionName = intent.getStringExtra("coalitionName") ?: ""
        val benefit = intent.getStringExtra("benefit") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val BusinessImages = intent.getStringArrayListExtra("images") ?: arrayListOf()


        // UI 설정
        findViewById<TextView>(R.id.field_title).text = coalitionName
        findViewById<TextView>(R.id.field_info).text = benefit
        // ChipGroup 내에서 category 값과 일치하는 Chip 찾기
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.text.toString() == category) {
                if (chip != null) {
                    chip.isChecked = true
                }
                break
            }
        }
        binding.photoAddBtn2.totalCount.text = "/2"

        lifecycleScope.launch {
            viewModel.selectedImages.collect { images ->
                updateUI(images)
            }
        }

        // 변수명 수정
        val imageUris = BusinessImages.map { Uri.parse(it) }
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

        sendButton.setOnClickListener {
            patchRentData(coalitionId)
        }

        viewModel.patchResult.observe(this) { result ->
            result.onSuccess {
                Log.d("BusinessEditActivity", "전송 성공: ${it.message}")
                Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show()
                finishAndGoToRentActivity() // 성공하면 이동
            }.onFailure { error ->
                Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
                Log.e("BusinessEditActivity", "전송 실패: ${error.message}")
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
        val intent = Intent(this, BusinessActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun patchRentData(loseItemId: Int) {
        val token = "Bearer ${AuthManager.getToken()}" // 🔥 토큰 가져오기
        val coalitionName = binding.fieldTitle.text.toString()
        val benefit = binding.fieldInfo.text.toString()

        chipGroup = findViewById(R.id.group_category)
        val selectedCategoryChip = chipGroup.findViewById<View>(chipGroup.checkedChipId) as? Chip
        val category = selectedCategoryChip?.text.toString()

        if (coalitionName.isBlank() || benefit.isBlank() || category.isBlank()) {
            dialogFragment.show()
            return
        }

        Log.d("BusinessEditActivity", "전송할 데이터: name=$coalitionName, benefit=$benefit, category=$category")

        val imageUris = viewModel.selectedImages.value ?: emptyList()
        if (imageUris.isEmpty()) {
            dialogFragment.show()
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.patchData(
            token,
            loseItemId,
            coalitionName,
            benefit,
            category,
            imageUris,
            applicationContext
        )
    }


    private fun updateUI(images: List<Uri>) {
        // photoAddBtn 숨기기
        photoBtn.visibility = if (images.isEmpty()) View.VISIBLE else View.GONE
        binding.photoContainer.removeAllViews()

        // 사진 추가
        images.forEachIndexed { index, uri ->
            val photoView =
                layoutInflater.inflate(R.layout.fragment_photo_view, binding.photoContainer, false)
            val imageView = photoView.findViewById<ImageView>(R.id.photo_view)
            val deleteBtn = photoView.findViewById<ImageView>(R.id.delete_btn)

            // Glide를 사용하여 이미지 로드
            Glide.with(this)
                .load(uri.toString())  // 원격 이미지 URL
                .into(imageView)  // 이미지 뷰에 로드된 이미지 설정

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
        binding.photoAddBtn2.root.visibility = if (images.isEmpty()) View.GONE else View.VISIBLE
        binding.photoAddBtn2.photoCount.text = "${images.size}"

        binding.photoAddBtn2.root.setOnClickListener {
            getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    }
}
