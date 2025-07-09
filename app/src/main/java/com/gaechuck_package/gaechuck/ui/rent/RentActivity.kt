package com.gaechuck_package.gaechuck.ui.rent

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
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
import com.gaechuck_package.gaechuck.repository.RentRepository
import com.gaechuck_package.gaechuck.ui.rent.viewmodel.RentViewModel
import com.gaechuck_package.gaechuck.ui.util.DeleteDialogFragment

class RentActivity : AppCompatActivity(R.layout.activity_rent) {

    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var homeButton: ImageView
    private lateinit var etcButton : ImageView
    private lateinit var searchButton : ImageView
    private lateinit var rentViewModel : RentViewModel

    private var rentItemId: Int = -1
    private var rentItemName: String = ""
    private var rentItemCount: Int = 0
    private var rentItemImage: ArrayList<String> = arrayListOf()

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
        val repository = RentRepository()
        val viewModelFactory = RentViewModel.RentViewModelFactory(repository)
        rentViewModel = ViewModelProvider(this, viewModelFactory).get(RentViewModel::class.java)

        // NavHostFragment에서 NavController 가져오기
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        searchButton.setOnClickListener{
            val intent = Intent(this, RentSearchActivity::class.java)
            startActivity(intent)
        }
        // 검색에서 디테일로 바로 이동
        val fromSearch = intent.getBooleanExtra("startFromSearch", false)
        val rentItemId = intent.getIntExtra("rentItemId", -1)

        // ② 검색에서 왔다면, 바로 DetailFragment로 이동
        if (fromSearch && rentItemId != -1) {
            val bundle = Bundle().apply {
                putInt("rentItemId", rentItemId)
            }
            navController.navigate(
                R.id.rentDetailFragment,
                bundle,
                null,
                null
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed(){
                val currentDestinationId = navController.currentDestination?.id

                if (fromSearch) {
                    finish()
                    return
                }

                when (currentDestinationId) {
                    R.id.rentMainFragment -> {
                        val intent = Intent(this@RentActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            this@RentActivity,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    }

                    R.id.rentDetailFragment -> {
                        navController.navigate(R.id.action_rentDetailFragment_to_rentMainFragment)
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
                finish()
            }
            when (currentDestinationId) {
                R.id.rentMainFragment -> {
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
                R.id.rentDetailFragment -> {
                    navController.navigate(R.id.action_rentDetailFragment_to_rentMainFragment)
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

            // 삭제하기 항목만 색상 변경
            val deleteMenuItem = popupMenu.menu.findItem(R.id.menu_delete)
            val redTitle = SpannableString(deleteMenuItem.title)
            redTitle.setSpan(ForegroundColorSpan(Color.RED), 0, redTitle.length, 0)
            deleteMenuItem.title = redTitle

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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val view = currentFocus
        if (view is EditText) {
            val outRect = Rect()
            view.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                // EditText 외부를 클릭하면 키보드 숨기기
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun showDeleteConfirmationDialog() {
        val rentItemId = getRentItemId()

        val deleteDialog = DeleteDialogFragment(this) {
            deleteRentItem(rentItemId) // 삭제 로직 실행
        }

        deleteDialog.show()
    }

    private fun deleteRentItem(rentItemId: Int) {
//        val token = "Bearer ${com.example.gaechuck.api.AuthManager.getToken()}"
        rentViewModel.deleteData(rentItemId)
        // 삭제 작업의 결과를 관찰
        rentViewModel.deleteResult.observe(this) { result ->
            result.onSuccess { response ->
                // 삭제 성공 시 LoseMainFragment로 이동
                Toast.makeText(this, "삭제 완료.", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_rentDetailFragment_to_rentMainFragment)
            }.onFailure { error ->
                // 삭제 실패 시 사용자에게 알림
                Toast.makeText(this, "삭제 실패 : ${error.message}", Toast.LENGTH_SHORT).show()
            }
            // 옵저버 제거 (메모리 누수 방지)
            rentViewModel.deleteResult.removeObservers(this)
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
        searchButton.visibility = if (showSearchButton) View.VISIBLE else View.GONE
    }

    fun setRentItemId(id: Int) {
        rentItemId = id
    }

    fun getRentItemId(): Int {
        return rentItemId
    }

    fun setRentItemData(rentItemId: Int, rentItemName: String, rentItemCount: Int, rentItemImage: ArrayList<String>) {
        this.rentItemId = rentItemId
        this.rentItemName = rentItemName
        this.rentItemCount = rentItemCount
        this.rentItemImage = rentItemImage
    }

    private fun startEditActivity() {
        val intent = Intent(this, RentEditActivity::class.java).apply {
            putExtra("rentItemId", rentItemId)
            putExtra("rentItemName", rentItemName)
            putExtra("rentItemCount", rentItemCount)
            putStringArrayListExtra("rentItemImage", rentItemImage)
        }
        startActivity(intent)
    }
}
