package com.gaechuck_package.gaechuck.ui.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.databinding.LayoutBottomNavBinding
import com.gaechuck_package.gaechuck.ui.login.LoginActivity
import com.gaechuck_package.gaechuck.ui.mypage.MyPageActivity
import com.gaechuck_package.gaechuck.ui.noticeuniv.NoticeUnivActivity
import com.gaechuck_package.gaechuck.ui.setting.SettingActivity

enum class BottomNavTab { HOME, NOTICE, MY, SETTING }

object BottomNavHelper {

    fun bind(nav: LayoutBottomNavBinding, activity: AppCompatActivity, active: BottomNavTab) {
        val activeColor = ContextCompat.getColor(activity, R.color.brand_2)
        val inactiveColor = ContextCompat.getColor(activity, R.color.info_grey)

        fun style(iconId: Int, labelId: Int, tab: BottomNavTab) {
            val icon = nav.root.findViewById<android.widget.ImageView>(iconId)
            val label = nav.root.findViewById<android.widget.TextView>(labelId)
            val isActive = tab == active
            icon.setColorFilter(if (isActive) activeColor else inactiveColor)
            label.setTextColor(if (isActive) activeColor else inactiveColor)
            label.setTypeface(
                ResourcesCompat.getFont(
                    activity,
                    if (isActive) R.font.pretendard_bold else R.font.pretendard_medium
                )
            )
        }

        style(R.id.nav_home_icon, R.id.nav_home_label, BottomNavTab.HOME)
        style(R.id.nav_notice_icon, R.id.nav_notice_label, BottomNavTab.NOTICE)
        style(R.id.nav_my_icon, R.id.nav_my_label, BottomNavTab.MY)
        style(R.id.nav_setting_icon, R.id.nav_setting_label, BottomNavTab.SETTING)

        nav.navHome.setOnClickListener {
            if (active != BottomNavTab.HOME) {
                val intent = Intent(activity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                activity.startActivity(intent)
                activity.finish()
            }
        }
        nav.navNotice.setOnClickListener {
            if (active != BottomNavTab.NOTICE) {
                activity.startActivity(Intent(activity, NoticeUnivActivity::class.java))
            }
        }
        nav.navMy.setOnClickListener {
            if (active != BottomNavTab.MY) {
                val target = if (!AuthManager.getToken().isNullOrEmpty()) {
                    MyPageActivity::class.java
                } else {
                    LoginActivity::class.java
                }
                activity.startActivity(Intent(activity, target))
            }
        }
        nav.navSetting.setOnClickListener {
            if (active != BottomNavTab.SETTING) {
                activity.startActivity(Intent(activity, SettingActivity::class.java))
            }
        }
    }
}
