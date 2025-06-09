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
import com.example.gaechuck.ui.util.DeleteDialogFragment

class RentMainFragment : Fragment(R.layout.fragment_rent_main),RentAdapter.OnRentItemClickListener {
    private lateinit var binding: FragmentRentMainBinding
    private lateinit var rentViewModel: RentViewModel
    private lateinit var rentAdapter: RentAdapter
    private var originalList: List<RentList> = listOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var urlButton : Button

    private var isSearchMode = false

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
        urlButton = view.findViewById(R.id.url_btn)

        // RecyclerView 설정 (어댑터 설정 *이전*)
        recyclerView = view.findViewById(R.id.rent_view)
        binding.rentView.layoutManager = LinearLayoutManager(context)
        rentAdapter = RentAdapter(object : RentAdapter.OnRentItemClickListener {
            override fun OnRentItemClick(item: RentList) {
                val action = RentMainFragmentDirections.actionRentMainFragmentToRentDetailFragment(item.rentItemId)
                view?.findNavController()?.navigate(action)
            }

            override fun onEditClicked(item: RentList) {
                val intent = Intent(requireContext(), RentEditActivity::class.java).apply {
                    putExtra("rentItemId", item.rentItemId)
                    putExtra("rentItemName", item.rentItemName)
                    putExtra("rentItemCount", item.rentItemCount)
                    putStringArrayListExtra("rentItemImage", arrayListOf(item.image))
                }
                startActivity(intent)
            }

            override fun onDeleteClicked(item: RentList) {
                val dialog = DeleteDialogFragment(requireContext()) {
                    rentViewModel.deleteData(item.rentItemId)
                }
                dialog.show()
            }
        })
        binding.rentView.adapter = rentAdapter

        // 로그인 상태 확인
        rentViewModel.checkLoginStatus()
        rentViewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            binding.writeBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
            rentAdapter.updateLoginState(isLoggedIn) // 이 줄 추가
        })

        // RentActivity의 Toolbar 업데이트
        (activity as? RentActivity)?.updateToolbar(
            title = getString(R.string.bar_rent), // 제목 설정
            showBackButton = true, // 뒤로가기 버튼 표시
            showHomeButton = false, // 홈 버튼 표시
            showEtcButton = false,
            showSearchButton = true,
        )



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

        rentViewModel.filterRentList.observe(viewLifecycleOwner, Observer { filteredItems ->
            Log.d("RentMainFragment", "Filtered LiveData observed: ${filteredItems.size} items")
            rentAdapter.submitList(filteredItems.toList())
        })

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

        rentViewModel.RentDetailRetrofit("렌트")

    }

    // 렌트 아이템 클릭 시 네비게이션 처리
    override fun OnRentItemClick(item: RentList) {
        val action = RentMainFragmentDirections.actionRentMainFragmentToRentDetailFragment(item.rentItemId)
        view?.findNavController()?.navigate(action)
    }

    override fun onEditClicked(item: RentList) {
        val intent = Intent(requireContext(), RentEditActivity::class.java).apply {
            putExtra("rentItemId", item.rentItemId)
            putExtra("rentItemName", item.rentItemName)
            putExtra("rentItemCount", item.rentItemCount)
            putStringArrayListExtra("rentItemImage", arrayListOf(item.image)) // List<String>이라면
        }
        startActivity(intent)
    }

    override fun onDeleteClicked(item: RentList) {
        val dialog = DeleteDialogFragment(requireContext()) {
            rentViewModel.deleteData(item.rentItemId)
        }
        dialog.show()
    }

}