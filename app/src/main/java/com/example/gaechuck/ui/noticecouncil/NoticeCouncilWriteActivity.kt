package com.example.gaechuck.ui.noticecouncil

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.data.request.NoticeCouncilRequest
import com.example.gaechuck.ui.noticecouncil.adaptor.ImageAdapter
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class NoticeCouncilWriteActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var addImageButton: ImageView
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var postButton: Button

    private val imageList = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageList.add(it)
            imageAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_council_write)

        titleEditText = findViewById(R.id.titleEditText)
        bodyEditText = findViewById(R.id.bodyEditText)
        addImageButton = findViewById(R.id.addImageButton)
        imageRecyclerView = findViewById(R.id.imageRecyclerView)
        postButton = findViewById(R.id.postButton)

        imageAdapter = ImageAdapter(imageList) { position ->
            imageList.removeAt(position)
            imageAdapter.notifyDataSetChanged()
        }
        imageRecyclerView.layoutManager = LinearLayoutManager(this)
        imageRecyclerView.adapter = imageAdapter

        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        postButton.setOnClickListener {
            postNotice()
        }
    }

    private fun postNotice() {
        val title = titleEditText.text.toString().trim()
        val body = bodyEditText.text.toString().trim()

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val authToken = AuthManager.getToken() // 🔥 토큰 가져오기
        if (authToken.isNullOrEmpty()) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val requestBody = createJsonRequestBody(NoticeCouncilRequest(title, body))
                val imagePart = createImagePart(imageList.firstOrNull(), this@NoticeCouncilWriteActivity)

                val response = ApiConnection.getRetrofitService.postNoticeCouncil(
                    authToken = "Bearer $authToken", // ✅ 실제 토큰 적용
                    data = requestBody,
                    file = imagePart
                )

                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Toast.makeText(this@NoticeCouncilWriteActivity, "게시물이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("PostNotice", "게시 실패: ${response.body()?.message}")
                    Toast.makeText(this@NoticeCouncilWriteActivity, "게시 실패: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PostNotice", "오류 발생: ${e.message}")
                Toast.makeText(this@NoticeCouncilWriteActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createJsonRequestBody(request: NoticeCouncilRequest): RequestBody {
        val json = Gson().toJson(request)
        return RequestBody.create("application/json".toMediaType(), json)
    }

    private fun createImagePart(uri: Uri?, context: Context): MultipartBody.Part? {
        uri ?: return null

        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_image.jpg") // 임시 파일 생성

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = RequestBody.create("image/*".toMediaType(), file)
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

}
