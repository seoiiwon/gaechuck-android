package com.example.gaechuck.ui.foundcenter

import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R

class FoundCenterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_found_center)

        // GridView 초기화
        val gridView: GridView = findViewById(R.id.gridview)

        // 아이템 데이터 생성
        val gridItems = listOf(
            GridItem("나이키 신발", "2024.11.11"),
            GridItem("교통카드", "2024.11.11"),
            GridItem("지갑", "2024.11.14"),
            GridItem("고양이 키링", "2024.11.14"),
            GridItem("담배", "2024.11.15"),
            GridItem("카메라", "2024.11.15"),
            GridItem("강아지 인형", "2024.11.14"),
            GridItem("아이패드", "2024.11.16"),
            GridItem("에어팟", "2024.11.28")
        )

        // 어댑터 설정
        val adapter = GridAdapter(this, gridItems)
        gridView.adapter = adapter

        // 아이템 클릭 이벤트 처리
        gridView.setOnItemClickListener { _, _, position, _ ->
            val item = gridItems[position]
            Toast.makeText(this, "${item.title} 클릭됨!", Toast.LENGTH_SHORT).show()
        }
    }
}