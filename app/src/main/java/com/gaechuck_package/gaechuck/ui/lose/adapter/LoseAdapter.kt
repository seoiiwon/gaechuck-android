package com.gaechuck_package.gaechuck.ui.lose.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.data.response.LoseList

class LoseAdapter(private var data: List<LoseList>, // LoseItem 전체 데이터 리스트
                  private val itemsPerPage: Int = 9, // 한 페이지에 표시할 최대 아이템 개수
                  private var totalPages: Int = 1,  // totalPages 추가
                  private val listener: OnLoseItemClickListener,
                  private val usePaging: Boolean = true // true: ViewPager2용, false: RecyclerView용
)
    : RecyclerView.Adapter<LoseAdapter.PageViewHolder>() {

    // 클릭 리스너 인터페이스
    interface OnLoseItemClickListener {
        fun onLoseItemClick(item: LoseList)
    }

    // ViewHolder 정의
    inner class PageViewHolder(private val binding: RecyclerView) :
        RecyclerView.ViewHolder(binding) {
        fun bind(pageItems: List<LoseList>) {
            binding.layoutManager = GridLayoutManager(binding.context, 3) // 3x3 Grid
            binding.adapter = GridAdapter(pageItems, listener) // 각 페이지의 GridAdapter 연결
        }
    }

    // Adapter에 필요한 페이지 수 반환
    override fun getItemCount(): Int {
        return if (usePaging) {
            totalPages // API에서 받은 전체 페이지 수
        } else {
            1
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val recyclerView = RecyclerView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return PageViewHolder(recyclerView)
    }
    // 페이지 데이터 바인딩
//    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
//        val pageItems = if (usePaging) {
//            val start = position * itemsPerPage
//            val end = minOf(start + itemsPerPage, data.size)
//            if (start < data.size) data.subList(start, end) else emptyList()
//        } else {
//            data
//        }
//        holder.bind(pageItems)
//    }
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val pageItems = if (usePaging) {
            val start = position * itemsPerPage
            val end = minOf(start + itemsPerPage, data.size)
            if (start < data.size) {
                data.subList(start, end)
            } else {
                emptyList() // 아직 로드되지 않은 페이지의 경우 빈 리스트
            }
        } else {
            data
        }

        Log.d("LoseAdapter", "Binding page $position with ${pageItems.size} items")
        holder.bind(pageItems)
    }

    fun updateData(newData: List<LoseList>, newTotalPages: Int) {
        data = newData
        totalPages = newTotalPages
        Log.d("LoseAdapter", "Data updated - Items: ${newData.size}, Pages: $newTotalPages")
        notifyDataSetChanged()
    }

}