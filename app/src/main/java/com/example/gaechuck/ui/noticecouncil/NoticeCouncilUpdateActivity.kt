package com.example.gaechuck.ui.noticecouncil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.ui.noticecouncil.adaptor.ImageAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class NoticeCouncilUpdateActivity : AppCompatActivity() {

    private val viewModel: NoticeCouncilViewModel by viewModels()
    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var addImageButton: ImageView
    private lateinit var updateButton: Button

    private var noticeId: Int = -1
    private lateinit var imageAdapter: ImageAdapter
    private val selectedImages = mutableListOf<Uri>() // 기존 및 추가된 이미지 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council_write)

        titleEditText = findViewById(R.id.titleEditText)
        bodyEditText = findViewById(R.id.bodyEditText)
        imageRecyclerView = findViewById(R.id.imageRecyclerView)
        addImageButton = findViewById(R.id.addImageButton)
        updateButton = findViewById(R.id.postButton)

        noticeId = intent.getIntExtra("notice_id", -1)

        if (noticeId != -1) {
            loadNoticeDetails(noticeId)
        }

        imageAdapter = ImageAdapter(selectedImages) { position ->
            selectedImages.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }
        imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.adapter = imageAdapter

        addImageButton.setOnClickListener {
            selectImageFromGallery()
        }

        updateButton.setOnClickListener {
            updateNotice()
        }
    }

    private fun loadNoticeDetails(noticeId: Int) {
        lifecycleScope.launch {
            try {
                val noticeDetail = withContext(Dispatchers.IO) {
                    viewModel.getNoticeDetail(noticeId)
                }

                if (noticeDetail != null) {
                    titleEditText.setText(noticeDetail.title)
                    bodyEditText.setText(noticeDetail.body)

                    // 기존 이미지 URL을 Uri로 변환하여 추가
                    selectedImages.clear()
                    selectedImages.addAll(noticeDetail.images?.map { Uri.parse(it) } ?: emptyList())
                    imageAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("UpdateNotice", "공지사항 불러오기 실패: ${e.message}")
            }
        }
    }

    private fun updateNotice() {
        val title = titleEditText.text.toString()
        val body = bodyEditText.text.toString()
        val token = AuthManager.getToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "인증 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val requestBody = mapOf(
                    "title" to title.toRequestBody(),
                    "body" to body.toRequestBody()
                )

                val imageParts = selectedImages.map { imageUri ->
                    val file = contentResolver.openInputStream(imageUri) ?: return@map null
                    val requestFile = file.readBytes().toRequestBody()
                    MultipartBody.Part.createFormData("images", imageUri.lastPathSegment, requestFile)
                }.filterNotNull()

                val response = withContext(Dispatchers.IO) {
                    ApiConnection.getRetrofitService.updateNoticeCouncil(
                        noticeId, "Bearer $token", requestBody, imageParts
                    )
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@NoticeCouncilUpdateActivity, "공지사항이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("UpdateNotice", "공지사항 수정 실패: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@NoticeCouncilUpdateActivity, "수정 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("UpdateNotice", "예외 발생: ${e.message}")
                Toast.makeText(this@NoticeCouncilUpdateActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        resultLauncher.launch(galleryIntent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                result.data?.data?.let { uri ->
                    selectedImages.add(uri)
                    imageAdapter.notifyItemInserted(selectedImages.size - 1)
                }
            }
        }
}