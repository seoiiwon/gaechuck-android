package com.gaechuck_package.gaechuck.ui.noticecouncil.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.data.response.GetCouncilNoticeDataResponse
import java.text.SimpleDateFormat
import java.util.Locale

class NoticeCouncilAdapter(
    private val noticeList: MutableList<GetCouncilNoticeDataResponse>,
    private val onDeleteClick: (Int) -> Unit,
    private val onUpdateClick: (Int) -> Unit,
    private val onItemClick: (GetCouncilNoticeDataResponse) -> Unit
) : RecyclerView.Adapter<NoticeCouncilAdapter.NoticeCouncilViewHolder>() {

    private var filteredList: MutableList<GetCouncilNoticeDataResponse> = noticeList.toMutableList()

    class NoticeCouncilViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageContainer: FrameLayout = view.findViewById(R.id.imageContainer)
        val noticeImage: ImageView = view.findViewById(R.id.noticeImage)

        val noticeTitle: TextView = view.findViewById(R.id.noticeTitle)
        val noticeDescription: TextView = view.findViewById(R.id.noticeDescription)
        val noticeDate: TextView = view.findViewById(R.id.noticeDate)
        val moreButton: ImageButton = view.findViewById(R.id.more_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeCouncilViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notice_council, parent, false)
        return NoticeCouncilViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeCouncilViewHolder, position: Int) {
        val notice = filteredList[position]
        val token = AuthManager.getToken()

        holder.noticeTitle.text = notice.title
        holder.noticeDescription.text = notice.body
        holder.noticeDate.text = formatNoticeDate(notice.time)

        if (!notice.representationImages.isNullOrEmpty()) {

            holder.imageContainer.visibility = View.VISIBLE
            holder.noticeImage.visibility = View.VISIBLE

            Glide.with(holder.itemView.context)
                .load(notice.representationImages)
                .into(holder.noticeImage)
        } else {
            holder.imageContainer.visibility = View.GONE
//            holder.imagePlaceholder.visibility = View.VISIBLE
        }

        // 수정하기 / 삭제하기
        if (token.isNullOrEmpty()) {
            holder.moreButton.visibility = View.INVISIBLE
        } else {
            holder.moreButton.visibility = View.VISIBLE
        }

        holder.moreButton.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.etc_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        onUpdateClick(notice.id)
                        true
                    }
                    R.id.menu_delete -> {
                        onDeleteClick(notice.id)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
        holder.itemView.setOnClickListener {
            onItemClick(notice)
        }
    }

    class NoticeDiffCallback(
        private val oldList: List<GetCouncilNoticeDataResponse>,
        private val newList: List<GetCouncilNoticeDataResponse>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun removeNotice(noticeId: Int) {
        val position = noticeList.indexOfFirst { it.id == noticeId }
        if (position != -1) {
            noticeList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, noticeList.size)
        }
    }

    fun updateData(newList: List<GetCouncilNoticeDataResponse>) {
        val diffCallback = NoticeDiffCallback(filteredList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        filteredList.clear()
        filteredList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun formatNoticeDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date)
        } catch (e: Exception) {
            inputDate
        }
    }
}