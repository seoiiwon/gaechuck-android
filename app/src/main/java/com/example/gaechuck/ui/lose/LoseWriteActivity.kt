package com.example.gaechuck.ui.lose

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
import com.example.gaechuck.databinding.ActivityLoseWriteBinding
import com.example.gaechuck.repository.LoseRepository
import com.example.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.example.gaechuck.ui.util.ImageDialogFragment
import com.example.gaechuck.ui.util.ImageFragment
import com.example.gaechuck.ui.util.WriteDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.util.Calendar

class LoseWriteActivity : AppCompatActivity() {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityLoseWriteBinding
    private lateinit var photoCountTextView: TextView
    private lateinit var photoBtn : View
    private lateinit var viewModel: LoseViewModel
    private val dialogFragment = WriteDialogFragment(this)
    private val imageDialogFragment = ImageFragment(this)
    private var isSending = false


    // 갤러리에서 여러 개의 이미지를 선택하는 ActivityResult
    private val getContent = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addImages(uris)
        }else {
            Log.w("getContent", "No URIs selected")
        }
    }

    private fun updateSendButtonColor() {
        val titleFilled = binding.fieldTitle.text.toString().isNotBlank()
        val infoFilled = binding.fieldInfo.text.toString().isNotBlank()
        val locationFilled = binding.fieldLocation.text.toString().isNotBlank()

        val isReady = titleFilled && infoFilled && locationFilled
        val colorRes = if (isReady) R.color.gnu_blue else R.color.gnu_grey

        sendButton.setTextColor(ContextCompat.getColor(this, colorRes))
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
        binding.textViewTitle.text = "분실물 글 작성하기"


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

        binding.fieldDate.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("날짜 선택")
                .setTheme(R.style.CustomDatePickerTheme) // ✅ 테마 적용
                .build()

            builder.addOnPositiveButtonClickListener { selection ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selection

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1  // 월은 0부터 시작해서 +1 필요
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val monthText = if (month < 10) "0$month" else "$month"
                val dayText = if (day < 10) "0$day" else "$day"

                binding.fieldDate.text = "$year.$monthText.$dayText"
            }

            builder.show(supportFragmentManager, "datePicker")
        }

        binding.fieldTitle.addTextChangedListener { updateSendButtonColor() }
        binding.fieldInfo.addTextChangedListener { updateSendButtonColor() }
        binding.fieldLocation.addTextChangedListener { updateSendButtonColor() }

        sendButton.setOnClickListener {
            if (isSending) return@setOnClickListener // 중복 방지
            // 유효성 검사를 통과한 경우에만 전송 시작
            if (validateForm()) {
                isSending = true
                sendButton.isEnabled = false
                sendButton.setTextColor(ContextCompat.getColor(this, R.color.gnu_grey))
                sendLoseData()
            }
        }

        viewModel.postResult.observe(this) { result ->
            isSending = false
            sendButton.isEnabled = true
            updateSendButtonColor() // 원래 색상으로 복구

            result.onSuccess {
                Log.d("LoseWriteActivity", "전송 성공: ${it.message}")
                Toast.makeText(this, "작성 완료", Toast.LENGTH_SHORT).show()
                finishAndGoToLoseActivity() // 성공하면 이동
            }.onFailure { error ->
                Log.e("LoseWriteActivity", "전송 실패: ${error.message}")
                Toast.makeText(this, "작성 실패", Toast.LENGTH_SHORT).show()

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

    private fun finishAndGoToLoseActivity() {
        val intent = Intent(this, LoseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun sendLoseData() {
//        val token = "Bearer ${AuthManager.getToken()}" // 토큰 가져오기
        val title = binding.fieldTitle.text.toString()
        val lostDate = binding.fieldDate.text.toString()
        val description = binding.fieldInfo.text.toString()
        val lostLocation = binding.fieldLocation.text.toString()

        if (title.isBlank() || lostDate.isBlank() || description.isBlank() || lostLocation.isBlank()) {
            dialogFragment.show()
            return
        }

        Log.d("LoseWriteActivity", "전송할 데이터: name=$title, lostDate=$lostDate,description=$description, lostLocation=$lostLocation")

        val imageUris = viewModel.selectedImages.value
        if (imageUris.isEmpty()) {
            imageDialogFragment.show()
            return
        }

        sendButton.isEnabled = false // 로딩 중 비활성화
        viewModel.sendData(title, lostDate, description, lostLocation,imageUris, applicationContext)
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

            imageView.setOnClickListener {
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
//        binding.photoAddBtn2.root.visibility = if (images.isEmpty() || images.size == 3) View.GONE else View.VISIBLE
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
        val imageUris = viewModel.selectedImages.value ?: emptyList()

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