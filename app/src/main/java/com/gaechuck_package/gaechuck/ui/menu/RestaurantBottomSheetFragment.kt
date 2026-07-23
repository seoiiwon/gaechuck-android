package com.gaechuck_package.gaechuck.ui.menu

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.ui.menu.adaptor.RestaurantAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.parcelize.Parcelize

class RestaurantBottomSheetFragment : BottomSheetDialogFragment() {

    @Parcelize
    data class RestaurantItem(
        val name: String,
        val campus: String,
        val seq: Int
    ) : Parcelable

    private var onSelected: ((seq: Int, name: String) -> Unit)? = null

    companion object {
        private const val ARG_ITEMS = "items"
        private const val ARG_SELECTED_SEQ = "selected_seq"

        fun newInstance(
            restaurants: List<RestaurantItem>,
            selectedSeq: Int,
            onSelected: (seq: Int, name: String) -> Unit
        ): RestaurantBottomSheetFragment {
            return RestaurantBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_ITEMS, ArrayList(restaurants))
                    putInt(ARG_SELECTED_SEQ, selectedSeq)
                }
                this.onSelected = onSelected
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_restaurant_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        val items: List<RestaurantItem> = requireArguments().getParcelableArrayList<RestaurantItem>(ARG_ITEMS) ?: emptyList()
        val selectedSeq = requireArguments().getInt(ARG_SELECTED_SEQ)

        view.findViewById<TextView>(R.id.closeButton).setOnClickListener { dismiss() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.restaurantRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RestaurantAdapter(items, selectedSeq) { item ->
            onSelected?.invoke(item.seq, item.name)
            dismiss()
        }
    }
}
