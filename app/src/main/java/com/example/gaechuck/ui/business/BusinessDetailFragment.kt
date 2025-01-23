package com.example.gaechuck.ui.business

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gaechuck.R
import com.example.gaechuck.data.model.BusinessItem
import com.example.gaechuck.databinding.FragmentBusinessDetailBinding
import com.example.gaechuck.ui.business.adapter.BusinessAdapter
import com.example.gaechuck.ui.business.adapter.ImagePagerAdapter
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class BusinessDetailFragment : Fragment(R.layout.fragment_business_detail) {
    private val businessViewModel: BusinessViewModel by viewModels()
    private lateinit var binding: FragmentBusinessDetailBinding
    private lateinit var businessAdapter: BusinessAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBusinessDetailBinding.bind(view)

        (activity as? BusinessActivity)?.updateToolbar(
            title = getString(R.string.bar_business), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = true // 홈 버튼 숨김
        )

        // SafeArgs로 데이터 가져오기
        val businessItem = arguments?.let {
            BusinessDetailFragmentArgs.fromBundle(it).businessItem
        }

        businessItem?.let {
            setupUI(it)
        }
    }
    private fun setupUI(item: BusinessItem) {
        binding.businessName.text = item.name
        binding.businessCategory.text = item.category
        binding.businessInfo.text = item.summary

        // ViewPager2에 이미지 설정
        val adapter = ImagePagerAdapter(item.images)
        binding.businessImagesViewpager.adapter = adapter

        // 페이지 인디케이터 연결
        val wormDotsIndicator: WormDotsIndicator = binding.imageIndicator
        wormDotsIndicator.setViewPager2(binding.businessImagesViewpager)

    }


}