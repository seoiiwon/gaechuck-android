package com.gaechuck_package.gaechuck.ui.auth.signup

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentSignupEmailBinding

class SignupEmailFragment : Fragment() {

    private var _binding: FragmentSignupEmailBinding? = null
    private val binding get() = _binding!!

    private var countDownTimer: CountDownTimer? = null
    private var sentCode: String? = null
    private var isVerified = false
    private var isCodeVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as SignupActivity).updateStep(3)

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.etEmail.setBackgroundResource(R.drawable.bg_signup_input)
                binding.tvEmailError.visibility = View.GONE
            }
        })

        binding.etCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                isVerified = false
                binding.etCode.setBackgroundResource(R.drawable.bg_signup_input)
                binding.tvVerifyStatus.visibility = View.GONE
                updateButton()
            }
        })

        binding.btnSendCode.setOnClickListener { sendCode() }
        binding.tvResend.setOnClickListener { sendCode() }
        binding.btnVerify.setOnClickListener { verifyCode() }

        binding.toggleCodeVisibility.setOnClickListener {
            isCodeVisible = !isCodeVisible
            binding.etCode.inputType = if (isCodeVisible) {
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            } else {
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
            binding.etCode.setSelection(binding.etCode.text.length)
            binding.toggleCodeVisibility.setImageResource(
                if (isCodeVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
            )
        }

        binding.btnNext.setOnClickListener {
            if (binding.btnNext.isEnabled) {
                findNavController().navigate(R.id.action_email_to_complete)
            }
        }
    }

    private fun sendCode() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty() || !email.endsWith("@gnu.ac.kr")) {
            binding.etEmail.setBackgroundResource(R.drawable.bg_signup_input_error)
            binding.tvEmailError.text = "잘못된 이메일입니다"
            binding.tvEmailError.visibility = View.VISIBLE
            return
        }

        binding.etEmail.setBackgroundResource(R.drawable.bg_signup_input)
        binding.tvEmailError.visibility = View.GONE
        binding.resendRow.visibility = View.VISIBLE
        binding.tvCodeLabel.visibility = View.VISIBLE
        binding.codeRow.visibility = View.VISIBLE

        sentCode = (10000..99999).random().toString()
        isVerified = false
        binding.etCode.text?.clear()
        binding.tvVerifyStatus.visibility = View.GONE
        startTimer(5 * 60 * 1000L)
        updateButton()
    }

    private fun verifyCode() {
        val code = binding.etCode.text.toString()
        if (code.length < 5) return

        if (code == sentCode) {
            isVerified = true
            binding.etCode.setBackgroundResource(R.drawable.bg_signup_input)
            binding.tvVerifyStatus.text = "인증이 완료되었습니다"
            binding.tvVerifyStatus.setTextColor(resources.getColor(R.color.brand_2, null))
        } else {
            isVerified = false
            binding.etCode.setBackgroundResource(R.drawable.bg_signup_input_error)
            binding.tvVerifyStatus.text = "인증번호가 일치하지 않습니다"
            binding.tvVerifyStatus.setTextColor(resources.getColor(R.color.red_alert, null))
        }
        binding.tvVerifyStatus.visibility = View.VISIBLE
        updateButton()
    }

    private fun startTimer(millis: Long) {
        countDownTimer?.cancel()
        binding.tvTimer.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(millis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvTimer.text = String.format("%d:%02d 후에 인증번호가 만료됩니다", minutes, seconds)
            }
            override fun onFinish() {
                binding.tvTimer.text = "인증번호가 만료되었습니다. 재전송해주세요."
                isVerified = false
                updateButton()
            }
        }.start()
    }

    private fun updateButton() {
        binding.btnNext.setBackgroundResource(
            if (isVerified) R.drawable.bg_signup_btn_primary else R.drawable.bg_signup_btn_disabled
        )
        binding.btnNext.setTextColor(
            resources.getColor(if (isVerified) R.color.white else R.color.grey, null)
        )
        binding.btnNext.isEnabled = isVerified
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
