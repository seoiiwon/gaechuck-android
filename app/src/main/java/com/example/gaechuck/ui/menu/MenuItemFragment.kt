package com.example.gaechuck.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gaechuck.R

class MenuItemFragment : Fragment() {

    private val menuItems = mapOf(
        1 to arrayOf(
            "주식" to "쌀밥",
            "국류" to "호박수제비국 청국장찌개",
            "찬류" to "닭가슴살야채볶음 갈비만두/초간장 미역줄기볶음",
            "후식" to "액티브요구르트"
        ),
        2 to arrayOf(
            "주식" to "잡곡밥",
            "국류" to "된장찌개 미역국",
            "찬류" to "제육볶음 김치전 고등어구이",
            "후식" to "바나나 요거트"
        ),
        3 to arrayOf(
            "주식" to "보리밥",
            "국류" to "순두부찌개 김치찌개",
            "찬류" to "소고기장조림 시금치나물 깍두기",
            "후식" to "배/사과"
        ),
        4 to arrayOf(
            "주식" to "흑미밥",
            "국류" to "북어국 콩나물국",
            "찬류" to "닭가슴살구이 감자조림 쌈장",
            "후식" to "젤리"
        ),
        5 to arrayOf(
            "주식" to "쌀밥",
            "국류" to "해물탕 미소된장국",
            "찬류" to "부추겉절이 오징어볶음 쌈채소",
            "후식" to "아이스크림"
        ),
        6 to arrayOf(
            "주식" to "비빔밥",
            "국류" to "시래기국 청국장",
            "찬류" to "돈까스 새우튀김 양배추샐러드",
            "후식" to "커피"
        ),
        7 to arrayOf(
            "주식" to "쌀밥",
            "국류" to "김치찌개 고기국",
            "찬류" to "불고기 조림 두부찜",
            "후식" to "커피 또는 차"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cafeteria_menu_item, container, false)

        val gridLayout = view.findViewById<GridLayout>(R.id.menuGridLayout)

        val selectedCafeteriaSeq = arguments?.getInt("cafeteriaSeq") ?: 1

        Log.d("MenuItemFragment", "onCreateView: Received cafeteriaSeq: $selectedCafeteriaSeq")

        val menuForSelectedCafeteria = menuItems[selectedCafeteriaSeq] ?: return view

        gridLayout.removeAllViews()

        for (item in menuForSelectedCafeteria) {
            val labelTextView = TextView(context)
            labelTextView.text = item.first
            labelTextView.gravity = android.view.Gravity.CENTER
            labelTextView.setPadding(16, 16, 16, 16)

            val valueTextView = TextView(context)
            valueTextView.text = item.second
            valueTextView.gravity = android.view.Gravity.CENTER
            valueTextView.setPadding(16, 16, 16, 16)

            gridLayout.addView(labelTextView)
            gridLayout.addView(valueTextView)
        }

        return view
    }

    fun setCafeteriaSeq(cafeteriaSeq: Int) {
        val args = Bundle()
        args.putInt("cafeteriaSeq", cafeteriaSeq)
        arguments = args

        Log.d("MenuItemFragment", "setCafeteriaSeq called with cafeteriaSeq: $cafeteriaSeq")
    }}
