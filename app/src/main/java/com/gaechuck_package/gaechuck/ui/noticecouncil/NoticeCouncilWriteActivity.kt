package com.gaechuck_package.gaechuck.ui.noticecouncil


import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.repository.NoticeCouncilRepository
import com.gaechuck_package.gaechuck.ui.noticecouncil.adaptor.ImageAdapter
import com.gaechuck_package.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModel
import com.gaechuck_package.gaechuck.ui.noticecouncil.viewmodel.NoticeCouncilViewModelFactory
import com.gaechuck_package.gaechuck.ui.util.WriteDialogFragment


class NoticeCouncilWriteActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var bodyEditText: EditText
    private lateinit var addImageButton: ImageView
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var postButton: Button
    private val dialogFragment = WriteDialogFragment(this)

    private val imageList = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter

    private val viewModel: NoticeCouncilViewModel by viewModels {
        NoticeCouncilViewModelFactory(NoticeCouncilRepository())
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris?.let {
            imageList.addAll(it)
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

        findViewById<ImageView>(R.id.backBtn).setOnClickListener { finish() }

        viewModel.postResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "게시물이 등록되었습니다!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, NoticeCouncilActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                finish()
            }.onFailure {
                Toast.makeText(this, "작성 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, "오류: $it", Toast.LENGTH_SHORT).show()
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

    private fun postNotice() {
        val title = titleEditText.text.toString().trim()
        val body = bodyEditText.text.toString().trim()

        if (title.isEmpty() || body.isEmpty() || imageList.isEmpty()) {
            dialogFragment.show()
            return
        }

        viewModel.postNotice(title, body, imageList, applicationContext)
    }
}