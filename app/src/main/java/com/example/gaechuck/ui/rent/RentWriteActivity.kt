package com.example.gaechuck.ui.rent

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R
import com.example.gaechuck.databinding.ActivityRentWriteBinding

class RentWriteActivity : AppCompatActivity(R.layout.activity_rent_write) {
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView
    private lateinit var photoCountTextView: TextView
    private lateinit var binding: ActivityRentWriteBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // photo_count TextView 찾기
        photoCountTextView = binding.photoAddBtn.photoCount
        photoCountTextView.text = "3"

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}