package com.gaechuck_package.gaechuck.ui.auth.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentSignupEmailBinding

class SignupEmailFragment : Fragment() {

    private var _binding: FragmentSignupEmailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupFieldWatcher()

        binding.btnSendCode.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!email.endsWith("@gnu.ac.kr")) {
                Toast.makeText(requireContext(), "학교 이메일(@gnu.ac.kr)을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_email_to_verify)
        }
    }

    private fun setupFieldWatcher() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val enabled = s?.isNotEmpty() == true
                binding.btnSendCode.setBackgroundResource(
                    if (enabled) R.drawable.bg_renewal_btn_primary else R.drawable.bg_renewal_btn_disabled
                )
                binding.btnSendCode.isEnabled = enabled
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
