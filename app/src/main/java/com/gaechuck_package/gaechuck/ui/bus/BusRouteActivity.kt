package com.gaechuck_package.gaechuck.ui.bus

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.databinding.ActivityBusRouteBinding
import com.gaechuck_package.gaechuck.ui.bus.adapter.BusTimeAdapter
import com.gaechuck_package.gaechuck.ui.bus.viewmodel.BusRouteViewModel

class BusRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusRouteBinding
    private val viewModel: BusRouteViewModel by viewModels()
    private lateinit var adapter: BusTimeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = BusTimeAdapter(emptyList())
        binding.timeRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@BusRouteActivity)
            adapter = this@BusRouteActivity.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.swapButton.setOnClickListener { viewModel.swap() }

        binding.tabAm.setOnClickListener { viewModel.setAm() }

        binding.tabPm.setOnClickListener { viewModel.setPm() }
    }

    private fun observeViewModel() {
        viewModel.route.observe(this) { route ->
            binding.fromCampusText.text = route.fromCampus
            binding.toCampusText.text = route.toCampus
            binding.boardingLocationText.text = route.boardingLocation
            binding.operationNote.text = route.operationNote
            updateSchedule()
        }

        viewModel.isAm.observe(this) { isAm ->
            val activeColor = getColor(R.color.renewal_primary)
            val inactiveColor = getColor(R.color.info_grey)
            binding.tabAm.setTextColor(if (isAm) activeColor else inactiveColor)
            binding.tabPm.setTextColor(if (isAm) inactiveColor else activeColor)
            updateSchedule()
        }
    }

    private fun updateSchedule() {
        adapter.updateItems(viewModel.getCurrentSchedule())
    }
}
