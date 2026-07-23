package com.gaechuck_package.gaechuck.ui.club

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R

class ClubActivity : AppCompatActivity(R.layout.activity_club) {

    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var titleTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var favoriteButton: ImageView
    private lateinit var shareButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbar_main)
        titleTextView = toolbar.findViewById(R.id.textView_title)
        backButton = toolbar.findViewById(R.id.button_back)
        favoriteButton = toolbar.findViewById(R.id.button_favorite)
        shareButton = toolbar.findViewById(R.id.button_share)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })
        backButton.setOnClickListener { handleBack() }
    }

    private fun handleBack() {
        when (navController.currentDestination?.id) {
            R.id.clubMainFragment -> navigateToMain()
            else -> if (!navController.popBackStack()) navigateToMain()
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

    fun setToolbarTitle(title: String) {
        titleTextView.text = title
    }

    fun setFavoriteButtonVisible(visible: Boolean, isFilled: Boolean = false, onClick: (() -> Unit)?) {
        favoriteButton.visibility = if (visible) View.VISIBLE else View.GONE
        favoriteButton.setImageResource(if (isFilled) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)
        favoriteButton.setOnClickListener { onClick?.invoke() }
    }

    fun setShareButtonVisible(visible: Boolean, onClick: (() -> Unit)?) {
        shareButton.visibility = if (visible) View.VISIBLE else View.GONE
        shareButton.setOnClickListener { onClick?.invoke() }
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()
}
