package com.gaechuck_package.gaechuck.ui.lose

import android.content.Intent
import android.os.Bundle
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
import com.gaechuck_package.gaechuck.repository.LoseRepository
import com.gaechuck_package.gaechuck.ui.lose.viewmodel.LoseViewModel
import com.gaechuck_package.gaechuck.ui.util.DeleteDialogFragment

class LoseActivity : AppCompatActivity(R.layout.activity_lose) {

    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var homeButton: ImageView
    private lateinit var etcButton: ImageView
    private lateinit var writeButton: TextView
    private lateinit var loseViewModel: LoseViewModel

    private var lostItemId: Int = -1
    private var title: String = ""
    private var lostDate: String = ""
    private var description: String = ""
    private var lostLocation: String = ""
    private var images: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbar_main)
        titleTextView = toolbar.findViewById(R.id.textView_title)
        backButton = toolbar.findViewById(R.id.button_back)
        homeButton = toolbar.findViewById(R.id.button_home)
        etcButton = toolbar.findViewById(R.id.button_etc)
        writeButton = toolbar.findViewById(R.id.button_write)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        loseViewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        writeButton.setOnClickListener {
            val intent = Intent(this, LoseWriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val fromSearch = intent.getBooleanExtra("startFromSearch", false)
        val lostItemIdExtra = intent.getIntExtra("lostItemId", -1)

        if (fromSearch && lostItemIdExtra != -1) {
            val bundle = Bundle().apply { putInt("lostItemId", lostItemIdExtra) }
            navController.navigate(R.id.loseDetailFragment, bundle, null, null)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestinationId = navController.currentDestination?.id
                if (fromSearch) { finish(); return }
                when (currentDestinationId) {
                    R.id.loseMainFragment -> navigateToMain()
                    R.id.loseDetailFragment -> navController.navigate(R.id.action_loseDetailFragment_to_loseMainFragment)
                    else -> { if (!navController.popBackStack()) finish() }
                }
            }
        })

        backButton.setOnClickListener {
            val currentDestinationId = navController.currentDestination?.id
            if (fromSearch) { finish(); return@setOnClickListener }
            when (currentDestinationId) {
                R.id.loseMainFragment -> navigateToMain()
                R.id.loseDetailFragment -> navController.navigate(R.id.action_loseDetailFragment_to_loseMainFragment)
                else -> { if (!navController.popBackStack()) finish() }
            }
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        etcButton.setOnClickListener {
            val themeWrapper = ContextThemeWrapper(this, R.style.PopupMenuStyle)
            val popupMenu = PopupMenu(themeWrapper, etcButton, Gravity.END, 0, R.style.PopupMenuStyle)
            popupMenu.menuInflater.inflate(R.menu.etc_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> { startEditActivity(); true }
                    R.id.menu_delete -> { showDeleteConfirmationDialog(); true }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this, R.anim.slide_in_left, R.anim.slide_out_right
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    fun setLoseItemData(
        lostItemId: Int, title: String, lostDate: String,
        lostLocation: String, description: String, images: ArrayList<String>
    ) {
        this.lostItemId = lostItemId
        this.title = title
        this.lostDate = lostDate
        this.lostLocation = lostLocation
        this.description = description
        this.images = images
    }

    private fun startEditActivity() {
        val intent = Intent(this, LoseEditActivity::class.java).apply {
            putExtra("lostItemId", lostItemId)
            putExtra("title", title)
            putExtra("lostDate", lostDate)
            putExtra("lostLocation", lostLocation)
            putExtra("description", description)
            putStringArrayListExtra("images", images)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog() {
        val deleteDialog = DeleteDialogFragment(this) { deleteLoseItem(lostItemId) }
        deleteDialog.show()
    }

    private fun deleteLoseItem(loseItemId: Int) {
        loseViewModel.deleteData(loseItemId)
        loseViewModel.deleteData.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "삭제 완료.", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_loseDetailFragment_to_loseMainFragment)
            }.onFailure { error ->
                Toast.makeText(this, "삭제 실패 : ${error.message}", Toast.LENGTH_SHORT).show()
            }
            loseViewModel.deleteData.removeObservers(this)
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

    fun updateToolbar(
        title: String,
        showBackButton: Boolean,
        showHomeButton: Boolean,
        showEtcButton: Boolean,
        showSearchButton: Boolean = false,
        showWriteButton: Boolean = false
    ) {
        titleTextView.text = title
        backButton.visibility = if (showBackButton) View.VISIBLE else View.GONE
        homeButton.visibility = if (showHomeButton) View.VISIBLE else View.GONE
        etcButton.visibility = if (showEtcButton) View.VISIBLE else View.GONE
        writeButton.visibility = if (showWriteButton) View.VISIBLE else View.GONE
    }

    fun setLostItemId(id: Int) { lostItemId = id }
    fun getLostItemId(): Int = lostItemId
}
