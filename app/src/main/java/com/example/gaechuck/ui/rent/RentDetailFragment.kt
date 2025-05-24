package com.example.gaechuck.ui.rent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

    private var rentItemCount: Int = 0
    private var rentItemName: String = ""
    private var rentItemImage: String = ""

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

        // Activity에 lostItemId 전달
        if (rentItemId != null) {
            (activity as? RentActivity)?.setRentItemId(rentItemId)
        }

        rentItemId?.let {
            rentViewModel.RentDetailRetrofit(it)
        }

        // LiveData 옵저빙하여 UI 업데이트
        rentViewModel.rentDetailData.observe(viewLifecycleOwner) {
            rentDetail -> rentDetail?.let {
                setupUI(it)

                // RentActivity로 데이터 전달
                (activity as? RentActivity)?.setRentItemData(
                    rentItemId = rentItemId ?: -1,
                    rentItemName = it.rentItemName,
                    rentItemCount = it.rentItemCount,
                    rentItemImage = ArrayList(it.images)
                )
            }
        }

        rentViewModel.RentDetailRetrofit("렌트")

        // rentUrl 옵저빙하여 버튼 클릭 시 해당 URL로 이동하도록 설정
        rentViewModel.rentUrl.observe(viewLifecycleOwner) { url ->
            rentButton.setOnClickListener {
                if (!url.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } else {
                    Log.e("RentDetailFragment", "URL is empty or null")
                }
            }
        }

    }

    // RentActivity의 Toolbar 업데이트
    private fun updateToolbar(isLoggedIn: Boolean) {
        (activity as? RentActivity)?.updateToolbar(
            title = getString(R.string.bar_rent),
            showBackButton = true,
            showHomeButton = !isLoggedIn,
            showEtcButton = isLoggedIn,
            showSearchButton = false
        )
    }

    private fun setupUI(item: GetRentDetailResponse) {
        binding.rentName.text = item.rentItemName
        binding.rentCount.text = item.rentItemCount.toString()

        val imageList = item.images

        Log.d("rent", imageList.toString())

        // ViewPager2에 이미지 설정 (수정)
        val adapter = ImagePagerAdapter(this, imageList)
        binding.rentImagesViewpager.adapter = adapter

        // 페이지 인디케이터 연결
        val wormDotsIndicator: WormDotsIndicator = binding.imageIndicator
        wormDotsIndicator.setViewPager2(binding.rentImagesViewpager)

    }

}