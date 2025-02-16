package com.example.gaechuck.ui.lose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gaechuck.R
import com.example.gaechuck.data.response.GetLoseDetailResponse
import com.example.gaechuck.databinding.FragmentLoseDetailBinding
import com.example.gaechuck.repository.LoseRepository
import com.example.gaechuck.ui.lose.adapter.ImagePagerAdapter
import com.example.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class LoseDetailFragment : Fragment(R.layout.fragment_lose_detail) {
    private lateinit var binding: FragmentLoseDetailBinding
    private lateinit var viewModel: LoseViewModel
    private lateinit var LoseButton : Button

    override fun onResume() {
        super.onResume()
        viewModel.checkLoginStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoseDetailBinding.bind(view)
        LoseButton = view.findViewById(R.id.lose_button)

        // ViewModel 초기화
        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)


        // 로그인 상태 확인
        viewModel.checkLoginStatus()
        viewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            updateToolbar(isLoggedIn)
        })

        // Arguments에서 lostItemId 가져오기
        val lostItemId = arguments?.let {
            LoseDetailFragmentArgs.fromBundle(it).lostItemId
        }

        lostItemId?.let {
            viewModel.loseDetailRetrofit(it) // API 호출
        }

        // LiveData 옵저빙하여 UI 업데이트
        viewModel.loseDetailData.observe(viewLifecycleOwner) { loseDetail ->
            loseDetail?.let {
                setupUI(it)
            }
        }

        // 버튼 클릭 시 오픈채팅으로 이동
        LoseButton.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com"))
            startActivity(intent)
        }


    }
    // RentActivity의 Toolbar 업데이트
    private fun updateToolbar(isLoggedIn: Boolean) {
        (activity as? LoseActivity)?.updateToolbar(
            title = getString(R.string.bar_lose),
            showBackButton = true,
            showHomeButton = !isLoggedIn,
            showEtcButton = isLoggedIn
        )
    }

    private fun setupUI(item: GetLoseDetailResponse) {
        binding.loseName.text = item.title
        binding.loseDate.text = item.lostDate
        binding.loseLocation.text = item.lostLocation
        binding.rentInfo.text = item.description
        binding.loseGetDate.text = item.lostDate

        // ViewPager2에 이미지 설정
        val adapter = ImagePagerAdapter(item.images)
        binding.loseImagesViewpager.adapter = adapter

        // 페이지 인디케이터 연결
        val wormDotsIndicator: WormDotsIndicator = binding.imageIndicator
        wormDotsIndicator.setViewPager2(binding.loseImagesViewpager)

    }
}