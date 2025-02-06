package com.example.gaechuck.ui.business

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R
import com.google.android.material.chip.ChipGroup

class BusinessWriteActivity : AppCompatActivity(R.layout.activity_business_write) {

    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var backButton : ImageView
    private lateinit var sendButton : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        backButton = toolbar.findViewById(R.id.button_back)
        sendButton = toolbar.findViewById(R.id.form_send)

        val chipGroup = findViewById<ChipGroup>(R.id.group_category)
        chipGroup.isSelectionRequired = true

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}