package com.gaechuck_package.gaechuck.ui.auth.signup

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentSignupVerifyBinding

class SignupVerifyFragment : Fragment() {

    private var _binding: FragmentSignupVerifyBinding? = null
    private val binding get() = _binding!!
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupOtpInputs()
        startTimer(5 * 60 * 1000L)

        binding.btnVerify.setOnClickListener {
            val code = getOtpCode()
            if (code.length < 5) {
                Toast.makeText(requireContext(), "인증번호 5자리를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            countDownTimer?.cancel()
            findNavController().navigate(R.id.action_verify_to_complete)
        }

        binding.tvResend.setOnClickListener {
            startTimer(5 * 60 * 1000L)
            Toast.makeText(requireContext(), "인증 코드를 재발송했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOtpCode(): String {
        return listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4, binding.otp5)
            .joinToString("") { it.text.toString() }
    }

    private fun setupOtpInputs() {
        val boxes = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4, binding.otp5)

        boxes.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < boxes.lastIndex) {
                        boxes[index + 1].requestFocus()
                    }
                    updateVerifyButton()
                }
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editText.text.isEmpty() && index > 0) {
                        boxes[index - 1].requestFocus()
                        boxes[index - 1].text.clear()
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    private fun updateVerifyButton() {
        val code = getOtpCode()
        val enabled = code.length == 5
        binding.btnVerify.setBackgroundResource(
            if (enabled) R.drawable.bg_renewal_btn_primary else R.drawable.bg_renewal_btn_disabled
        )
        binding.btnVerify.isEnabled = enabled
    }

    private fun startTimer(millis: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(millis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvTimer.text = String.format("%d:%02d 후에 인증번호가 만료됩니다", minutes, seconds)
            }
            override fun onFinish() {
                binding.tvTimer.text = "인증번호가 만료되었습니다. 재발송해주세요."
            }
        }.start()
    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
