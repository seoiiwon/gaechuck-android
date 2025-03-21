package com.example.gaechuck.ui.business

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.repository.BusinessRepository
import com.example.gaechuck.ui.business.viewmodel.BusinessViewModel
import com.example.gaechuck.ui.lose.LoseEditActivity

class BusinessActivity : AppCompatActivity(R.layout.activity_business) {

    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var homeButton: ImageView
    private lateinit var etcButton: ImageView
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

        // 뒤로가기 버튼 동작 설정
        backButton.setOnClickListener {
            if (navController.currentDestination?.id == R.id.businessMainFragment) {
                // MainFragment에서 뒤로가기 버튼을 눌렀을 때는 MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish() // LoseActivity 종료
            } else if (!navController.popBackStack()) {
                finish() // 다른 경우엔 Activity 종료
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
        val dialogView = layoutInflater.inflate(R.layout.alert_detail_popup, null)

        // 커스텀 다이얼로그 생성
        val dialog = AlertDialog.Builder(this)
            .setTitle("삭제 확인")
            .setMessage("정말 삭제하시겠습니까?")
            .setView(dialogView) // 커스텀 레이아웃 설정
            .create()

        // 버튼 동작 설정
        val positiveButton = dialogView.findViewById<Button>(R.id.dialog_yes_btn)
        val negativeButton = dialogView.findViewById<Button>(R.id.dialog_no_btn)

        positiveButton.setOnClickListener {
            // 확인 버튼 클릭 시 삭제 처리
             deleteBusinessItem(coalitionId)
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(R.drawable.custom_popup_background)
        dialog.show()
    }

    private fun deleteBusinessItem(coalitionId: Int) {
        val token = "Bearer ${com.example.gaechuck.api.AuthManager.getToken()}"
        businessViewModel.deleteData(token, coalitionId)
        // 삭제 작업의 결과를 관찰
        businessViewModel.deleteResult.observe(this) { result ->
            result.onSuccess { response ->
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
    fun updateToolbar(title: String, showBackButton: Boolean, showHomeButton: Boolean, showEtcButton:Boolean) {
        titleTextView.text = title
        backButton.visibility = if (showBackButton) View.VISIBLE else View.GONE
        homeButton.visibility = if (showHomeButton) View.VISIBLE else View.GONE
        etcButton.visibility = if (showEtcButton) View.VISIBLE else View.GONE

    }

    fun setCoalitionItemId(id: Int) {
        coalitionId = id
    }

    fun getCoalitionItemId(): Int {
        return coalitionId
    }
}
