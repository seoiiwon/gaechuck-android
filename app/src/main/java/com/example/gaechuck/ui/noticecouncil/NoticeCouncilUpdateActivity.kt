package com.example.gaechuck.ui.noticecouncil

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.repository.NoticeCouncilRepository
import com.example.gaechuck.ui.noticecouncil.adaptor.ImageAdapter
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.example.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class NoticeCouncilUpdateActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var addImageButton: ImageView
    private lateinit var updateButton: Button
    private lateinit var imageAdapter: ImageAdapter

    private val viewModel: NoticeCouncilViewModel by viewModels {
        NoticeCouncilViewModelFactory(NoticeCouncilRepository())
    }
    private var noticeId: Int = -1
    private val selectedImages = mutableListOf<Uri>()
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris?.let {
            selectedImages.addAll(it)
            imageAdapter.notifyDataSetChanged()
        }
    }

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
        imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        imageRecyclerView.adapter = imageAdapter

        // 드래그 앤 드랍으로 구현
        val callback = SimpleItemTouchHelperCallback(imageAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(imageRecyclerView)

        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        updateButton.setOnClickListener {
            updateNotice()
        }

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }
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
        val title = titleEditText.text.toString().trim()
        val body = bodyEditText.text.toString().trim()
        val token = AuthManager.getToken()

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImages.isEmpty()) {
            Toast.makeText(this, "최소 하나 이상의 이미지를 선택하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val dataMap = mapOf(
                    "title" to title,
                    "body" to body
                )
                val dataJson = Gson().toJson(dataMap)
                val dataBody = dataJson.toRequestBody("application/json; charset=utf-8".toMediaType())

                // 모든 이미지에 MultipartBody.Part 생성 (기존 URL인 경우 다운로드하여 임시 파일로, 그 외는 그대로 변환)
                val imageParts = selectedImages.mapIndexedNotNull { index, uri ->
                    createImagePart(uri, this@NoticeCouncilUpdateActivity, index)
                }

                // PATCH 요청: data 파트와 files 파트 모두 전송
                val response = ApiConnection.getRetrofitService.updateNoticeCouncil(
                    noticeId = noticeId,
                    token = "Bearer $token",
                    data = dataBody,
                    files = imageParts
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@NoticeCouncilUpdateActivity, "공지사항이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@NoticeCouncilUpdateActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra("refreshNotice", true)
                    }
                    startActivity(intent)
                    finish()
                    finish()
                } else {
                    Log.e("UpdateNotice", "공지사항 수정 실패: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@NoticeCouncilUpdateActivity, "수정 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("UpdateNotice", "예외 발생: ${e.message}", e)
                Toast.makeText(this@NoticeCouncilUpdateActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun createImagePart(uri: Uri?, context: Context, index: Int): MultipartBody.Part? {
        uri ?: return null
        return withContext(Dispatchers.IO) {
            when {
                uri.toString().startsWith("http", ignoreCase = true) -> {
                    try {
                        // URL에서 이미지 다운로드
                        val url = URL(uri.toString())
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.doInput = true
                        connection.connect()

                        // 다운로드한 이미지 임시 파일에 저장
                        val file = File(context.cacheDir, "upload_image_$index.jpg")
                        connection.inputStream.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                        val requestFile = RequestBody.create("image/*".toMediaType(), file)
                        MultipartBody.Part.createFormData("file", file.name, requestFile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                uri.toString().startsWith("content", ignoreCase = true) || uri.toString().startsWith("file", ignoreCase = true) -> {
                    try {
                        val contentResolver = context.contentResolver
                        val inputStream = contentResolver.openInputStream(uri)
                        val file = File(context.cacheDir, "upload_image_$index.jpg")
                        inputStream?.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                        val requestFile = RequestBody.create("image/*".toMediaType(), file)
                        MultipartBody.Part.createFormData("file", file.name, requestFile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                else -> null
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchNotices()
    }

    class SimpleItemTouchHelperCallback(private val adapter: ImageAdapter) : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            // 수직 이동만 허용
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0 // 스와이프는 사용하지 않음
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition
            adapter.onItemMove(fromPos, toPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // 스와이프 시 삭제
        }

        // 드래그 시작, 종료 시 효과를 위해 (선택 사항)
        override fun isLongPressDragEnabled(): Boolean = true
        override fun isItemViewSwipeEnabled(): Boolean = false
    }
}