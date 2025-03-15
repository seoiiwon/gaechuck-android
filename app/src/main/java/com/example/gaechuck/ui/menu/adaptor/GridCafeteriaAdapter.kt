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
            -2 -> { // 카테고리 row
                holder.menuDivisionTextView.text = menuItem.menuDivision
                holder.menuDivisionTextView.setTextColor(Color.parseColor("#005BAC")) // GNU 블루
                holder.menuDivisionTextView.textSize = 16f
                holder.menuDivisionTextView.setTypeface(null, Typeface.BOLD)
                holder.menuDivisionTextView.visibility = View.VISIBLE

                holder.menuTextView.text = ""
                holder.menuTextView.visibility = View.GONE
            }

            -1 -> { // "식단 정보가 없습니다." row → 1열에 출력
                holder.menuDivisionTextView.text = menuItem.menu  // 여기 menu에 "식단 정보가 없습니다."가 있어야 함
                holder.menuDivisionTextView.setTextColor(Color.parseColor("#A0A0A0")) // 회색
                holder.menuDivisionTextView.textSize = 14f
                holder.menuDivisionTextView.gravity = Gravity.CENTER
                holder.menuDivisionTextView.visibility = View.VISIBLE

                holder.menuTextView.text = ""
                holder.menuTextView.visibility = View.GONE
            }

            else -> { // 일반 메뉴 row (오른쪽, 2열)
                holder.menuDivisionTextView.text = ""
                holder.menuDivisionTextView.visibility = View.GONE

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