package com.example.gaechuck.ui.rent

import VerticalItemDecorate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gaechuck.R
import com.example.gaechuck.data.model.RentItem
import com.example.gaechuck.databinding.FragmentRentMainBinding
import com.example.gaechuck.ui.rent.adapter.RentAdapter
import com.example.gaechuck.ui.rent.viewmodel.RentViewModel

class RentMainFragment : Fragment(R.layout.fragment_rent_main),RentAdapter.OnRentItemClickListener {
    private lateinit var binding: FragmentRentMainBinding
    private val rentViewModel: RentViewModel by viewModels()
    private lateinit var rentAdapter: RentAdapter

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

        // RentActivity의 Toolbar 업데이트
        (activity as? RentActivity)?.updateToolbar(
            title = getString(R.string.bar_rent), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false // 홈 버튼 표시
        )
//        initView()

        // RecyclerView 설정
        binding.rentView.layoutManager = LinearLayoutManager(context)

        // Adapter 설정
        rentAdapter = RentAdapter(this)
        binding.rentView.adapter = rentAdapter

        // VerticalItemDecorate 추가
        val itemDecoration = VerticalItemDecorate(20)
        binding.rentView.addItemDecoration(itemDecoration)

        // 데이터 가져오는 함수 호출
        ShowRentItems()
    }

//    private fun initView() {
//        binding.toolbar.run {
//            buttonBack.setOnClickListener {
//                findNavController().navigateUp()
//            }
//            textViewTitle.text = "물품 대여"
//        }
//    }

    // 데이터 가져오는 함수
    private fun ShowRentItems() {
        rentViewModel.rentList.observe(viewLifecycleOwner) { rentItems ->

            rentAdapter.submitList(rentItems.toList())  // 새로운 리스트 객체 생성
        }
    }

    // 렌트 아이템 클릭 시 네비게이션 처리
    override fun OnRentItemClick(item: RentItem) {
        val action = RentMainFragmentDirections.actionRentMainFragmentToRentDetailFragment(item)
        view?.findNavController()?.navigate(action)
    }


}