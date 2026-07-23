package com.gaechuck_package.gaechuck.ui.club

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.model.ClubDetail
import com.gaechuck_package.gaechuck.data.model.ClubScheduleItem
import com.gaechuck_package.gaechuck.databinding.FragmentClubDetailBinding
import com.gaechuck_package.gaechuck.databinding.RowClubScheduleItemBinding
import com.gaechuck_package.gaechuck.repository.ClubRepository
import com.gaechuck_package.gaechuck.ui.club.adapter.ClubGalleryAdapter
import com.gaechuck_package.gaechuck.ui.club.adapter.ClubListAdapter
import com.gaechuck_package.gaechuck.ui.club.viewmodel.ClubViewModel

class ClubDetailFragment : Fragment(R.layout.fragment_club_detail) {

    private lateinit var binding: FragmentClubDetailBinding
    private lateinit var viewModel: ClubViewModel
    private val args: ClubDetailFragmentArgs by navArgs()
    private var currentDetail: ClubDetail? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClubDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = ClubViewModel.ClubViewModelFactory(ClubRepository.getInstance())
        viewModel = ViewModelProvider(this, viewModelFactory)[ClubViewModel::class.java]

        (activity as? ClubActivity)?.setShareButtonVisible(true) { shareClub() }

        setupTabs()

        viewModel.clubDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let { bindDetail(it) }
        }
        viewModel.loadClubDetail(args.clubId)
    }

    private fun bindDetail(detail: ClubDetail) {
        currentDetail = detail
        (activity as? ClubActivity)?.setToolbarTitle("${detail.orgType} / ${detail.category}")
        binding.clubName.text = detail.name
        binding.clubSubtitle.text = "${detail.orgType} / ${detail.category}"
        ClubListAdapter.bindStatusBadge(binding.clubStatusBadge, detail.status)
        binding.clubBeginnerBadge.visibility = if (detail.beginnerFriendly) View.VISIBLE else View.GONE
        binding.introBody.text = detail.introduction

        (activity as? ClubActivity)?.setFavoriteButtonVisible(true, detail.isFavorite) {
            viewModel.toggleFavorite(detail.id)
        }

        bindActivityInfo(detail)
        bindContact(detail)
        bindSchedule(detail.recruitSchedule)
        bindGallery(detail.galleryImages)

        binding.applyBtn.setOnClickListener { showApplySheet(detail) }

        showTab(binding.clubTabs.selectedTabPosition.coerceAtLeast(0))
    }

    private fun bindActivityInfo(detail: ClubDetail) {
        val hasAny = !detail.meetingInfo.isNullOrEmpty() || !detail.fee.isNullOrEmpty() || !detail.preparation.isNullOrEmpty()
        binding.activityInfoSection.visibility = if (hasAny) View.VISIBLE else View.GONE

        binding.meetingInfoText.visibility = if (detail.meetingInfo.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.meetingInfoText.text = detail.meetingInfo

        binding.feeText.visibility = if (detail.fee.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.feeText.text = detail.fee

        binding.preparationText.visibility = if (detail.preparation.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.preparationText.text = detail.preparation
    }

    private fun bindContact(detail: ClubDetail) {
        val hasContact = !detail.contactUrl.isNullOrEmpty()
        binding.contactSection.visibility = if (hasContact) View.VISIBLE else View.GONE
        if (hasContact) {
            binding.contactChip.text = "${detail.contactLabel ?: "링크"} ↗"
            binding.contactChip.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detail.contactUrl)))
            }
        }
    }

    private fun bindSchedule(schedule: List<ClubScheduleItem>) {
        binding.scheduleContent.removeAllViews()
        if (schedule.isEmpty()) {
            binding.scheduleEmptyState.visibility = View.VISIBLE
            return
        }
        binding.scheduleEmptyState.visibility = View.GONE
        schedule.forEachIndexed { index, item ->
            val rowBinding = RowClubScheduleItemBinding.inflate(LayoutInflater.from(requireContext()), binding.scheduleContent, false)
            rowBinding.scheduleLabel.text = item.label
            rowBinding.scheduleDate.text = item.dateRange
            rowBinding.dotView.setBackgroundResource(
                if (item.isCurrent) R.drawable.dot_timeline_current else R.drawable.dot_timeline_pending
            )
            rowBinding.lineView.visibility = if (index == schedule.lastIndex) View.INVISIBLE else View.VISIBLE
            binding.scheduleContent.addView(rowBinding.root)
        }
    }

    private fun bindGallery(images: List<String>) {
        val hasImages = images.isNotEmpty()
        binding.galleryEmptyState.visibility = if (hasImages) View.GONE else View.VISIBLE
        binding.galleryRecyclerView.visibility = if (hasImages) View.VISIBLE else View.GONE
        if (hasImages) {
            binding.galleryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.galleryRecyclerView.adapter = ClubGalleryAdapter(images)
        }
    }

    private fun showApplySheet(detail: ClubDetail) {
        val url = detail.applyUrl
        if (url.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "아직 등록된 지원 링크가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        val period = detail.recruitSchedule.firstOrNull { it.isCurrent }?.dateRange
            ?: detail.recruitSchedule.firstOrNull()?.dateRange
        ClubApplyBottomSheetFragment.newInstance(detail.name, period, url)
            .show(childFragmentManager, "club_apply_sheet")
    }

    private fun shareClub() {
        val detail = currentDetail ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "${detail.name} - ${detail.summary}")
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun setupTabs() {
        showTab(0)
        binding.clubTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { showTab(tab.position) }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun showTab(position: Int) {
        binding.introContent.visibility = if (position == 0) View.VISIBLE else View.GONE

        val showSchedule = position == 1
        val hasSchedule = binding.scheduleContent.childCount > 0
        binding.scheduleContent.visibility = if (showSchedule && hasSchedule) View.VISIBLE else View.GONE
        binding.scheduleEmptyState.visibility = if (showSchedule && !hasSchedule) View.VISIBLE else View.GONE

        val showGallery = position == 2
        val hasGallery = binding.galleryRecyclerView.adapter != null && binding.galleryRecyclerView.adapter!!.itemCount > 0
        binding.galleryRecyclerView.visibility = if (showGallery && hasGallery) View.VISIBLE else View.GONE
        binding.galleryEmptyState.visibility = if (showGallery && !hasGallery) View.VISIBLE else View.GONE
    }
}
