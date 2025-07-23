package com.gaechuck_package.gaechuck.ui.menu.adaptor

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.FoodMenuItem

class GridCafeteriaAdapter :
    ListAdapter<FoodMenuItem, RecyclerView.ViewHolder>(DiffCallback()) {


    private var cafeteriaSeq: Int = -1

    fun updateCafeteriaSeq(seq: Int) {
        cafeteriaSeq = seq
    }

    override fun getItemCount(): Int {
        return if (cafeteriaSeq == 8) 1
        else super.getItemCount()
    }


    companion object {
        private const val VIEW_TYPE_STANDARD = 0
        private const val VIEW_TYPE_SEQ8 = 1
        private const val VIEW_TYPE_NO_DATA = 2
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            // CafeteriaSeq가 8인 경우
            cafeteriaSeq == 8 -> VIEW_TYPE_SEQ8
            // 데이터 없으면 NoData
            getItem(position).menuSeq < 0 || getItem(position).menu.isBlank() -> VIEW_TYPE_NO_DATA
            else -> VIEW_TYPE_STANDARD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_STANDARD -> {
                val view = inflater.inflate(R.layout.item_menu_row, parent, false)
                MenuViewHolder(view)
            }
            VIEW_TYPE_SEQ8 -> {
                val view = inflater.inflate(R.layout.item_menu_seq8, parent, false)
                Seq8MenuViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_menu_no_data, parent, false)
                NoDataViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is MenuViewHolder -> holder.bind(item)
            is Seq8MenuViewHolder -> holder.bind(currentList)
            is NoDataViewHolder -> holder.bind()
        }
    }


    // 기본 Grid 뷰
    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val divisionTv = view.findViewById<TextView>(R.id.divisionTextView)
        private val menuTv     = view.findViewById<TextView>(R.id.menuTextView)

        fun bind(item: FoodMenuItem) {
            if (item.menuSeq < 0) {
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
            val startRegex = "\\d{2}:\\d{2} ~".toRegex()
            val match = startRegex.find(raw)
            return if (match != null) {
                val titlePart = raw.substring(0, match.range.first).trim()
                val timePart  = raw.substring(match.range.first).trim()

                val ssb = SpannableStringBuilder()
                    .append(titlePart)
                    .append("\n")
                val offset = ssb.length
                ssb.append(timePart)

                val titleColor = ContextCompat.getColor(ctx, R.color.gnu_blue)
                ssb.setSpan(StyleSpan(Typeface.BOLD), 0, titlePart.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.setSpan(ForegroundColorSpan(titleColor), 0, titlePart.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.setSpan(StyleSpan(Typeface.NORMAL), offset, offset + timePart.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
                    setSpan(StyleSpan(Typeface.BOLD), 0, raw.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(ForegroundColorSpan(titleColor), 0, raw.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }


    // CafeteriaSeq가 8일 때의 뷰
    inner class Seq8MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cells = arrayOf(
            arrayOf(view.findViewById<TextView>(R.id.tv_r1c1), view.findViewById<TextView>(R.id.tv_r1c2), view.findViewById<TextView>(R.id.tv_r1c3)),
            arrayOf(view.findViewById<TextView>(R.id.tv_r2c1), view.findViewById<TextView>(R.id.tv_r2c2), view.findViewById<TextView>(R.id.tv_r2c3)),
            arrayOf(view.findViewById<TextView>(R.id.tv_r3c1), view.findViewById<TextView>(R.id.tv_r3c2), view.findViewById<TextView>(R.id.tv_r3c3)),
            arrayOf(view.findViewById<TextView>(R.id.tv_r4c1), view.findViewById<TextView>(R.id.tv_r4c2), view.findViewById<TextView>(R.id.tv_r4c3))
        )

        fun bind(items: List<FoodMenuItem>) {
            // 1) 모든 칸 초기화
            cells.flatten().forEach { it.text = "" }

            // 2) 열 분류 순서: 아침(0), 점심(1), 저녁(2)
            val divisionOrder = listOf("아침", "점심", "저녁")

            divisionOrder.forEachIndexed { col, division ->
                val divisionItems = items.filter { it.menuDivision == division }

                val aList = divisionItems.filter {
                    it.menu.substringBefore(" ") == "A코스/한식" || it.menu.substringBefore(" ") == "A코스/일품"
                }
                val bList = divisionItems.filter { it.menu.substringBefore(" ") == "B코스/베이커리" }
                val others = divisionItems.filter {
                    val course = it.menu.substringBefore(" ")
                    course != "A코스/한식" && course != "A코스/일품" && course != "B코스/베이커리"
                }

                val colItems = aList + bList + others


                colItems.take(4).forEachIndexed { row, item ->
                    val cell = cells[row][col]
                    cell.text = ""

                    val full   = item.menu.trim()
                    val title  = full.substringBefore(" ")
                    val rest   = full.substringAfter(" ").trim()
                        .split("\\s+".toRegex())
                        .filter { it.isNotBlank() }

                    val ssb = SpannableStringBuilder().apply {
                        append(title)
                        append("\n\n")
                        rest.forEach { append(it).append("\n") }
                        if (endsWith("\n")) delete(length - 1, length)
                    }

                    val colorBlue = ContextCompat.getColor(itemView.context, R.color.gnu_blue)
                    ssb.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(ForegroundColorSpan(colorBlue), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(AbsoluteSizeSpan(12, true), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    cell.text = ssb
                    cell.gravity = Gravity.CENTER
                    cell.setLineSpacing(4f, 1f)
                    cell.setTextColor(ContextCompat.getColor(itemView.context, R.color.black_1))
                    cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    cell.typeface = ResourcesCompat.getFont(itemView.context, R.font.pretendard_regular)
                }
            }
        }
    }



    inner class NoDataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            // 특별한 로직 없음, static xml 사용
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FoodMenuItem>() {
        override fun areItemsTheSame(a: FoodMenuItem, b: FoodMenuItem) =
            a.menuSeq == b.menuSeq && a.date == b.date
        override fun areContentsTheSame(a: FoodMenuItem, b: FoodMenuItem) = a == b
    }
}
