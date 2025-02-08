package com.example.gaechuck.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gaechuck.R
import com.example.gaechuck.api.AuthManager
import com.example.gaechuck.databinding.FragmentLoginBinding
import com.example.gaechuck.ui.login.viewmodel.LoginViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding : FragmentLoginBinding
    private val loginViewModel : LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar 업데이트
        (activity as? LoginActivity)?.updateToolbar(
            showBackButton = true,
            showHomeButton = true,
        )

        val token = AuthManager.getToken()
        Log.d("AuthManager", "Access Token: $token")

        // ID & PW 변화 감지
        binding.loginId.addTextChangedListener(textWatcher)
        binding.loginPw.addTextChangedListener(textWatcher)

        // 버튼 온클릭 : LoginCompleteFragment로 이동 (navigation 사용)
        binding.loginButton.setOnClickListener{
            val id = binding.loginId.text.toString()
            val pw = binding.loginPw.text.toString()

            binding.loginButton.isEnabled = false

            loginViewModel.login(id, pw) { success ->
                if(success){
                    Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
                    Log.d("login", "로그인성공")
                } else {
                    Toast.makeText(requireContext(), "로그인 실패ㅜ", Toast.LENGTH_SHORT).show()
                    Log.d("login", "로그인 실패")
                }

            }
        }

        // 로그인 상태 변화 감지
        loginViewModel.loginResult.observe(viewLifecycleOwner) { isSuccess ->

            val navController = findNavController()
            val currentDestination = navController.currentDestination?.id

            if(isSuccess && currentDestination == R.id.loginFragment) {
                findNavController().navigate(R.id.action_loginFragment_to_loginCompleteFragment)
            } else if (!isSuccess && currentDestination == R.id.loginFragment){
                findNavController().navigate(R.id.action_loginFragment_to_loginFailFragment)
            }
            binding.loginButton.isEnabled = true

        }

        // 폼 유효성 상태 감지
        loginViewModel.isFormValid.observe(viewLifecycleOwner) { isValid ->
            if(isValid){
                binding.loginButton.setBackgroundResource(R.drawable.style_btn)
            } else {
                binding.loginButton.setBackgroundResource(R.drawable.style_btn_none) }
        }
    }

    // ID, PW 입력값 확인
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            val id = binding.loginId.text.toString()
            val pw = binding.loginPw.text.toString()

            loginViewModel.onCredentialsChanged(id, pw)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    }

}