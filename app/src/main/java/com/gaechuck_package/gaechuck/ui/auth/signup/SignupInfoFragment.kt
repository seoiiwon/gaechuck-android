package com.gaechuck_package.gaechuck.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentSignupInfoBinding

class SignupInfoFragment : Fragment() {

    private var _binding: FragmentSignupInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupFieldWatchers()

        binding.btnNext.setOnClickListener {
            val id = binding.etId.text.toString().trim()
            val password = binding.etPassword.text.toString()

            when {
                id.isEmpty() -> Toast.makeText(requireContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
                password.isEmpty() -> Toast.makeText(requireContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                else -> findNavController().navigate(R.id.action_info_to_email)
            }
        }

        binding.tvGoLogin.setOnClickListener { requireActivity().finish() }
        binding.tvGuest.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun setupFieldWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = updateButton()
        }
        binding.etId.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)
    }

    private fun updateButton() {
        val enabled = binding.etId.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()
        binding.btnNext.setBackgroundResource(
            if (enabled) R.drawable.bg_renewal_btn_primary else R.drawable.bg_renewal_btn_disabled
        )
        binding.btnNext.isEnabled = enabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
