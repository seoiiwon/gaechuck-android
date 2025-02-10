package com.example.gaechuck.ui.lose

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.databinding.ActivityLoseWriteBinding

class LoseWriteActivity : AppCompatActivity() {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var binding: ActivityLoseWriteBinding
    private lateinit var photoCountTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoseWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // photo_count TextView 찾기
        photoCountTextView = binding.photoAddBtn.photoCount
        photoCountTextView.text = "3"

        // Toolbar 설정
        toolbar = binding.toolbarMain
        backButton = binding.buttonBack
        sendButton = binding.formSend

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}