package com.gaechuck_package.gaechuck.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentLoginCompleteBinding

class LoginCompleteFragment : Fragment(R.layout.fragment_login_complete){

    private lateinit var binding: FragmentLoginCompleteBinding
    private lateinit var mainButton : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainButton = view.findViewById(R.id.main_button)

        (activity as? LoginActivity)?.updateToolbar(
            showBackButton = false,
            showHomeButton = false,
        )

        // 버튼 온클릭 > MainActivity로 이동
        mainButton.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()  // 현재 Activity 종료
        }

    }
}