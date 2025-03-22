package com.example.gaechuck.ui.business

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gaechuck.R
import com.example.gaechuck.data.response.GetBusinessDetailResponse
import com.example.gaechuck.databinding.FragmentBusinessDetailBinding
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.ui.business.adapter.ImagePagerAdapter
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.example.gaechuck.ui.rent.RentActivity
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class BusinessDetailFragment : Fragment(R.layout.fragment_business_detail) {
    private lateinit var businessViewModel: BusinessViewModel
    private lateinit var binding: FragmentBusinessDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBusinessDetailBinding.bind(view)

        //
        val repository = BusinessRepository()
        val viewModelFactory = BusinessViewModel.BusinessViewModelFactory(repository)
        businessViewModel = ViewModelProvider(this, viewModelFactory).get(BusinessViewModel::class.java)

        // 로그인 상태 확인
        businessViewModel.checkLoginStatus()
        businessViewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            updateToolbar(isLoggedIn)
        })

        // SafeArgs로 데이터 가져오기
        val businessItem = arguments?.let {
            BusinessDetailFragmentArgs.fromBundle(it).coalitionId
        }

        // Arguments에서 lostItemId 가져오기
        val coalitionId = arguments?.let {
            BusinessDetailFragmentArgs.fromBundle(it).coalitionId
        }

        // Activity에 lostItemId 전달
        if (coalitionId != null) {
            (activity as? BusinessActivity)?.setCoalitionItemId(coalitionId)
        }


        businessItem?.let {
            businessViewModel.BusinessDetailRetrofit(it)
        }

        businessViewModel.businessDetailData.observe(viewLifecycleOwner) {
            businessDetail ->
                businessDetail.let {
                    setupUI(it)

                    // BusinessActivity로 데이터 전달
                    (activity as? BusinessActivity)?.setLoseItemData(
                        coalitionId = coalitionId ?: -1,
                        coalitionName = it.coalitionName,
                        benefit = it.benefit,
                        category = it.category,
                        images = ArrayList(it.images)
                    )
                }
        }
    }

    // BusinessActivity의 Toolbar 업데이트
    private fun updateToolbar(isLoggedIn: Boolean) {
        (activity as? BusinessActivity)?.updateToolbar(
            title = getString(R.string.bar_business),
            showBackButton = true,
            showHomeButton = !isLoggedIn,
            showEtcButton = isLoggedIn,
        )
    }


    private fun setupUI(item: GetBusinessDetailResponse) {
        binding.businessName.text = item.coalitionName
        binding.businessCategory.text = item.category
        binding.businessInfo.text = item.benefit

        // ViewPager2에 이미지 설정
        val adapter = ImagePagerAdapter(item.images)
        binding.businessImagesViewpager.adapter = adapter

        // 페이지 인디케이터 연결
        val wormDotsIndicator: WormDotsIndicator = binding.imageIndicator
        wormDotsIndicator.setViewPager2(binding.businessImagesViewpager)

    }


}