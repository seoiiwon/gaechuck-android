package com.example.gaechuck.ui.rent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gaechuck.R
import com.example.gaechuck.data.response.GetRentDetailResponse
import com.example.gaechuck.databinding.FragmentRentDetailBinding
import com.example.gaechuck.repository.RentRepository
import com.example.gaechuck.ui.lose.adapter.ImagePagerAdapter
import com.example.gaechuck.ui.rent.viewmodel.RentViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class RentDetailFragment : Fragment(R.layout.fragment_rent_detail) {

    private lateinit var binding: FragmentRentDetailBinding
    private lateinit var rentViewModel: RentViewModel
    private lateinit var rentButton : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRentDetailBinding.bind(view)
        rentButton = view.findViewById(R.id.rent_button)

        //
        val repository = RentRepository()
        val viewModelFactory = RentViewModel.RentViewModelFactory(repository)
        rentViewModel = ViewModelProvider(this, viewModelFactory).get(RentViewModel::class.java)

        // 로그인 상태 확인
        rentViewModel.checkLoginStatus()
        rentViewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            updateToolbar(isLoggedIn)
        })


        val rentItemId = arguments?.let {
            RentDetailFragmentArgs.fromBundle(it).rentItemId
        }

        rentItemId?.let {
            rentViewModel.RentDetailRetrofit(it)
        }

        // LiveData 옵저빙하여 UI 업데이트
        rentViewModel.rentDetailData.observe(viewLifecycleOwner) {
            rentDetail -> rentDetail?.let {
                setupUI(it)
        }
        }

        // 버튼 클릭 시 오픈채팅으로 이동
        rentButton.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com"))
            startActivity(intent)
        }

    }

    // RentActivity의 Toolbar 업데이트
    private fun updateToolbar(isLoggedIn: Boolean) {
        (activity as? RentActivity)?.updateToolbar(
            title = getString(R.string.bar_lose),
            showBackButton = true,
            showHomeButton = !isLoggedIn,
            showEtcButton = isLoggedIn,
        )
    }

    private fun setupUI(item: GetRentDetailResponse) {
        binding.rentName.text = item.rentItemName
        binding.rentCount.text = item.rentItemCount.toString()

        val imageList = listOf(item.rentItemImage)

        // ViewPager2에 이미지 설정 (수정)
        val adapter = ImagePagerAdapter(imageList)
        binding.rentImagesViewpager.adapter = adapter

        // 페이지 인디케이터 연결
        val wormDotsIndicator: WormDotsIndicator = binding.imageIndicator
        wormDotsIndicator.setViewPager2(binding.rentImagesViewpager)

    }

}