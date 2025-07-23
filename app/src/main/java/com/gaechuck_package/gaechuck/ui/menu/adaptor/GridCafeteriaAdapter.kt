package com.gaechuck_package.gaechuck.ui.menu.adaptor

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
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

class GridCafeteriaAdapter :
    ListAdapter<FoodMenuItem, RecyclerView.ViewHolder>(DiffCallback()) {


    private var cafeteriaSeq: Int = -1

    fun updateCafeteriaSeq(seq: Int) {
        cafeteriaSeq = seq
    }

    companion object {
        private const val VIEW_TYPE_STANDARD = 0
        private const val VIEW_TYPE_SEQ8 = 1
        private const val VIEW_TYPE_NO_DATA = 2
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            // cafeteriaSeq 가 8이면 Seq8 전용 뷰
            cafeteriaSeq == 8 -> VIEW_TYPE_SEQ8
            // 데이터 없으면 NoData 뷰
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
            is Seq8MenuViewHolder -> holder.bind(item)
            is NoDataViewHolder -> holder.bind()
        }
    }


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


    inner class Seq8MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val menuTv = view.findViewById<TextView>(R.id.seq8TextView)
        fun bind(item: FoodMenuItem) {
            menuTv.text = item.menu
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
