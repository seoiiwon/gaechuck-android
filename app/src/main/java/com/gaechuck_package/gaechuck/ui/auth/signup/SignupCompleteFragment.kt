package com.gaechuck_package.gaechuck.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gaechuck_package.gaechuck.ui.auth.AuthActivity
import com.gaechuck_package.gaechuck.databinding.FragmentSignupCompleteBinding

class SignupCompleteFragment : Fragment() {

    private var _binding: FragmentSignupCompleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 2초 후 자동으로 로그인 화면으로 이동
        view.postDelayed({
            if (isAdded) {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
        }, 2000L)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
