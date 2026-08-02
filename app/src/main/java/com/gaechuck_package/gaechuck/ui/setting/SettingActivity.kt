package com.gaechuck_package.gaechuck.ui.setting

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.api.AuthManager
import com.gaechuck_package.gaechuck.databinding.ActivitySettingBinding
import com.gaechuck_package.gaechuck.databinding.RowSettingToggleBinding
import com.gaechuck_package.gaechuck.repository.SettingRepository
import com.gaechuck_package.gaechuck.ui.setting.viewmodel.SettingViewModel
import com.gaechuck_package.gaechuck.ui.termsofuse.TermsOfUseActivity
import com.gaechuck_package.gaechuck.ui.util.BottomNavHelper
import com.gaechuck_package.gaechuck.ui.util.BottomNavTab

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var viewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = SettingRepository(this)
        val factory = SettingViewModel.SettingViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]

        BottomNavHelper.bind(binding.bottomNavInclude, this, BottomNavTab.SETTING)

        binding.buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        bindToggleRow(
            binding.toggleCouncilNotice, "총학생회 공지", "총학생회 실시간 공지 알람을 받습니다",
            SettingRepository.KEY_COUNCIL_NOTICE
        )
        bindToggleRow(
            binding.toggleUnivNotice, "교내 공지", "교내 실시간 공지 알람을 받습니다",
            SettingRepository.KEY_UNIV_NOTICE
        )
        bindDarkModeRow(binding.toggleDarkMode)

        bindQuickInfoRow(binding.toggleGajwaStaff, "교직원 식당", SettingRepository.KEY_GAJWA_STAFF)
        bindQuickInfoRow(binding.toggleGajwaCulture, "교육문화센터식당", SettingRepository.KEY_GAJWA_CULTURE)
        bindQuickInfoRow(binding.toggleGajwaCentral, "중앙식당", SettingRepository.KEY_GAJWA_CENTRAL)
        bindQuickInfoRow(binding.toggleChiramStaff, "교직원 식당", SettingRepository.KEY_CHIRAM_STAFF)
        bindQuickInfoRow(binding.toggleChiramCentral, "중앙식당", SettingRepository.KEY_CHIRAM_CENTRAL)

        binding.termsRow.root.setOnClickListener {
            startActivity(Intent(this, TermsOfUseActivity::class.java))
        }

        binding.withdrawRow.setOnClickListener { showWithdrawConfirmDialog() }
    }

    private fun bindToggleRow(row: RowSettingToggleBinding, label: String, description: String?, key: String) {
        row.rowLabel.text = label
        if (description != null) {
            row.rowDescription.text = description
            row.rowDescription.visibility = android.view.View.VISIBLE
        }
        row.rowSwitch.isChecked = viewModel.isEnabled(key)
        row.rowSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEnabled(key, isChecked)
        }
    }

    private fun bindDarkModeRow(row: RowSettingToggleBinding) {
        row.rowLabel.text = "다크모드"
        row.rowSwitch.isChecked = viewModel.isEnabled(SettingRepository.KEY_DARK_MODE)
        row.rowSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEnabled(SettingRepository.KEY_DARK_MODE, isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun bindQuickInfoRow(row: RowSettingToggleBinding, label: String, key: String) {
        row.rowLabel.text = label
        row.rowSwitch.isChecked = viewModel.isEnabled(key)
        row.rowSwitch.setOnCheckedChangeListener { button, isChecked ->
            handleQuickInfoToggle(button, key, isChecked)
        }
    }

    private fun handleQuickInfoToggle(button: CompoundButton, key: String, isChecked: Boolean) {
        val accepted = viewModel.toggleQuickInfo(key, isChecked)
        if (!accepted) {
            button.setOnCheckedChangeListener(null)
            button.isChecked = false
            Toast.makeText(this, "2개까지 선택할 수 있어요", Toast.LENGTH_SHORT).show()
            button.setOnCheckedChangeListener { b, checked -> handleQuickInfoToggle(b, key, checked) }
        }
    }

    private fun showWithdrawConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("회원 탈퇴")
            .setMessage("정말 탈퇴하시겠습니까?")
            .setPositiveButton("탈퇴") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "탈퇴가 접수되었습니다", Toast.LENGTH_SHORT).show()
                AuthManager.clearTokens()
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
