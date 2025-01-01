package com.example.gaechuck.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.models.SettingItem
import com.example.gaechuck.ui.setting.adapter.SettingAdapter

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_setting) // 이 레이아웃을 설정합니다

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_settings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val items = listOf(
            SettingItem(getString(R.string.setting_title_1), getString(R.string.setting_info_1), true, true),
            SettingItem(getString(R.string.setting_title_2), getString(R.string.setting_info_2), false, false)
        )


        recyclerView.adapter = SettingAdapter(items)
    }
}
