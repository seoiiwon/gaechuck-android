package com.gaechuck_package.gaechuck.ui.menu.adaptor

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.FoodMenuItem

//class GridCafeteriaAdapter(
//    private var menuList: List<FoodMenuItem>,
//) :
//    RecyclerView.Adapter<GridCafeteriaAdapter.MenuViewHolder>() {
//
//    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val menuDivisionTextView: TextView = view.findViewById(R.id.menuDivisionTextView)
//        val menuTextView: TextView = view.findViewById(R.id.menuTextView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_grid, parent, false)
//        return MenuViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
//        val menuItem = menuList[position]
//
//        when (menuItem.menuSeq) {
//            -2 -> { // 카테고리 row
//                holder.menuDivisionTextView.text = menuItem.menuDivision
//                holder.menuDivisionTextView.setTextColor(Color.parseColor("#005BAC")) // GNU 블루
//                holder.menuDivisionTextView.textSize = 16f
//                holder.menuDivisionTextView.setTypeface(null, Typeface.BOLD)
//                holder.menuDivisionTextView.visibility = View.VISIBLE
//
//                holder.menuTextView.text = ""
//                holder.menuTextView.visibility = View.GONE
//            }
//
//            -1 -> { // "식단 정보가 없습니다." row → 1열에 출력
//                holder.menuDivisionTextView.text = menuItem.menu  // 여기 menu에 "식단 정보가 없습니다."가 있어야 함
//                holder.menuDivisionTextView.setTextColor(Color.parseColor("#A0A0A0")) // 회색
//                holder.menuDivisionTextView.textSize = 14f
//                holder.menuDivisionTextView.gravity = Gravity.CENTER
//                holder.menuDivisionTextView.visibility = View.VISIBLE
//
//                holder.menuTextView.text = ""
//                holder.menuTextView.visibility = View.GONE
//            }
//
//            else -> { // 일반 메뉴 row (오른쪽, 2열)
//                holder.menuDivisionTextView.text = ""
//                holder.menuDivisionTextView.visibility = View.GONE
//
//                holder.menuTextView.text = menuItem.menu.replace(" ", "\n") // 줄 바꿈 적용
//                holder.menuTextView.setTextColor(Color.BLACK)
//                holder.menuTextView.textSize = 14f
//                holder.menuTextView.gravity = Gravity.START
//                holder.menuTextView.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = menuList.size
//
//    fun updateMenuList(newMenuList: List<FoodMenuItem>) {
//        menuList = newMenuList
//        notifyDataSetChanged()
//    }
//
//}

//class GridCafeteriaAdapter : ListAdapter<FoodMenuItem, GridCafeteriaAdapter.MenuViewHolder>(DiffCallback()) {
//
//    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val division = view.findViewById<TextView>(R.id.menuDivisionTextView)
//        private val menuText = view.findViewById<TextView>(R.id.menuTextView)
//
//        fun bind(item: FoodMenuItem) {
//            when (item.menuSeq) {
//                -2 -> {
//                    division.apply {
//                        text = item.menuDivision
//                        typeface = Typeface.DEFAULT_BOLD
//                        textSize = 16f
//                        visibility = View.VISIBLE
//                    }
//                    menuText.visibility = View.GONE
//                }
//                -1 -> {
//                    division.apply {
//                        text = item.menu
//                        gravity = Gravity.CENTER
//                        textSize = 14f
//                        visibility = View.VISIBLE
//                    }
//                    menuText.visibility = View.GONE
//                }
//                else -> {
//                    division.visibility = View.GONE
//                    menuText.apply {
//                        text = item.menu.replace(" ", "\n")
//                        visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_menu_grid, parent, false)
//        return MenuViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class DiffCallback : DiffUtil.ItemCallback<FoodMenuItem>() {
//        override fun areItemsTheSame(old: FoodMenuItem, new: FoodMenuItem) =
//            old.menuSeq == new.menuSeq && old.date == new.date
//
//        override fun areContentsTheSame(old: FoodMenuItem, new: FoodMenuItem) = old == new
//    }
//}


class GridCafeteriaAdapter :
    ListAdapter<FoodMenuItem, GridCafeteriaAdapter.MenuViewHolder>(DiffCallback()) {

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val divisionTv = view.findViewById<TextView>(R.id.divisionTextView)
        private val menuTv     = view.findViewById<TextView>(R.id.menuTextView)

        fun bind(item: FoodMenuItem) {
            if (item.menuSeq < 0) {
                // 헤더 / “식단 정보가 없습니다” 같은 row
                divisionTv.apply {
                    text = if (item.menuSeq == -2) item.menuDivision else item.menu
                    setTypeface(null, Typeface.BOLD)
                }
                menuTv.visibility = View.GONE
            } else {
                divisionTv.text = styleDivisionText(item.menuDivision)
                menuTv.apply {
                    text = item.menu.split("/", " ")
                        .filter { it.isNotBlank() }
                        .joinToString("\n") { it.trim() }
                    visibility = View.VISIBLE
                }
            }
        }

        private fun styleDivisionText(raw: String): CharSequence {
            val ctx = divisionTv.context

            // “HH:mm ~” 이 나오는 첫 위치를 찾는다
            val startRegex = "\\d{2}:\\d{2} ~".toRegex()
            val match = startRegex.find(raw)

            return if (match != null) {
                // 1) 제목부 = 매칭 전까지
                val titlePart = raw.substring(0, match.range.first).trim()
                // 2) 시간부 = 매칭된 위치부터 끝까지
                val timePart  = raw.substring(match.range.first).trim()

                // Spannable 으로 조합
                val ssb = SpannableStringBuilder()
                    .append(titlePart)
                    .append("\n")                     // 여기가 줄바꿈 지점
                val offset = ssb.length
                ssb.append(timePart)

                val titleColor = ContextCompat.getColor(ctx, R.color.gnu_blue)
                ssb.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    titlePart.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    ForegroundColorSpan(titleColor),
                    0,
                    titlePart.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // 2) 시간부 전체에 Normal (명시적으로)
                ssb.setSpan(
                    StyleSpan(Typeface.NORMAL),
                    offset,
                    offset + timePart.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // 숫자와 ~ 만 80% 축소
                "[\\d~]".toRegex().findAll(timePart).forEach { m ->
                    val span = RelativeSizeSpan(0.8f)
                    val s = offset + m.range.first
                    val e = offset + m.range.last + 1
                    ssb.setSpan(span, s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                ssb
            } else {
                SpannableStringBuilder(raw).apply {
                    val titleColor = ContextCompat.getColor(ctx, R.color.gnu_blue)
                    setSpan(
                        StyleSpan(Typeface.BOLD),
                        0, raw.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    setSpan(
                        ForegroundColorSpan(titleColor),
                        0, raw.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FoodMenuItem>() {
        override fun areItemsTheSame(a: FoodMenuItem, b: FoodMenuItem) =
            a.menuSeq == b.menuSeq && a.date == b.date
        override fun areContentsTheSame(a: FoodMenuItem, b: FoodMenuItem) = a == b
    }
}