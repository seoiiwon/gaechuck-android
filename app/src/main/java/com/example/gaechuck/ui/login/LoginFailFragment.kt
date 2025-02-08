package com.example.gaechuck.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gaechuck.MainActivity
import com.example.gaechuck.R
import com.example.gaechuck.databinding.FragmentLoginFailBinding

class LoginFailFragment : Fragment(R.layout.fragment_login_fail) {

    private lateinit var binding: FragmentLoginFailBinding
    private lateinit var mainButton : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginFailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainButton = view.findViewById(R.id.main_button)

        (activity as? LoginActivity)?.updateToolbar(
            showHomeButton = false,
            showBackButton = true,
        )

        // 뒤로 가기 버튼 동작 추가
        (activity as? LoginActivity)?.findViewById<ImageView>(R.id.button_back)?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFailFragment_to_loginFragment)
        }

        // 버튼 온 클릭 > MainActivity로 이동
        mainButton.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()  // 현재 Activity 종료
        }
    }
}