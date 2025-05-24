package com.example.gaechuck.ui.util

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gaechuck.R
import com.example.gaechuck.repository.LoseRepository
import com.example.gaechuck.repository.RentRepository
import com.example.gaechuck.ui.business.BusinessSearchActivity
import com.example.gaechuck.ui.lose.LoseSearchActivity
import com.example.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.example.gaechuck.ui.rent.RentSearchActivity
import com.example.gaechuck.ui.rent.viewmodel.RentViewModel

class SearchFailFragment : Fragment(R.layout.fragment_search_fail){
    private lateinit var rentViewModel: RentViewModel
    private lateinit var loseViewModel: LoseViewModel
    private lateinit var button : Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button = view.findViewById(R.id.search_call)

        // 호출한 액티비티가 BusinessSearchActivity인 경우 버튼 숨기기
        if (activity is BusinessSearchActivity) {
            button.visibility = View.GONE
        }

        Log.d("SearchFailFragment", "activity = ${activity?.javaClass?.simpleName}")

        // 문의하기 버튼 클릭 처리 (예: 전화 연결 등)
        if (activity is RentSearchActivity) {
            val repository = RentRepository()
            val factory = RentViewModel.RentViewModelFactory(repository)
            rentViewModel = ViewModelProvider(requireActivity(), factory)[RentViewModel::class.java]
            rentViewModel.RentDetailRetrofit("렌트")

            rentViewModel.rentUrl.observe(viewLifecycleOwner) { url ->
                button.setOnClickListener {
                    Log.d("SearchFailFragment", "💥 버튼 클릭됨")
                    Toast.makeText(requireContext(), "버튼 눌림!", Toast.LENGTH_SHORT).show()
                    if (!url.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } else {
                        Log.e("RentDetailFragment", "URL is empty or null")
                    }
                }
            }
        } else if (activity is LoseSearchActivity) {
            val repository = LoseRepository()
            val factory = LoseViewModel.LoseViewModelFactory(repository)
            loseViewModel = ViewModelProvider(requireActivity(), factory)[LoseViewModel::class.java]
            loseViewModel.LoseDetailRetrofit("분실물")

            loseViewModel.loseUrl.observe(viewLifecycleOwner) { url ->
                Log.d("SearchFailFragment", "Opening URL: $url")
                button.setOnClickListener {
                    if (!url.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } else {
                        Log.e("LoseDetailFragment", "URL is empty or null")
                    }
                }
            }
        }
    }
}