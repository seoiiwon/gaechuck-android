package com.example.gaechuck.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.FoodMenuItem // ✅ 올바른 데이터 모델 import
import com.example.gaechuck.ui.menu.adaptor.GridCafeteriaAdapter

class MenuItemFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private val adapter = GridCafeteriaAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cafeteria_menu_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.menuGridRecyclerView)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        recycler.adapter = adapter

        // arguments 에서 리스트 꺼내서 어댑터에 넘기기
        @Suppress("UNCHECKED_CAST")
        val list = arguments
            ?.getParcelableArrayList<FoodMenuItem>("menuList")
            ?.toList()
            ?: emptyList()

        adapter.submitList(list)
    }

    companion object {
        fun newInstance(menuList: List<FoodMenuItem>): MenuItemFragment {
            val frag = MenuItemFragment()
            val args = Bundle().apply {
                putParcelableArrayList("menuList", ArrayList(menuList))
            }
            frag.arguments = args
            return frag
        }
    }
}