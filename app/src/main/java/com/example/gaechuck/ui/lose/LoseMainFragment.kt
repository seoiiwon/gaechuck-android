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
    private var isLoading = false
    private var hasLoadedNextPage = false // 다음 페이지 로드 여부 추적

    private var currentPage = 0


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

        loseAdapter = LoseAdapter(mutableListOf(), 9, 0,this)
        viewPager.adapter = loseAdapter

        // ViewModel 데이터 관찰

        viewModel.loseList.observe(viewLifecycleOwner) { loseList ->
            if (loseList.isNotEmpty()) {
                val totalPages = viewModel.totalPages.value ?: 1
                loseAdapter.updateData(loseList, totalPages)
                indicator.attachTo(viewPager)
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

        viewModel.totalPages.observe(viewLifecycleOwner) { totalPages ->
            loseAdapter.updateData(viewModel.loseList.value.orEmpty(), totalPages)
            indicator.attachTo(viewPager)
        }

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

//        setupViewPager()
//        setupObservers()
//        setupClickListeners()

        viewModel.LoseDetailRetrofit("분실물")

    }
//
//    private fun setupViewPager() {
//        loseAdapter = LoseAdapter(mutableListOf(), 9, 0, this)
//        viewPager.adapter = loseAdapter
//
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                Log.d("LoseMainFragment", "ViewPager position selected: $position")
//
//                val totalPages = viewModel.totalPages.value ?: 1
//
//                // position이 마지막 페이지이고, 아직 다음 데이터를 로드하지 않았으며,
//                // 더 로드할 페이지가 있다면 다음 페이지 로드
//                if (position == loseAdapter.itemCount - 1 &&
//                    !isLoading &&
//                    !hasLoadedNextPage &&
//                    (position + 1) < totalPages) {
//
//                    isLoading = true
//                    hasLoadedNextPage = true
//                    val nextApiPage = position + 1 // ViewPager position + 1 = 다음 API 페이지
//
//                    Log.d("LoseMainFragment", "Loading next API page: $nextApiPage")
//                    viewModel.loadLoseData(nextApiPage)
//                }
//            }
//        })
//    }
//
//    private fun setupObservers() {
//        viewModel.loseList.observe(viewLifecycleOwner) { loseList ->
//            if (loseList.isNotEmpty()) {
//                val totalPages = viewModel.totalPages.value ?: 1
//                loseAdapter.updateData(loseList, totalPages)
//                indicator.attachTo(viewPager)
//
//                Log.d("LoseMainFragment", "Data updated - Items: ${loseList.size}, Pages: $totalPages")
//            } else {
//                Log.d("LoseMainFragment", "loseList is empty.")
//            }
//            isLoading = false
//            hasLoadedNextPage = false // 로딩 완료 후 플래그 리셋
//        }
//
//        viewModel.totalPages.observe(viewLifecycleOwner) { totalPages ->
//            loseAdapter.updateData(viewModel.loseList.value.orEmpty(), totalPages)
//            indicator.attachTo(viewPager)
//        }
//    }
//
//    private fun setupClickListeners() {
//        binding.writeBtn.setOnClickListener {
//            val intent = Intent(activity, LoseWriteActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//
//        binding.urlBtn.setOnClickListener {
//            val intent = Intent(activity, LoseUrlChangeActivity::class.java)
//            intent.putExtra("chatName", "분실물")
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//    }

    private fun updateIndicator(totalPages: Int) {
        indicator.attachTo(viewPager)

    }

    // 네비게이션 처리
    override fun onLoseItemClick(item: LoseList) {
        val action = LoseMainFragmentDirections.actionLoseMainFragmentRoLoseDetailFragment(item.lostItemId)
        view?.findNavController()?.navigate(action)
    }

}