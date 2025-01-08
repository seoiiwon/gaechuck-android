package com.example.gaechuck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Sub7Activity : AppCompatActivity() { // AppCompatActivity 상속
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub2) // sub2.xml 레이아웃
    }
}
