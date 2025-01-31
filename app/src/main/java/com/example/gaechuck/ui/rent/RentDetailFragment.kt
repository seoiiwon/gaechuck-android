package com.example.gaechuck.ui.rent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.gaechuck.R
import com.example.gaechuck.data.model.RentItem
import com.example.gaechuck.databinding.FragmentRentDetailBinding
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class RentDetailFragment : Fragment(R.layout.fragment_rent_detail) {

    private lateinit var binding: FragmentRentDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRentDetailBinding.bind(view)

        // RentActivity의 Toolbar 업데이트
        (activity as? RentActivity)?.updateToolbar(
            title = getString(R.string.bar_rent), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = true // 홈 버튼 표시
        )

        val rentItem = arguments?.let {
            RentDetailFragmentArgs.fromBundle(it).rentItem
        }

        rentItem?.let {
            setupUI(it)
        }

    }

    private fun setupUI(item: RentItem) {
        binding.rentName.text = item.name
        binding.rentCount.text = item.count.toString()
        binding.rentInfo.text = item.info

        // ViewPager2에 이미지 설정
//        val adapter = ImagePagerAdapter(item.images)
//        binding.rentImagesViewpager.adapter = adapter

        // 페이지 인디케이터 연결
        val wormDotsIndicator: WormDotsIndicator = binding.imageIndicator
        wormDotsIndicator.setViewPager2(binding.rentImagesViewpager)

    }

}