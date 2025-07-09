package com.gaechuck_package.gaechuck.ui.util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.gaechuck_package.gaechuck.R

class ImageDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명 처리
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_image, container, false)
        val imageView = view.findViewById<ImageView>(R.id.image_full)

        // 🔥 전달받은 이미지 URI 적용
        val imageUri = arguments?.getString("imageUri")
        if(!imageUri.isNullOrEmpty()){
            if (imageUri.startsWith("http") || imageUri.startsWith("https")) {
                // 🌐 네트워크 이미지 로드 (Glide 사용)
                Glide.with(requireContext())
                    .load(imageUri)
                    .into(imageView)
            } else {
                // 📂 로컬 이미지 로드
                imageView.setImageURI(Uri.parse(imageUri))
            }
        }

        // 이미지 클릭 시 닫기
        imageView.setOnClickListener { dismiss() }

        return view
    }

    companion object {
        fun newInstance(imageUri: String): ImageDialogFragment {
            val fragment = ImageDialogFragment()
            val args = Bundle()
            args.putString("imageUri", imageUri)
            fragment.arguments = args
            return fragment
        }
    }
}
