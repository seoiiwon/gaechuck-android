package com.example.gaechuck.ui.lose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.gaechuck.R
import com.example.gaechuck.data.response.LoseList
import com.example.gaechuck.databinding.FragmentLoseMainBinding
import com.example.gaechuck.repository.LoseRepository
import com.example.gaechuck.ui.lose.adapter.LoseAdapter
import com.example.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class LoseMainFragment : Fragment(R.layout.fragment_lose_main), LoseAdapter.OnLoseItemClickListener {
    private lateinit var binding: FragmentLoseMainBinding
    private lateinit var viewModel: LoseViewModel
    private lateinit var loseAdapter: LoseAdapter

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: WormDotsIndicator

    private var isFabOpen = false

    private var totalItems = 0
    private var currentPage = 0
    private var isLoading = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // binding을 초기화하는 부분 추가
        binding = FragmentLoseMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkLoginStatus()

        // 플로팅 버튼 상태 초기화
//        if (isFabOpen) {
//            closeFab()
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

        // 로그인 상태 확인
        viewModel.checkLoginStatus()
        viewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
//            binding.optionBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
            binding.writeBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        })

        // LoseActivity의 Toolbar 업데이트
        (activity as? LoseActivity)?.updateToolbar(
            title = getString(R.string.bar_lose), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false, // 홈 버튼 표시
            showEtcButton = false,
            showSearchButton = true,
        )

        viewPager = view.findViewById(R.id.view_pager)
        indicator = view.findViewById(R.id.image_indicator)

        loseAdapter = LoseAdapter(mutableListOf(), 9, this)
        viewPager.adapter = loseAdapter

        // ViewModel 데이터 관찰
        viewModel.loseList.observe(viewLifecycleOwner) { loseList ->
            if (loseList.isNotEmpty()) {
                totalItems = loseList.size
                loseAdapter.updateData(loseList)
                updateIndicator()
            } else {
                Log.d("LoseMainFragment", "loseList is empty.")
            }
            isLoading = false
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == loseAdapter.itemCount - 1 && !isLoading) {
                    isLoading = true
                    currentPage++
                    viewModel.loadLoseData(currentPage)
                }
            }
        })

        // 초기 데이터 로드
        viewModel.loadLoseData(currentPage)

        // 작성하기, url 수정하기 버튼
        binding.writeBtn.setOnClickListener {
            val intent = Intent(activity, LoseWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.urlBtn.setOnClickListener {
            val intent = Intent(activity, LoseUrlChangeActivity::class.java)
            intent.putExtra("chatName", "분실물")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        viewModel.LoseDetailRetrofit("렌트")

    }

    private fun updateIndicator() {
        val totalPages = (totalItems + 8) / 9 // 9개씩 나누어 올림
        indicator.attachTo(viewPager)
    }

    // 네비게이션 처리
    override fun onLoseItemClick(item: LoseList) {
        val action = LoseMainFragmentDirections.actionLoseMainFragmentRoLoseDetailFragment(item.lostItemId)
        view?.findNavController()?.navigate(action)
    }

}