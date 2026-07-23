package com.gaechuck_package.gaechuck.ui.noticecouncil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gaechuck_package.gaechuck.ui.noticeuniv.NoticeUnivActivity

class NoticeCouncilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, NoticeUnivActivity::class.java).putExtra("mode", "COUNCIL"))
        finish()
    }
}
