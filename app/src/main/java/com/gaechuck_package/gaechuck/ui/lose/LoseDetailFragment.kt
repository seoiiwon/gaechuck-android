package com.gaechuck_package.gaechuck.ui.lose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gaechuck_package.gaechuck.R
import com.gaechuck_package.gaechuck.data.response.GetLoseDetailResponse
import com.gaechuck_package.gaechuck.databinding.FragmentLoseDetailBinding
import com.gaechuck_package.gaechuck.repository.LoseRepository
import com.gaechuck_package.gaechuck.ui.lose.adapter.ImagePagerAdapter
import com.gaechuck_package.gaechuck.ui.lose.viewmodel.LoseViewModel

class LoseDetailFragment : Fragment(R.layout.fragment_lose_detail) {
    private lateinit var binding: FragmentLoseDetailBinding
    private lateinit var viewModel: LoseViewModel
    private var lostItemId: Int = -1

    override fun onResume() {
        super.onResume()
        viewModel.checkLoginStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoseDetailBinding.bind(view)

        val repository = LoseRepository()
        val viewModelFactory = LoseViewModel.LoseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoseViewModel::class.java)

        lostItemId = arguments?.let {
            LoseDetailFragmentArgs.fromBundle(it).lostItemId
        } ?: -1

        if (lostItemId != -1) {
            (activity as? LoseActivity)?.setLostItemId(lostItemId)
            viewModel.loseDetailRetrofit(lostItemId)
        }

        viewModel.checkLoginStatus()
        viewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            (activity as? LoseActivity)?.updateToolbar(
                title = getString(R.string.bar_lose),
                showBackButton = true,
                showHomeButton = true,
                showEtcButton = isLoggedIn,
                showSearchButton = false,
                showWriteButton = false
            )
            binding.foundBtn.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        }

        viewModel.loseDetailData.observe(viewLifecycleOwner) { loseDetail ->
            loseDetail?.let {
                setupUI(it)
                (activity as? LoseActivity)?.setLoseItemData(
                    lostItemId = lostItemId,
                    title = it.title,
                    lostDate = it.lostDate,
                    lostLocation = it.lostLocation,
                    description = it.description,
                    images = ArrayList(it.images)
                )
            }
        }

        viewModel.LoseDetailRetrofit("분실물")

        viewModel.loseUrl.observe(viewLifecycleOwner) { url ->
            binding.chatBtn.setOnClickListener {
                if (!url.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } else {
                    Log.e("LoseDetailFragment", "URL is empty or null")
                }
            }
        }

        binding.foundBtn.setOnClickListener {
            if (lostItemId != -1) {
                viewModel.deleteData(lostItemId)
            }
        }

        viewModel.deleteData.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }

    private fun setupUI(item: GetLoseDetailResponse) {
        binding.loseName.text = item.title
        binding.loseDate.text = item.lostDate
        binding.loseLocation.text = item.lostLocation
        binding.loseDescription.text = item.description

        if (item.images.isNotEmpty()) {
            binding.loseIconPlaceholder.visibility = View.GONE
            val adapter = ImagePagerAdapter(this, item.images)
            binding.loseImagesViewpager.adapter = adapter
            if (item.images.size > 1) {
                binding.imageIndicator.visibility = View.VISIBLE
                binding.imageIndicator.attachTo(binding.loseImagesViewpager)
            }
        }
    }
}
