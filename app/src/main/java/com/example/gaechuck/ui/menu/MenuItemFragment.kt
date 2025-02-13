package com.example.gaechuck.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gaechuck.R
import com.example.gaechuck.data.response.FoodMenuItem // ✅ 올바른 데이터 모델 import

class MenuItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cafeteria_menu_item, container, false)
        val gridLayout = view.findViewById<GridLayout>(R.id.menuGridLayout)

        // ✅ `Parcelable`을 사용하여 `menuList` 가져오기
        val menuList = arguments?.getParcelableArrayList<FoodMenuItem>("menuList")

        menuList?.forEach { item ->
            val labelTextView = TextView(context)
            labelTextView.text = item.menuDivision
            labelTextView.setPadding(16, 8, 16, 8)

            val valueTextView = TextView(context)
            valueTextView.text = item.menu
            valueTextView.setPadding(16, 8, 16, 8)

            gridLayout.addView(labelTextView)
            gridLayout.addView(valueTextView)
        }

        return view
    }
}
