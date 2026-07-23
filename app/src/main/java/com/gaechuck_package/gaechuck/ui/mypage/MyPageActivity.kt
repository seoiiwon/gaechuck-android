package com.gaechuck_package.gaechuck.ui.mypage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gaechuck_package.gaechuck.databinding.ActivityMypageBinding
import com.gaechuck_package.gaechuck.repository.MyPageRepository
import com.gaechuck_package.gaechuck.ui.mypage.viewmodel.MyPageViewModel
import com.gaechuck_package.gaechuck.ui.util.BottomNavHelper
import com.gaechuck_package.gaechuck.ui.util.BottomNavTab

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private lateinit var viewModel: MyPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = MyPageViewModel.MyPageViewModelFactory(MyPageRepository.getInstance())
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]

        BottomNavHelper.bind(binding.bottomNavInclude, this, BottomNavTab.MY)

        binding.buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.profileEditCard.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }

        binding.logoutCard.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, com.gaechuck_package.gaechuck.MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }

        viewModel.profile.observe(this) { profile ->
            binding.nicknameText.text = profile.nickname
            binding.deptGradeText.text = "${profile.department} · ${profile.grade}"
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfile()
    }
}
