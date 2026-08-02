package com.gaechuck_package.gaechuck.ui.auth.signup

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentSignupInfoBinding

class SignupInfoFragment : Fragment() {

    private var _binding: FragmentSignupInfoBinding? = null
    private val binding get() = _binding!!

    private val idPattern = Regex("^[A-Za-z0-9]{1,6}$")
    private val passwordPattern = Regex("^[A-Za-z0-9!@#\$%^&*()_+\\-=\\[\\]{}:;\"'<>,.?/\\\\|]{1,6}$")
    private val takenIds = setOf("admin", "test", "kinako2")

    private var isIdDuplicateChecked = false
    private var isPasswordVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as SignupActivity).updateStep(2)

        binding.etId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                isIdDuplicateChecked = false
                validateId()
                updateButton()
            }
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
                validatePasswordConfirm()
                updateButton()
            }
        })

        binding.etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePasswordConfirm()
                updateButton()
            }
        })

        binding.btnCheckId.setOnClickListener { checkIdDuplicate() }

        binding.togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            binding.etPassword.inputType = if (isPasswordVisible) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
            binding.togglePasswordVisibility.setImageResource(
                if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
            )
        }

        binding.btnNext.setOnClickListener {
            if (binding.btnNext.isEnabled) {
                findNavController().navigate(R.id.action_info_to_email)
            }
        }
    }

    private fun checkIdDuplicate() {
        val id = binding.etId.text.toString().trim()
        if (!idPattern.matches(id)) return

        isIdDuplicateChecked = !takenIds.contains(id.lowercase())
        if (isIdDuplicateChecked) {
            binding.etId.setBackgroundResource(R.drawable.bg_signup_input)
            binding.btnCheckId.text = "확인 완료"
            binding.btnCheckId.setTextColor(resources.getColor(R.color.grey, null))
            binding.tvIdStatus.text = "사용가능한 아이디입니다"
            binding.tvIdStatus.setTextColor(resources.getColor(R.color.renewal_primary, null))
        } else {
            binding.etId.setBackgroundResource(R.drawable.bg_signup_input_error)
            binding.btnCheckId.text = "사용 불가"
            binding.btnCheckId.setTextColor(resources.getColor(R.color.red_alert, null))
            binding.tvIdStatus.text = "중복된 아이디입니다"
            binding.tvIdStatus.setTextColor(resources.getColor(R.color.red_alert, null))
        }
        binding.tvIdStatus.visibility = View.VISIBLE
        updateButton()
    }

    private fun validateId() {
        val id = binding.etId.text.toString().trim()
        if (id.isEmpty() || idPattern.matches(id)) {
            binding.etId.setBackgroundResource(R.drawable.bg_signup_input)
            binding.tvIdStatus.visibility = View.GONE
            binding.btnCheckId.text = "중복 확인"
            binding.btnCheckId.setTextColor(resources.getColor(R.color.grey, null))
        } else {
            binding.etId.setBackgroundResource(R.drawable.bg_signup_input_error)
            binding.tvIdStatus.text = "영문, 숫자 포함 6자 이내로 작성해 주세요"
            binding.tvIdStatus.setTextColor(resources.getColor(R.color.red_alert, null))
            binding.tvIdStatus.visibility = View.VISIBLE
        }
    }

    private fun validatePassword() {
        val password = binding.etPassword.text.toString()
        if (password.isEmpty() || passwordPattern.matches(password)) {
            binding.etPassword.setBackgroundResource(R.drawable.bg_signup_input)
            binding.tvPasswordStatus.visibility = View.GONE
        } else {
            binding.etPassword.setBackgroundResource(R.drawable.bg_signup_input_error)
            binding.tvPasswordStatus.text = "영문, 숫자, 특수문자 포함 6자 이내로 작성해 주세요"
            binding.tvPasswordStatus.visibility = View.VISIBLE
        }
    }

    private fun validatePasswordConfirm() {
        val password = binding.etPassword.text.toString()
        val confirm = binding.etPasswordConfirm.text.toString()
        if (confirm.isEmpty() || confirm == password) {
            binding.etPasswordConfirm.setBackgroundResource(R.drawable.bg_signup_input)
            binding.tvPasswordConfirmStatus.visibility = View.GONE
        } else {
            binding.etPasswordConfirm.setBackgroundResource(R.drawable.bg_signup_input_error)
            binding.tvPasswordConfirmStatus.visibility = View.VISIBLE
        }
    }

    private fun updateButton() {
        val id = binding.etId.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etPasswordConfirm.text.toString()

        val enabled = idPattern.matches(id) && isIdDuplicateChecked &&
            passwordPattern.matches(password) && confirm.isNotEmpty() && confirm == password

        binding.btnNext.setBackgroundResource(
            if (enabled) R.drawable.bg_signup_btn_primary else R.drawable.bg_signup_btn_disabled
        )
        binding.btnNext.setTextColor(
            resources.getColor(if (enabled) R.color.white else R.color.grey, null)
        )
        binding.btnNext.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
