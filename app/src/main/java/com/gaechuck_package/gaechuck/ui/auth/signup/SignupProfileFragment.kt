package com.gaechuck_package.gaechuck.ui.auth.signup

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaechuck_package.gaechuck.MainActivity
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.FragmentSignupProfileBinding

class SignupProfileFragment : Fragment() {

    private var _binding: FragmentSignupProfileBinding? = null
    private val binding get() = _binding!!

    private val gradeOptions = listOf("1학년", "2학년", "3학년", "4학년")
    private val departmentOptions = listOf("컴퓨터공학부", "기계공학부", "전기공학과", "경영학과", "간호학과")

    private var selectedGrade: String? = null
    private var selectedDepartment: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as SignupActivity).updateStep(1)

        setupExpandableField(
            headerRow = binding.gradeHeader,
            chevron = binding.gradeChevron,
            optionsContainer = binding.gradeOptionsContainer,
            options = gradeOptions
        ) { selected ->
            selectedGrade = selected
            binding.gradeValueText.text = selected
            updateButton()
        }

        setupExpandableField(
            headerRow = binding.departmentHeader,
            chevron = binding.departmentChevron,
            optionsContainer = binding.departmentOptionsContainer,
            options = departmentOptions
        ) { selected ->
            selectedDepartment = selected
            binding.departmentValueText.text = selected
            updateButton()
        }

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = updateButton()
        })

        binding.btnNext.setOnClickListener {
            if (binding.btnNext.isEnabled) {
                findNavController().navigate(R.id.action_profile_to_info)
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

    private fun setupExpandableField(
        headerRow: ViewGroup,
        chevron: View,
        optionsContainer: ViewGroup,
        options: List<String>,
        onSelect: (String) -> Unit
    ) {
        if (optionsContainer.childCount == 0) {
            options.forEach { option ->
                val row = layoutInflater.inflate(R.layout.item_signup_dropdown_option, optionsContainer, false) as TextView
                row.text = option
                row.setOnClickListener {
                    onSelect(option)
                    optionsContainer.isVisible = false
                    chevron.rotation = -90f
                }
                optionsContainer.addView(row)
            }
        }

        headerRow.setOnClickListener {
            val expand = !optionsContainer.isVisible
            optionsContainer.isVisible = expand
            chevron.rotation = if (expand) 90f else -90f
        }
    }

    private fun updateButton() {
        val enabled = binding.etName.text.isNotBlank() && selectedGrade != null && selectedDepartment != null
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
