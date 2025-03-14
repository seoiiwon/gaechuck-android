package com.example.gaechuck.ui.menu.adaptor

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.FoodMenuItem

class GridCafeteriaAdapter(
    private var menuList: List<FoodMenuItem>,
) :
    RecyclerView.Adapter<GridCafeteriaAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val menuDivisionTextView: TextView = view.findViewById(R.id.menuDivisionTextView)
        val menuTextView: TextView = view.findViewById(R.id.menuTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_grid, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]

        when (menuItem.menuSeq) {
            -2 -> { // 카테고리
                holder.menuDivisionTextView.text = menuItem.menuDivision
                holder.menuDivisionTextView.setTextColor(Color.parseColor("#005BAC")) // GNU 블루 적용
                holder.menuDivisionTextView.textSize = 16f
                holder.menuDivisionTextView.setTypeface(null, Typeface.BOLD)
                holder.menuDivisionTextView.visibility = View.VISIBLE

                holder.menuTextView.text = ""
                holder.menuTextView.visibility = View.INVISIBLE
            }

            -1 -> { // "식단 정보가 없습니다."
                holder.menuDivisionTextView.text = ""
                holder.menuDivisionTextView.visibility = View.INVISIBLE

                holder.menuTextView.text = menuItem.menu
                holder.menuTextView.setTextColor(Color.parseColor("#A0A0A0")) // 회색 적용
                holder.menuTextView.textSize = 14f
                holder.menuTextView.gravity = Gravity.CENTER
                holder.menuTextView.visibility = View.VISIBLE
            }

            else -> { // 일반 메뉴 (2열 오른쪽)
                holder.menuDivisionTextView.text = ""
                holder.menuDivisionTextView.visibility = View.INVISIBLE

                holder.menuTextView.text = menuItem.menu.replace(" ", "\n") // 줄 바꿈 적용
                holder.menuTextView.setTextColor(Color.BLACK)
                holder.menuTextView.textSize = 14f
                holder.menuTextView.gravity = Gravity.START
                holder.menuTextView.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = menuList.size

    fun updateMenuList(newMenuList: List<FoodMenuItem>) {
        menuList = newMenuList
        notifyDataSetChanged()
    }

}