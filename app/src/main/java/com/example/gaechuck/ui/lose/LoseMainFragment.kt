package com.example.gaechuck.ui.lose

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
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
        if (isFabOpen) {
            closeFab()
        }
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
            binding.optionBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        })

        // LoseActivity의 Toolbar 업데이트
        (activity as? LoseActivity)?.updateToolbar(
            title = getString(R.string.bar_lose), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false, // 홈 버튼 표시
            showEtcButton = false,
        )

        viewPager = view.findViewById(R.id.view_pager)
        indicator = view.findViewById(R.id.image_indicator)

        // ViewModel 데이터 관찰
        viewModel.loseList.observe(viewLifecycleOwner) { loseList ->
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

        // 플로팅 버튼 클릭시 에니메이션 동작 기능
        binding.optionBtn.setOnClickListener {
            toggleFab()
        }

        // 작성하기, url 수정하기 버튼
        binding.writeBtn.setOnClickListener {
            val intent = Intent(activity, LoseWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.urlBtn.setOnClickListener {
            val intent = Intent(activity, LoseUrlChangeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }

    private fun closeFab() {
        ObjectAnimator.ofFloat(binding.writeBtn, "translationY", 20f).apply {
            start()
            doOnEnd { binding.writeBtn.visibility = View.GONE }
        }
        ObjectAnimator.ofFloat(binding.urlBtn, "translationY", 20f).apply {
            start()
            doOnEnd { binding.urlBtn.visibility = View.GONE }
        }
        ObjectAnimator.ofFloat(binding.optionBtn, View.ROTATION, 90F, 0f).apply {
            duration = 300
            start()
            doOnEnd { binding.optionBtn.setImageResource(R.drawable.ic_etc) }
        }
        isFabOpen = false
    }

    // floatBtn 애니메이션 효과
    private fun toggleFab() {

        // 플로팅 액션 버튼 닫기 - 열려있는 플로팅 버튼 집어넣는 애니메이션 세팅
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.writeBtn, "translationY", 20f).apply {
                start()
                doOnEnd { binding.writeBtn.visibility = View.GONE }
            }
            ObjectAnimator.ofFloat(binding.urlBtn, "translationY", 20f).apply {
                start()
                doOnEnd { binding.urlBtn.visibility = View.GONE }

            }
            ObjectAnimator.ofFloat(binding.optionBtn, View.ROTATION, 90F, 0f).apply {
                duration = 300
                start()
                doOnEnd { binding.optionBtn.setImageResource(R.drawable.ic_etc) }
            }

            // 플로팅 액션 버튼 열기 - 닫혀있는 플로팅 버튼 꺼내는 애니메이션 세팅
        } else {
            binding.writeBtn.visibility = View.VISIBLE
            binding.urlBtn.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(binding.urlBtn, "translationY", -160f).apply { start() }
            ObjectAnimator.ofFloat(binding.writeBtn, "translationY", -300f).apply { start() }
            ObjectAnimator.ofFloat(binding.optionBtn, View.ROTATION, 0F, 90f).apply {
                duration = 300
                start()
            }
        }

        isFabOpen = !isFabOpen
    }

    // 네비게이션 처리
    override fun onLoseItemClick(item: LoseList) {
        val action = LoseMainFragmentDirections.actionLoseMainFragmentRoLoseDetailFragment(item.lostItemId)
        view?.findNavController()?.navigate(action)
    }

}