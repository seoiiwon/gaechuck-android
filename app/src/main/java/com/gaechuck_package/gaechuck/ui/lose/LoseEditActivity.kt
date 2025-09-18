package com.gaechuck_package.gaechuck.ui.lose

import android.app.DatePickerDialog
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
import com.gaechuck_package.gaechuck.databinding.ActivityLoseWriteBinding
import com.gaechuck_package.gaechuck.repository.LoseRepository
import com.gaechuck_package.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.gaechuck_package.gaechuck.ui.util.ImageFragment
import com.gaechuck_package.gaechuck.ui.util.WriteDialogFragment
import com.gaechuck_package.gaechuck.ui.util.ZoomImageDialogFragment
import kotlinx.coroutines.launch
import java.util.Calendar

class LoseEditActivity : AppCompatActivity(R.layout.activity_lose_write) {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var backButton: ImageView
    private lateinit var sendButton: TextView
    private lateinit var photoCountTextView: TextView
    private lateinit var binding: ActivityLoseWriteBinding
    private lateinit var photoBtn: View
    private lateinit var viewModel: LoseViewModel
    private val dialogFragment = WriteDialogFragment(this)
    private val imageDialogFragment = ImageFragment(this)
    private var isSending = false


    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.addImages(uris)
            } else {
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
        photoCountTextView = binding.photoAddBtn.photoCount
        photoCountTextView.text = "3"

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)
        binding.textViewTitle.text = "수정하기"


        // LoseActivity에 전달된 데이터 받기
        val loseItemId = intent.getIntExtra("lostItemId", -1)
        val title = intent.getStringExtra("title") ?: ""
        val lostDate = intent.getStringExtra("lostDate") ?: ""
        val lostLocation = intent.getStringExtra("lostLocation") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val LostImages = intent.getStringArrayListExtra("images") ?: arrayListOf()


        // UI 설정
        findViewById<TextView>(R.id.field_title).text = title
        findViewById<TextView>(R.id.field_date).text = lostDate
        findViewById<TextView>(R.id.field_location).text = lostLocation
        findViewById<TextView>(R.id.field_info).text = description

        lifecycleScope.launch {
            viewModel.selectedImages.collect { images ->
                updateUI(images)
            }
        }

        // 변수명 수정
        val imageUris = LostImages.map { Uri.parse(it) }
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

        binding.fieldDate.setOnClickListener {

            val cal = Calendar.getInstance()

            // 기존 lostDate 값이 있을 경우 캘린더에 설정
            if (lostDate.isNotBlank()) {
                val parts = lostDate.split(".") // "yyyy.MM.dd" 형식 가정
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt() - 1 // Calendar.MONTH는 0부터 시작
                    val day = parts[2].toInt()
                    cal.set(year, month, day)
                }
            }

            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val monthText = if (month < 10) "0$month" else "$month"
                val dayText = if (day < 10) "0$day" else "$day"
                binding.fieldDate.text = "${year}.${monthText}.${dayText}"
            }

            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                Calendar.DAY_OF_MONTH)).show()
        }

        sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_blue))

        sendButton.setOnClickListener {
            if (isSending) return@setOnClickListener // 중복 방지
            // 유효성 검사를 통과한 경우에만 전송 시작
            if (validateForm()) {
                isSending = true
                sendButton.isEnabled = false
                sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_grey))
                patchRentData(loseItemId)
            }

        }

        viewModel.patchResult.observe(this) { result ->
            isSending = false
            sendButton.isEnabled = true
            sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_grey))

            result.onSuccess {
                Log.d("LoseEditActivity", "전송 성공: ${it.message}")
                Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show()
                finishAndGoToRentActivity() // 성공하면 이동
            }.onFailure { error ->
                Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
                Log.e("LoseEditActivity", "전송 실패: ${error.message}")
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
        val intent = Intent(this, LoseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun patchRentData(loseItemId: Int) {
//        val token = "Bearer ${AuthManager.getToken()}" // 🔥 토큰 가져오기
        val title = binding.fieldTitle.text.toString()
        val lostDate = binding.fieldDate.text.toString()
        val description = binding.fieldInfo.text.toString()
        val lostLocation = binding.fieldLocation.text.toString()

        if (title.isBlank() || lostDate.isBlank() || description.isBlank() || lostLocation.isBlank()) {
            dialogFragment.show()
            return
        }

        Log.d(
            "LoseWriteActivity",
            "전송할 데이터: name=$title, lostDate=$lostDate,description=$description, lostLocation=$lostLocation"
        )

        val imageUris = viewModel.selectedImages.value
        Log.d("LoseEditActivity", "전송이미지 ${imageUris}")
        if (imageUris.isEmpty()) {
            imageDialogFragment.show()
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.patchData(
//            token,
            loseItemId,
            title,
            lostDate,
            description,
            lostLocation,
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
        val location = binding.fieldLocation.text.toString()
        val lostDate = binding.fieldDate.text.toString()
        val description = binding.fieldInfo.text.toString()
        val imageUris = viewModel.selectedImages.value

        return if (title.isBlank() || location.isBlank() || lostDate.isBlank() || description.isBlank()) {
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
