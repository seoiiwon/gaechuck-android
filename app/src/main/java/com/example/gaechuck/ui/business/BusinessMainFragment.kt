package com.example.gaechuck.ui.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
            showHomeButton = false // 홈 버튼 숨김
        )

        val category: Array<String> = resources.getStringArray(R.array.CATEGORY)

        // DividerItemDecoration을 RecyclerView에 추가
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        binding.businessView.addItemDecoration(divider)

        // RecyclerView 설정
        binding.businessView.layoutManager = LinearLayoutManager(context)

        // Adapter 설정
        businessAdapter = BusinessAdapter(emptyList(), this)
        binding.businessView.adapter = businessAdapter

        // 카테고리 탭 설정
        for (i in category.indices) {
            val tab = binding.selectCategoryTl.newTab().setText(category[i])
            tab.contentDescription = category[i] // 접근성 설정 추가
            binding.selectCategoryTl.addTab(tab)
        }

        // "전체" 카테고리로 데이터를 불러옴
        filterBusinessItems("전체")

        businessViewModel.businessList.observe(viewLifecycleOwner) { list ->
            filterBusinessItems(binding.selectCategoryTl.getTabAt(0)?.text?.toString() ?: "전체")
        }

        // 기본적으로 첫 번째 탭 "전체"가 선택되도록 설정
        binding.selectCategoryTl.selectTab(binding.selectCategoryTl.getTabAt(0))



        // 탭 선택 리스너 추가
        binding.selectCategoryTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedCategory = tab?.text.toString()
                filterBusinessItems(selectedCategory) // 선택된 카테고리 필터링
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // TabLayout의 탭에 마진 추가
        tabItemMargin(binding.selectCategoryTl)
    }

    // 비즈니스 아이템을 선택된 카테고리로 필터링하는 함수
    private fun filterBusinessItems(category: String) {
        val validCategories = resources.getStringArray(R.array.CATEGORY).toList()
        val filteredList = when {
            category == "전체" -> businessViewModel.businessList.value
            else -> businessViewModel.businessList.value?.filter { item ->
                when {
                    item.category == category -> true
                    category == "기타" && item.category !in validCategories -> true
                    else -> false
                }
            }
        }
        businessAdapter = BusinessAdapter(filteredList ?: emptyList(), this)
        binding.businessView.adapter = businessAdapter
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