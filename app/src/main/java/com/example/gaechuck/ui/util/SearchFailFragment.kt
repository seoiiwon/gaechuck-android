package com.example.gaechuck.ui.util

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.gaechuck.R

class SearchFailFragment : Fragment(R.layout.fragment_search_fail){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.search_call_btn)?.setOnClickListener {
            // 문의하기 버튼 클릭 처리
        }
    }
}