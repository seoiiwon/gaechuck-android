package com.example.gaechuck.ui.business

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gaechuck.R
import com.example.gaechuck.data.response.BusinessList
import com.example.gaechuck.databinding.FragmentBusinessMainBinding
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.ui.business.adapter.BusinessAdapter
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.google.android.material.tabs.TabLayout

class BusinessMainFragment : Fragment(R.layout.fragment_business_main), BusinessAdapter.OnBusinessItemClickListener {

    private lateinit var binding: FragmentBusinessMainBinding
    private lateinit var businessViewModel: BusinessViewModel
    private lateinit var businessAdapter: BusinessAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var currentPage = 0
    private var isLoading = false
    private var currentCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBusinessMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //
        val repository = BusinessRepository()
        val viewModelFactory = BusinessViewModel.BusinessViewModelFactory(repository)
        businessViewModel = ViewModelProvider(this, viewModelFactory).get(BusinessViewModel::class.java)

        // RentActivity의 Toolbar 업데이트
        (activity as? BusinessActivity)?.updateToolbar(
            title = getString(R.string.bar_business), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false, // 홈 버튼 숨김
            showEtcButton = false,
        )

        // 로그인 상태 확인
        businessViewModel.checkLoginStatus()
        businessViewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            binding.writeBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        })

        val category: Array<String> = resources.getStringArray(R.array.CATEGORY)

        // DividerItemDecoration을 RecyclerView에 추가
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        binding.businessView.addItemDecoration(divider)

        // RecyclerView 설정
        linearLayoutManager = LinearLayoutManager(context)
        binding.businessView.layoutManager = linearLayoutManager

        // Adapter 설정
        businessAdapter = BusinessAdapter(mutableListOf(), this)
        binding.businessView.adapter = businessAdapter

        // 스크롤 리스너 추가
        binding.businessView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadData(currentCategory ?: "")
                }
            }
        })

        // 카테고리 탭 설정
        val categoryArray = resources.getStringArray(R.array.CATEGORY)
        for (i in categoryArray.indices) {
            val tab = binding.selectCategoryTl.newTab().setText(categoryArray[i])
            tab.contentDescription = categoryArray[i]
            binding.selectCategoryTl.addTab(tab)
        }

        // 기본적으로 첫 번째 탭 "전체"가 선택되도록 설정
        binding.selectCategoryTl.selectTab(binding.selectCategoryTl.getTabAt(0))

        // 초기 데이터 로드
        loadData()

        // 탭 선택 리스너 추가
        binding.selectCategoryTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedCategory = tab?.text.toString()
                currentPage = 0
                businessAdapter.updateItems(emptyList()) // UI에서만 초기화하고 ViewModel 데이터는 유지
                businessViewModel.clearBusinessList()

                if (selectedCategory == "전체") {
                    currentCategory = null
                    loadData(category = "") // "전체"는 빈값으로 처리
                } else {
                    currentCategory = selectedCategory // 현재 카테고리 업데이트
                    loadData(category = selectedCategory) // 선택된 카테고리 데이터 로드
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // TabLayout의 탭에 마진 추가
        tabItemMargin(binding.selectCategoryTl)

        // floatBtn 클릭 리스너
        binding.writeBtn.setOnClickListener{
            // TODO : 클릭했을 때 다시 토큰 검사
            val intent = Intent(activity, BusinessWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // Observe businessList LiveData
        businessViewModel.businessList.observe(viewLifecycleOwner) { list ->
            updateRecyclerView(list)
        }
    }

    private fun loadData(category: String = "") {
        if (isLoading) return  // 데이터 로드 중일 때는 아무 작업도 하지 않도록

        isLoading = true  // 데이터 로딩 시작
        val categoryToLoad = if (category.isEmpty()) null else category
        businessViewModel.loadBusinessData(currentPage, categoryToLoad)
        currentPage++
    }


    private fun updateRecyclerView(newList: List<BusinessList>) {
        businessAdapter.updateItems(newList)
        isLoading = false
    }

    override fun onResume() {
        super.onResume()
        businessViewModel.checkLoginStatus()
    }

    // 비즈니스 아이템 클릭 시 네비게이션 처리
    override fun onBusinessItemClick(item: BusinessList) {
        val action = BusinessMainFragmentDirections
            .actionBusinessMainFragmentToBusinessDetailFragment(item.coalitionId)

        view?.findNavController()?.navigate(action)
    }

    private fun tabItemMargin(mTabLayout: TabLayout) {
        for (i in 0 until mTabLayout.tabCount) {
            val tab = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(0, 0, dpToPx(8), 0) // dpToPx는 픽셀 단위로 변환하는 함수입니다.
            tab.requestLayout()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


}