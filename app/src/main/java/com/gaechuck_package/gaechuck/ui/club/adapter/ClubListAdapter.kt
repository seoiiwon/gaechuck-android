package com.gaechuck_package.gaechuck.ui.club.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.model.Club
import com.gaechuck_package.gaechuck.data.model.ClubStatus
import com.gaechuck_package.gaechuck.databinding.RowClubItemBinding

class ClubListAdapter(
    private var items: List<Club>,
    private val onItemClick: (Club) -> Unit,
    private val onFavoriteClick: (Club) -> Unit
) : RecyclerView.Adapter<ClubListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RowClubItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Club) {
            binding.clubName.text = item.name
            binding.clubSubtitle.text = "${item.orgType} / ${item.category}"
            binding.clubSummary.text = item.summary

            bindStatusBadge(binding.clubStatusBadge, item.status)
            binding.clubBeginnerBadge.visibility = if (item.beginnerFriendly) android.view.View.VISIBLE else android.view.View.GONE

            binding.clubFavoriteIcon.setImageResource(
                if (item.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )

            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.clubImage.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .transform(RoundedCorners(12))
                    .into(binding.clubImage)
            } else {
                Glide.with(binding.clubImage.context).clear(binding.clubImage)
            }

            binding.root.setOnClickListener { onItemClick(item) }
            binding.clubFavoriteIcon.setOnClickListener { onFavoriteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowClubItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Club>) {
        items = newItems
        notifyDataSetChanged()
    }

    companion object {
        fun bindStatusBadge(badge: android.widget.TextView, status: ClubStatus) {
            when (status) {
                ClubStatus.RECRUITING -> {
                    badge.text = "모집중"
                    badge.setBackgroundResource(R.drawable.bg_club_badge_recruiting)
                    badge.setTextColor(Color.parseColor("#005478"))
                }
                ClubStatus.URGENT -> {
                    badge.text = "마감임박"
                    badge.background = null
                    badge.setTextColor(Color.parseColor("#FB1616"))
                }
                ClubStatus.ALWAYS -> {
                    badge.text = "상시모집"
                    badge.setBackgroundResource(R.drawable.bg_club_badge_neutral)
                    badge.setTextColor(Color.parseColor("#505050"))
                }
                ClubStatus.CLOSED -> {
                    badge.text = "모집 종료"
                    badge.setBackgroundResource(R.drawable.bg_club_badge_neutral)
                    badge.setTextColor(Color.parseColor("#999999"))
                }
            }
        }
    }
}
