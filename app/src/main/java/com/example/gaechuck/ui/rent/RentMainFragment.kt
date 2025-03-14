package com.example.gaechuck.ui.rent

import VerticalItemDecorate
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.RentList
import com.example.gaechuck.databinding.FragmentRentMainBinding
import com.example.gaechuck.repository.RentRepository
import com.example.gaechuck.ui.lose.LoseUrlChangeActivity
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
    private lateinit var urlButton : Button

    private var isSearchMode = false
    private var searchResults: List<RentList> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding을 초기화하는 부분 추가
        binding = FragmentRentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        rentViewModel.checkLoginStatus()
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
        urlButton = view.findViewById(R.id.url_btn)

        // 로그인 상태 확인
        rentViewModel.checkLoginStatus()
        rentViewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            binding.writeBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        })


        // RentActivity의 Toolbar 업데이트
        (activity as? RentActivity)?.updateToolbar(
            title = getString(R.string.bar_rent), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false, // 홈 버튼 표시
            showEtcButton = false,
        )

        // RecyclerView 설정 (어댑터 설정 *이전*)
        recyclerView = view.findViewById(R.id.rent_view)
        binding.rentView.layoutManager = LinearLayoutManager(context)
        rentAdapter = RentAdapter(this)
        binding.rentView.adapter = rentAdapter

        // LiveData 관찰 설정 (데이터 로딩 *이전*)
        rentViewModel.rentList.observe(viewLifecycleOwner, Observer { rentItems ->
            Log.d("RentMainFragment", "LiveData observed: ${rentItems.size} items")
            rentAdapter.submitList(rentItems.toList())
        })

        // 페이지네이션을 위해 스크롤 리스너 추가
        rentAdapter.setOnScrollListener(recyclerView) {
            if (!isSearchMode) {
                rentViewModel.loadRentData() // 검색 모드가 아닐 때만 추가 데이터 로드
            }
        }


        // 초기 데이터 로드
        rentViewModel.loadRentData()
        rentViewModel.rentList.observe(viewLifecycleOwner, Observer { rentItems ->
            Log.d("RentMainFragment", "LiveData observed: ${rentItems.size} items")
            if (!isSearchMode) {
                rentAdapter.submitList(rentItems.toList())
                originalList = rentItems.toList() // 초기 데이터 로드 후 originalList 초기화
            }
        })

        // VerticalItemDecorate 추가
        val itemDecoration = VerticalItemDecorate(20)
        binding.rentView.addItemDecoration(itemDecoration)


        // search 버튼 찾기
        searchButton.setOnClickListener {
            val rentItemName = binding.searchEditText.text.toString().trim()
            filterList(rentItemName)
        }

        rentViewModel.filterRentList.observe(viewLifecycleOwner, Observer { filteredItems ->
            Log.d("RentMainFragment", "Filtered LiveData observed: ${filteredItems.size} items")
            rentAdapter.submitList(filteredItems.toList())
        })

        // 검색 결과가 없으면 검색 화면 다시 표시
        rentViewModel.isSearchResultEmpty.observe(viewLifecycleOwner, Observer { isEmpty ->
            if (isEmpty) {
                binding.root.findViewById<View>(R.id.search_fragment).apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate().alpha(1f).setDuration(300).start()
                }
                binding.rentView.visibility = View.GONE
            }
        })


        // 버튼 클릭 이벤트
        backButton.setOnClickListener{
            val fadeOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)

            binding.root.findViewById<View>(R.id.search_fragment).visibility = View.GONE // 완전히 숨기기
            binding.root.findViewById<View>(R.id.search_fragment).startAnimation(fadeOut)
            rentViewModel.loadRentData()
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

            isSearchMode = false
            rentAdapter.submitList(originalList)
            rentViewModel.loadRentData()
            // editText 없어지게 만들기
            binding.searchEditText.text.clear()

        }
        callButoon.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com"))
            startActivity(intent)
        }

        // floatBtn 클릭 리스너
        binding.writeBtn.setOnClickListener{
            val intent = Intent(activity, RentWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // urlBtn 클릭 리스너
        binding.urlBtn.setOnClickListener{
            val intent = Intent(activity, LoseUrlChangeActivity::class.java)
            intent.putExtra("chatName", "렌트")
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
    private fun filterList(rentItemName: String) {
        isSearchMode = rentItemName.isNotEmpty()
        if (isSearchMode) {
            rentViewModel.searchRentItems(rentItemName) // API 호출
            binding.root.findViewById<View>(R.id.search_fragment).animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.root.findViewById<View>(R.id.search_fragment).visibility = View.GONE
                }
                .start()
            binding.rentView.visibility = View.VISIBLE // RecyclerView 보이기
            rentAdapter.submitList(searchResults) // 검색 결과 표시
        } else {
            rentAdapter.submitList(originalList) // 검색어가 없으면 원래 리스트로 복귀
            binding.root.findViewById<View>(R.id.search_fragment).animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.root.findViewById<View>(R.id.search_fragment).visibility = View.GONE
                }
                .start()
            binding.rentView.visibility = View.VISIBLE // RecyclerView 보이기
        }
    }
}