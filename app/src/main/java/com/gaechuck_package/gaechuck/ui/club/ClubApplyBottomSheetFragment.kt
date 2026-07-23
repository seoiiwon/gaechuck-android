package com.gaechuck_package.gaechuck.ui.club

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gaechuck_package.gaechuck.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ClubApplyBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_CLUB_NAME = "club_name"
        private const val ARG_PERIOD = "period"
        private const val ARG_URL = "url"

        fun newInstance(clubName: String, period: String?, url: String): ClubApplyBottomSheetFragment {
            return ClubApplyBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CLUB_NAME, clubName)
                    putString(ARG_PERIOD, period)
                    putString(ARG_URL, url)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_club_apply_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clubName = requireArguments().getString(ARG_CLUB_NAME).orEmpty()
        val period = requireArguments().getString(ARG_PERIOD)
        val url = requireArguments().getString(ARG_URL).orEmpty()

        view.findViewById<TextView>(R.id.sheet_subtitle).text = "${clubName} 지원 구글폼으로 이동합니다"

        val periodRow = view.findViewById<View>(R.id.period_row)
        if (period.isNullOrEmpty()) {
            periodRow.visibility = View.GONE
        } else {
            view.findViewById<TextView>(R.id.period_value).text = period
        }

        view.findViewById<TextView>(R.id.link_value).text = url

        view.findViewById<View>(R.id.cancel_btn).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.move_btn).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            dismiss()
        }
    }
}
