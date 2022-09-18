package com.example.navigationtask.feed

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.navigationtask.R
import com.example.navigationtask.common.extentions.dp
import com.example.navigationtask.common.ui.AutoFitGridLayoutManager
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.data.network.Song
import com.example.navigationtask.databinding.FragmentFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initFields()
        initViews()
        binding.initListeners()
        binding.initObservers()

        return binding.root
    }

    private fun initFields() {
        binding = FragmentFeedBinding.inflate(layoutInflater)
    }

    private fun initViews() {
        (activity as AppCompatActivity).supportActionBar?.show()
        val isSharingEnabled = null != getShareIntent().resolveActivity(
            requireActivity().packageManager
        )
        val adapter = FeedAdapter(
            mutableListOf(),
            isSharingEnabled,
            object : FeedAdapter.ClickListener {
                override fun onShareClicked() {
                    viewModel.onShareClicked()
                }

                override fun onViewAllSongsClicked() {
                    viewModel.onViewAllSongsClicked()
                }

                override fun onViewAllArtistsClicked() {
                    viewModel.onViewAllArtistsClicked()
                }

                override fun onSongClicked(song: Song) {
                    viewModel.onSongClicked(song)
                }

                override fun onArtistClicked(artist: Artist) {
                    viewModel.onArtistClicked(artist)
                }
            })
        binding.rvFeed.adapter = adapter

        val layoutManager = AutoFitGridLayoutManager(requireContext(), 168.dp)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                when (adapter.getItemViewType(position)) {
                    WELCOME_CARD, HEADER -> layoutManager.spanCount
                    else -> 1
                }
        }
        binding.rvFeed.layoutManager = layoutManager
    }

    private fun FragmentFeedBinding.initListeners() {
        btnRetry.setOnClickListener {
            viewModel.loadFeedData()
        }
    }

    private fun FragmentFeedBinding.initObservers() {
        viewModel.shouldNavigateToLoginScreen.consume(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToLoginFragment())
            }
        }

        viewModel.shouldShowSharingScreen.consume(viewLifecycleOwner) { share ->
            if (share) {
                startActivity(getShareIntent())
            }
        }

        viewModel.shouldShowDeleteAccountErrorMessage.consume(viewLifecycleOwner) { shouldShowErrorToast ->
            if (shouldShowErrorToast) {
                Toast.makeText(
                    activity,
                    getString(R.string.delete_account_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.shouldShowDeleteAccountSuccessMessage.consume(viewLifecycleOwner) { shouldShowSuccessToast ->
            if (shouldShowSuccessToast) {
                Toast.makeText(
                    activity,
                    getString(R.string.delete_account_success),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.shouldNavigateToSongsScreen.consume(viewLifecycleOwner) { shouldNavigateToSongsScreen ->
            if (shouldNavigateToSongsScreen) {
                findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToSongsFragment())
            }
        }

        viewModel.shouldNavigateToArtistsScreen.consume(viewLifecycleOwner) { shouldNavigateToArtistsScreen ->
            if (shouldNavigateToArtistsScreen) {
                findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToArtistsFragment())
            }
        }

        viewModel.shouldNavigateToSongDetailsScreen.consume(viewLifecycleOwner) { song ->
            if (song != null) {
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToSongDetailsFragment(
                        song
                    )
                )
            }
        }

        viewModel.shouldNavigateToArtistDetailsScreen.consume(viewLifecycleOwner) { artist ->
            if (artist != null) {
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToArtistDetailsFragment(
                        artist
                    )
                )
            }
        }

        viewModel.feedData.observe(viewLifecycleOwner) { feedData ->
            if (feedData != null) {
                val adapter = rvFeed.adapter as FeedAdapter
                adapter.updateFeedData(feedData)
            }
        }

        viewModel.isLoading.consume(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                rvFeed.isVisible = false
                grpPlaceholder.isVisible = true
            } else {
                grpPlaceholder.isVisible = false
                rvFeed.isVisible = viewModel.failedLoading.value?.value != true
            }
        }

        viewModel.failedLoading.consume(viewLifecycleOwner) { failedLoading ->
            if (failedLoading) {
                grpFeedError.isVisible = true
                rvFeed.isVisible = false
                grpPlaceholder.isVisible = false
            } else {
                grpFeedError.isVisible = false
                if (viewModel.isLoading.value?.value == true) {
                    grpPlaceholder.isVisible = true
                } else {
                    rvFeed.isVisible = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_logout -> viewModel.onLogoutClicked()
            R.id.btn_delete_account -> viewModel.onDeleteAccountClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getShareIntent(): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, getString(R.string.description))
        return shareIntent
    }
}