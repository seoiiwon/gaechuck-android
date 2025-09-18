package com.gaechuck_package.gaechuck.ui.business

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.repository.BusinessRepository
import com.gaechuck_package.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.gaechuck_package.gaechuck.ui.util.DeleteDialogFragment

class BusinessActivity : AppCompatActivity(R.layout.activity_business) {

    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var homeButton: ImageView
    private lateinit var etcButton: ImageView
    private lateinit var searchButton : ImageView
    private lateinit var businessViewModel : BusinessViewModel

    private var coalitionId: Int = -1
    private var coalitionName: String = ""
    private var benefit: String = ""
    private var category: String = ""
    private var images: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Toolbar 설정
        toolbar = findViewById(R.id.toolbar_main)
        titleTextView = toolbar.findViewById(R.id.textView_title)
        backButton = toolbar.findViewById(R.id.button_back)
        homeButton = toolbar.findViewById(R.id.button_home)
        etcButton = toolbar.findViewById(R.id.button_etc)
        searchButton = toolbar.findViewById(R.id.button_search)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        // viewmodel 설정
        val repository = BusinessRepository()
        val viewModelFactory = BusinessViewModel.BusinessViewModelFactory(repository)
        businessViewModel = ViewModelProvider(this, viewModelFactory).get(BusinessViewModel::class.java)

        // NavHostFragment에서 NavController 가져오기
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        searchButton.setOnClickListener{
            val intent = Intent(this, BusinessSearchActivity::class.java)
            startActivity(intent)
        }

        // 검색에서 디테일로 바로 이동
        val fromSearch = intent.getBooleanExtra("startFromSearch", false)
        val coalitionId = intent.getIntExtra("coalitionId", -1)

        Log.e("BusinessActivity", "${fromSearch}, ${coalitionId}")

        // ② 검색에서 왔다면, 바로 DetailFragment로 이동
        if (fromSearch && coalitionId != -1) {
            val bundle = Bundle().apply {
                putInt("coalitionId", coalitionId)
            }
            navController.navigate(
                R.id.businessDetailFragment,
                bundle,
                null,
                null
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestinationId = navController.currentDestination?.id

                if (fromSearch) {
                    finish()
                    return
                }

                when (currentDestinationId) {
                    R.id.businessMainFragment -> {
                        val intent = Intent(this@BusinessActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            this@BusinessActivity,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    }

                    R.id.businessDetailFragment -> {
                        navController.navigate(R.id.action_businessDetailFragment_to_businessMainFragment)
                    }

                    else -> {
                        if (!navController.popBackStack()) {
                            finish()
                        }
                    }
                }
            }
        })

        // 뒤로가기 버튼 동작 설정
        backButton.setOnClickListener {
            val currentDestinationId = navController.currentDestination?.id

            if (fromSearch) {
                finish() // BusinessActivity 종료 → SearchActivity로 돌아감
            }
            when (currentDestinationId) {
                R.id.businessMainFragment -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    val options = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    startActivity(intent, options.toBundle())
                    finish()
                }
                R.id.businessDetailFragment -> {
                    navController.navigate(R.id.action_businessDetailFragment_to_businessMainFragment)
                }
                else -> {
                    if (!navController.popBackStack()) {
                        finish()
                    }
                }
            }
        }

        // 홈 버튼 동작 설정: MainActivity로 이동
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // RentActivity 종료
        }

        etcButton.setOnClickListener{
            // 팝업 메뉴를 생성하여 '수정'과 '삭제' 옵션 추가
            val themeWrapper = ContextThemeWrapper(this , R.style.PopupMenuStyle)
            val popupMenu = PopupMenu(themeWrapper , etcButton, Gravity.END , 0 , R.style.PopupMenuStyle)
            val menuInflater = popupMenu.menuInflater
            menuInflater.inflate(R.menu.etc_menu, popupMenu.menu)

            // 메뉴 항목 클릭 리스너
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        // 수정 로직
                        startEditActivity()
                        true
                    }
                    R.id.menu_delete -> {
                        // 삭제 확인 다이얼로그
                        showDeleteConfirmationDialog()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()

        }

    }

    fun setLoseItemData(coalitionId: Int, coalitionName: String, benefit: String,category:String, images: ArrayList<String>) {
        this.coalitionId = coalitionId
        this.coalitionName = coalitionName
        this.benefit = benefit
        this.category = category
        this.images = images

    }

    private fun startEditActivity() {
        val intent = Intent(this, BusinessEditActivity::class.java).apply {
            putExtra("coalitionId", coalitionId)
            putExtra("coalitionName", coalitionName)
            putExtra("benefit", benefit)
            putExtra("category", category)
            putStringArrayListExtra("images", images)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog() {
        val coalitionId = getCoalitionItemId()

        val deleteDialog = DeleteDialogFragment(this) {
            deleteBusinessItem(coalitionId) // 삭제 로직 실행
        }

        deleteDialog.show()
    }

    private fun deleteBusinessItem(coalitionId: Int) {
//        val token = "Bearer ${com.example.gaechuck.api.AuthManager.getToken()}"
        businessViewModel.deleteData(coalitionId)
        // 삭제 작업의 결과를 관찰
        businessViewModel.deleteResult.observe(this) { result ->
            result.onSuccess {
                // 삭제 성공 시 LoseMainFragment로 이동
                Toast.makeText(this, "삭제 완료.", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_businessDetailFragment_to_businessMainFragment)
            }.onFailure { error ->
                // 삭제 실패 시 사용자에게 알림
                Toast.makeText(this, "삭제 실패 : ${error.message}", Toast.LENGTH_SHORT).show()
            }
            // 옵저버 제거 (메모리 누수 방지)
            businessViewModel.deleteResult.removeObservers(this)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    fun updateToolbar(title: String, showBackButton: Boolean, showHomeButton: Boolean, showEtcButton:Boolean, showSearchButton:Boolean) {
        titleTextView.text = title
        backButton.visibility = if (showBackButton) View.VISIBLE else View.GONE
        homeButton.visibility = if (showHomeButton) View.VISIBLE else View.GONE
        etcButton.visibility = if (showEtcButton) View.VISIBLE else View.GONE
        searchButton.visibility = if(showSearchButton) View.VISIBLE else View.GONE

    }

    fun setCoalitionItemId(id: Int) {
        coalitionId = id
    }

    fun getCoalitionItemId(): Int {
        return coalitionId
    }
}
