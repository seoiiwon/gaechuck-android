package com.gaechuck_package.gaechuck

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.databinding.ActivityMainBinding
import com.gaechuck_package.gaechuck.repository.CafeteriaMenuRepository
import com.gaechuck_package.gaechuck.ui.auth.AuthActivity
import com.gaechuck_package.gaechuck.ui.bus.BusRouteActivity
import com.gaechuck_package.gaechuck.ui.business.BusinessActivity
import com.gaechuck_package.gaechuck.ui.club.ClubActivity
import com.gaechuck_package.gaechuck.ui.login.LoginActivity
import com.gaechuck_package.gaechuck.ui.lose.LoseActivity
import com.gaechuck_package.gaechuck.ui.menu.CafeteriaMenuActivity
import com.gaechuck_package.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModel
import com.gaechuck_package.gaechuck.ui.menu.viewmodel.CafeteriaMenuViewModelFactory
import com.gaechuck_package.gaechuck.ui.mypage.MyPageActivity
import com.gaechuck_package.gaechuck.ui.noticecouncil.NoticeCouncilActivity
import com.gaechuck_package.gaechuck.ui.noticeuniv.NoticeUnivActivity
import com.gaechuck_package.gaechuck.ui.rent.RentActivity
import com.gaechuck_package.gaechuck.ui.setting.SettingActivity
import com.gaechuck_package.gaechuck.ui.util.DialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val menuViewModel: CafeteriaMenuViewModel by viewModels {
        CafeteriaMenuViewModelFactory(CafeteriaMenuRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getBooleanExtra("refreshNotice", false)) {
            startActivity(Intent(this, NoticeCouncilActivity::class.java))
            finish()
            return
        }

        applyWindowInsets()
        setupMenuItems()
        setupTopBar()
        setupBottomNav()
        setupQuickInfo()
    }

    // 시스템 제스처 내비게이션 바 높이만큼 하단 내비게이션 패딩 적용
    private fun applyWindowInsets() {
        val baseBottomPadding = binding.bottomNav.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { view, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = baseBottomPadding + navBarHeight)
            insets
        }
    }

    private fun setupMenuItems() {
        binding.menuFood.setOnClickListener {
            startActivity(Intent(this, CafeteriaMenuActivity::class.java))
        }
        binding.menuCampusMap.setOnClickListener {
            Toast.makeText(this, "준비 중입니다", Toast.LENGTH_SHORT).show()
        }
        binding.menuRent.setOnClickListener {
            startActivity(Intent(this, RentActivity::class.java))
        }
        binding.menuNotice.setOnClickListener {
            startActivity(Intent(this, NoticeUnivActivity::class.java))
        }
        binding.menuBusiness.setOnClickListener {
            startActivity(Intent(this, BusinessActivity::class.java))
        }
        binding.menuBus.setOnClickListener {
            startActivity(Intent(this, BusRouteActivity::class.java))
        }
        binding.menuLose.setOnClickListener {
            startActivity(Intent(this, LoseActivity::class.java))
        }
        binding.menuClub.setOnClickListener {
            startActivity(Intent(this, ClubActivity::class.java))
        }
        binding.btnMore.setOnClickListener {
            startActivity(Intent(this, CafeteriaMenuActivity::class.java))
        }
    }

    private fun setupTopBar() {
        binding.loginArea.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        val dialogFragment = DialogFragment(this)
        binding.alarmBtn.setOnClickListener {
            dialogFragment.show()
        }
    }

    private fun setupBottomNav() {
        binding.navNotice.setOnClickListener {
            startActivity(Intent(this, NoticeUnivActivity::class.java))
        }
        binding.navMy.setOnClickListener {
            val target = if (!AuthManager.getToken().isNullOrEmpty()) {
                MyPageActivity::class.java
            } else {
                LoginActivity::class.java
            }
            startActivity(Intent(this, target))
        }
        binding.navSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun setupQuickInfo() {
        menuViewModel.menuList.observe(this) { list ->
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val breakfastItems = list.filter { item ->
                item.date == today && item.menuDivision.replace(" ", "").contains("천원")
            }
            val menuText = breakfastItems
                .flatMap { item ->
                    item.menu
                        .split("/", "\n")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                }
                .distinct()
                .take(6)
                .joinToString("\n")
            binding.cafeteriaMenuText.text = menuText.ifBlank { "오늘의 메뉴 정보가 없습니다" }
        }
        menuViewModel.loadMenu(2)
    }
}
