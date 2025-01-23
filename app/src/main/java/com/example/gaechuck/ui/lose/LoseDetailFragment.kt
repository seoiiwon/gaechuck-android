package com.example.gaechuck.ui.lose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.gaechuck.R
import com.example.gaechuck.data.model.LoseItem
import com.example.gaechuck.databinding.FragmentLoseDetailBinding
import com.example.gaechuck.ui.business.adapter.ImagePagerAdapter
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class LoseDetailFragment : Fragment(R.layout.fragment_lose_detail) {
    private lateinit var binding: FragmentLoseDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoseDetailBinding.bind(view)

        // RentActivity의 Toolbar 업데이트
        (activity as? LoseActivity)?.updateToolbar(
            title = getString(R.string.bar_lose), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = true // 홈 버튼 표시
        )

        val loseItem = arguments?.let {
            LoseDetailFragmentArgs.fromBundle(it).loseItem
        }

        loseItem?.let {
            setupUI(it)
        }

    }

    private fun setupUI(item: LoseItem) {
        binding.loseName.text = item.name
        binding.loseDate.text = item.date
        binding.loseLocation.text = item.location
        binding.rentInfo.text = item.info
        binding.loseGetDate.text = item.date

        // ViewPager2에 이미지 설정
        val adapter = ImagePagerAdapter(item.images)
        binding.loseImagesViewpager.adapter = adapter

        // 페이지 인디케이터 연결
        val wormDotsIndicator: WormDotsIndicator = binding.imageIndicator
        wormDotsIndicator.setViewPager2(binding.loseImagesViewpager)

    }
}