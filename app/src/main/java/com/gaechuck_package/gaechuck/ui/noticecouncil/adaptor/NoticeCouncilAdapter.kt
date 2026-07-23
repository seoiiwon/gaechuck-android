package com.gaechuck_package.gaechuck.ui.noticecouncil.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
        val noticeImage: ImageView = view.findViewById(R.id.noticeImage)
        val noticeTitle: TextView = view.findViewById(R.id.noticeTitle)
        val noticeDescription: TextView = view.findViewById(R.id.noticeDescription)
        val noticeDate: TextView = view.findViewById(R.id.noticeDate)
        val moreButton: View = view.findViewById(R.id.more_button)
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
        holder.noticeDescription.text = notice.body.take(80)
        holder.noticeDate.text = formatDate(notice.time)

        // Thumbnail image — always visible; show bg_muted placeholder when no URL
        val imgUrl = notice.representationImages.trim()
        if (imgUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imgUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.noticeImage)
        } else {
            Glide.with(holder.itemView.context).clear(holder.noticeImage)
            holder.noticeImage.setImageDrawable(null)
        }

        // Admin more button (hidden view — no UI button in new design, use long press instead)
        holder.moreButton.visibility = View.GONE

        if (!token.isNullOrEmpty()) {
            holder.itemView.setOnLongClickListener { v ->
                val popup = PopupMenu(v.context, v)
                popup.menuInflater.inflate(R.menu.etc_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> { onUpdateClick(notice.id); true }
                        R.id.menu_delete -> { onDeleteClick(notice.id); true }
                        else -> false
                    }
                }
                popup.show()
                true
            }
        }

        holder.itemView.setOnClickListener { onItemClick(notice) }
    }

    private fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            outputFormat.format(inputFormat.parse(inputDate)!!)
        } catch (e: Exception) {
            inputDate
        }
    }

    class NoticeDiffCallback(
        private val oldList: List<GetCouncilNoticeDataResponse>,
        private val newList: List<GetCouncilNoticeDataResponse>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(old: Int, new: Int) = oldList[old].id == newList[new].id
        override fun areContentsTheSame(old: Int, new: Int) = oldList[old] == newList[new]
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateData(newList: List<GetCouncilNoticeDataResponse>) {
        val diff = DiffUtil.calculateDiff(NoticeDiffCallback(filteredList, newList))
        filteredList.clear()
        filteredList.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    fun removeNotice(noticeId: Int) {
        val pos = filteredList.indexOfFirst { it.id == noticeId }
        if (pos != -1) {
            filteredList.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }
}
