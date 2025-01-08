package com.example.gaechuck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutId = intent.getIntExtra("layout_id", R.layout.sub1)
        setContentView(layoutId)
    }
}
