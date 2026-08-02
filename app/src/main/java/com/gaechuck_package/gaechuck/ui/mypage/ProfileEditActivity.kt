package com.gaechuck_package.gaechuck.ui.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.model.UserProfile
import com.gaechuck_package.gaechuck.databinding.ActivityProfileEditBinding
import com.gaechuck_package.gaechuck.repository.MyPageRepository
import com.gaechuck_package.gaechuck.ui.mypage.viewmodel.MyPageViewModel
import com.gaechuck_package.gaechuck.ui.mypage.viewmodel.NicknameValidation
import com.gaechuck_package.gaechuck.ui.util.BottomNavHelper
import com.gaechuck_package.gaechuck.ui.util.BottomNavTab

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var viewModel: MyPageViewModel
    private var isEditingNickname = false

    private val departmentOptions = listOf("컴퓨터공학부", "기계공학부", "전기공학과", "경영학과", "간호학과")
    private val gradeOptions = listOf("1학년", "2학년", "3학년", "4학년")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = MyPageViewModel.MyPageViewModelFactory(MyPageRepository.getInstance())
        viewModel = ViewModelProvider(this, factory)[MyPageViewModel::class.java]

        BottomNavHelper.bind(binding.bottomNavInclude, this, BottomNavTab.MY)

        binding.buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setupNicknameField()
        setupDepartmentField()
        setupGradeField()

        binding.saveButton.setOnClickListener { commitNickname() }

        viewModel.profile.observe(this) { profile -> renderProfile(profile) }
        viewModel.loadProfile()
    }

    private fun renderProfile(profile: UserProfile) {
        binding.nicknameDisplayText.text = profile.nickname
        binding.deptGradeText.text = "${profile.department} · ${profile.grade}"
        binding.departmentValue.text = profile.department
        binding.gradeValue.text = profile.grade
        binding.studentIdValue.text = profile.studentId
        if (!isEditingNickname) {
            binding.nicknameEditText.setText(profile.nickname)
        }
    }

    private fun setupNicknameField() {
        binding.nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isEditingNickname) return
                val value = s?.toString().orEmpty()
                val isValid = value.isNotEmpty() &&
                    viewModel.validateNickname(value) is NicknameValidation.Valid
                binding.nicknameHint.setTextColor(
                    ContextCompat.getColor(
                        this@ProfileEditActivity,
                        if (isValid || value.isEmpty()) R.color.info_grey else R.color.red_alert
                    )
                )
            }
        })

        binding.nicknameEditText.setOnClickListener {
            if (!isEditingNickname) enterNicknameEditMode()
        }
    }

    private fun enterNicknameEditMode() {
        isEditingNickname = true
        binding.nicknameEditText.isFocusable = true
        binding.nicknameEditText.isFocusableInTouchMode = true
        binding.nicknameEditText.requestFocus()
        binding.nicknameEditText.setBackgroundResource(R.drawable.bg_profile_edit_field_active)
        binding.nicknameEditText.setTextColor(ContextCompat.getColor(this, R.color.black_1))
        binding.nicknameEditText.setSelection(binding.nicknameEditText.text?.length ?: 0)
        binding.nicknameHint.visibility = android.view.View.VISIBLE
        binding.nicknameHint.setTextColor(ContextCompat.getColor(this, R.color.info_grey))
        binding.saveButton.visibility = android.view.View.VISIBLE

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.nicknameEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun exitNicknameEditMode() {
        isEditingNickname = false
        binding.nicknameEditText.isFocusable = false
        binding.nicknameEditText.isFocusableInTouchMode = false
        binding.nicknameEditText.setBackgroundResource(R.drawable.bg_profile_edit_field)
        binding.nicknameEditText.setTextColor(ContextCompat.getColor(this, R.color.grey))
        binding.nicknameHint.visibility = android.view.View.GONE
        binding.saveButton.visibility = android.view.View.GONE

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.nicknameEditText.windowToken, 0)
    }

    private fun commitNickname() {
        val value = binding.nicknameEditText.text?.toString().orEmpty()
        if (viewModel.saveNickname(value)) {
            exitNicknameEditMode()
        } else {
            Toast.makeText(this, "한글, 영어 포함 6자 이내로 작성해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDepartmentField() {
        binding.departmentRow.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("학과 선택")
                .setItems(departmentOptions.toTypedArray()) { _, index ->
                    viewModel.saveDepartment(departmentOptions[index])
                }
                .show()
        }
    }

    private fun setupGradeField() {
        binding.gradeRow.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("학년 선택")
                .setItems(gradeOptions.toTypedArray()) { _, index ->
                    viewModel.saveGrade(gradeOptions[index])
                }
                .show()
        }
    }
}
