package com.example.gaechuck.ui.rent

import VerticalItemDecorate
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.databinding.FragmentRentMainBinding
import com.example.gaechuck.repository.RentRepository
import com.example.gaechuck.ui.rent.adapter.RentAdapter
import com.example.gaechuck.ui.rent.viewmodel.RentViewModel

class RentMainFragment : Fragment(R.layout.fragment_rent_main),RentAdapter.OnRentItemClickListener {
    private lateinit var binding: FragmentRentMainBinding
    private lateinit var rentViewModel: RentViewModel
    private lateinit var rentAdapter: RentAdapter
    private lateinit var searchButton : ImageView
    private var originalList: List<RentList> = listOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton : Button
    private lateinit var callButoon : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding을 초기화하는 부분 추가
        binding = FragmentRentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        val repository = RentRepository()
        val viewModelFactory = RentViewModel.RentViewModelFactory(repository)
        rentViewModel = ViewModelProvider(this, viewModelFactory).get(RentViewModel::class.java)

        // 버튼 설정
        searchButton = view.findViewById(R.id.searchButton)
        backButton = view.findViewById(R.id.search_back_btn)
        callButoon = view.findViewById(R.id.search_call_btn)

        // RentActivity의 Toolbar 업데이트
        (activity as? RentActivity)?.updateToolbar(
            title = getString(R.string.bar_rent), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false // 홈 버튼 표시
        )

        // RecyclerView 설정
        recyclerView = view.findViewById(R.id.rent_view)
        binding.rentView.layoutManager = LinearLayoutManager(context)

        // Adapter 설정
        rentAdapter = RentAdapter(this)
        binding.rentView.adapter = rentAdapter

        // VerticalItemDecorate 추가
        val itemDecoration = VerticalItemDecorate(20)
        binding.rentView.addItemDecoration(itemDecoration)

        // 데이터 가져오는 함수 호출
        ShowRentItems()

        // search 버튼 찾기
        searchButton.setOnClickListener{
            val query = binding.searchEditText.text.toString().trim()
            filterList(query)
        }

        // 버튼 클릭 이벤트
        backButton.setOnClickListener{
            val fadeOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)

            binding.root.findViewById<View>(R.id.search_fragment).visibility = View.GONE // 완전히 숨기기
            binding.root.findViewById<View>(R.id.search_fragment).startAnimation(fadeOut)
            ShowRentItems()
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // 애니메이션 시작 시 처리할 내용
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // 애니메이션이 끝난 후 실행할 내용
                    binding.root.findViewById<View>(R.id.search_fragment).visibility = View.GONE

                    // RecyclerView 서서히 나타나게
                    val fadeIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
                    binding.rentView.startAnimation(fadeIn)
                    binding.rentView.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // 애니메이션 반복 시 처리할 내용
                }
            })

            // editText 없어지게 만들기
            binding.searchEditText.text.clear()

        }
        callButoon.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com"))
            startActivity(intent)
        }


    }

    // 데이터 가져오는 함수
    private fun ShowRentItems() {
        rentViewModel.rentList.observe(viewLifecycleOwner) { rentItems ->
            originalList = rentItems.toList() // 원본 데이터 저장
            rentAdapter.submitList(rentItems.toList())  // 새로운 리스트 객체 생성
        }
    }

    // 렌트 아이템 클릭 시 네비게이션 처리
    override fun OnRentItemClick(item: RentList) {
        val action = RentMainFragmentDirections.actionRentMainFragmentToRentDetailFragment(item.rentItemId)
        view?.findNavController()?.navigate(action)
    }

    // 검색 기능
    private fun filterList(query: String) {
        val filterList = originalList.filter {
            it.rentItemName.contains(query, ignoreCase = true)
        }
        if (filterList.isNotEmpty()) {
            binding.root.findViewById<View>(R.id.search_fragment).animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction { binding.root.findViewById<View>(R.id.search_fragment).visibility = View.GONE }
                .start()
            binding.rentView.visibility = View.VISIBLE // RecyclerView 보이기
        } else {
            binding.root.findViewById<View>(R.id.search_fragment).apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(300).start()
            }
            binding.rentView.visibility = View.GONE // RecyclerView 숨기기
        }

        rentAdapter.filteredList(filterList) // 필터링된 리스트 적용
    }
}