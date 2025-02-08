package com.example.gaechuck.ui.lose.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.data.response.LoseList

class LoseAdapter(private val data: List<LoseList>, // LoseItem 전체 데이터 리스트
                  private val itemsPerPage: Int = 9, // 한 페이지에 표시할 최대 아이템 개수
                  private val listener: OnLoseItemClickListener)
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
        return (data.size + itemsPerPage - 1) / itemsPerPage // 페이지 수 계산
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
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val start = position * itemsPerPage
        val end = minOf(start + itemsPerPage, data.size)
        val pageItems = data.subList(start, end) // 페이지별 데이터 분할
        holder.bind(pageItems)
    }

}