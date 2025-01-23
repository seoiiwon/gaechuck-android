package com.example.gaechuck.ui.lose

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.gaechuck.R
import com.example.gaechuck.data.model.LoseItem
import com.example.gaechuck.databinding.FragmentLoseMainBinding
import com.example.gaechuck.ui.lose.adapter.LoseAdapter
import com.example.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class LoseMainFragment : Fragment(R.layout.fragment_lose_main), LoseAdapter.OnLoseItemClickListener {
    private lateinit var binding: FragmentLoseMainBinding
    private val loseViewModel: LoseViewModel by viewModels()
    private lateinit var loseAdapter: LoseAdapter

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: WormDotsIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // binding을 초기화하는 부분 추가
        binding = FragmentLoseMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 예시로 초기값을 확인
        Log.d("LoseMainFragment", "Initial data: ${loseViewModel.loseList.value}")

        // LoseActivity의 Toolbar 업데이트
        (activity as? LoseActivity)?.updateToolbar(
            title = getString(R.string.bar_lose), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false // 홈 버튼 표시
        )

        viewPager = view.findViewById(R.id.view_pager)
        indicator = view.findViewById(R.id.image_indicator)

        // ViewModel 데이터 관찰
        loseViewModel.loseList.observe(viewLifecycleOwner) { loseList ->
            if (loseList.isEmpty()) {
                Log.d("LoseMainFragment", "loseList is empty.")
            } else {
                Log.d("LoseMainFragment", "loseList size: ${loseList.size}")
                // 어댑터 생성 및 설정
                loseAdapter = LoseAdapter(
                    data = loseList,
                    itemsPerPage = 9, // 페이지당 9개 아이템
                    listener = this // 클릭 리스너
                )
                viewPager.adapter = loseAdapter

                // 페이지 표시를 위한 DotsIndicator 설정
                indicator.attachTo(viewPager)
            }
        }

    }

    // 네비게이션 처리
    override fun onLoseItemClick(item: LoseItem) {
        val action = LoseMainFragmentDirections.actionLoseMainFragmentRoLoseDetailFragment(item)
        view?.findNavController()?.navigate(action)
    }

}