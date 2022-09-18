package com.example.navigationtask.artists

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationtask.R
import com.example.navigationtask.common.extentions.textChanges
import com.example.navigationtask.common.ui.LoadingErrorAdapter
import com.example.navigationtask.common.ui.SkeletonLoadingAdapter
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.databinding.FragmentArtistsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistsFragment : Fragment() {

    private lateinit var binding: FragmentArtistsBinding
    private val viewModel: ArtistsViewModel by viewModels()

    private lateinit var loadingArtistPageAdapter: SkeletonLoadingAdapter
    private lateinit var loadingArtistAdapter: SkeletonLoadingAdapter
    private lateinit var artistsAdapter: ArtistsAdapter
    private lateinit var loadingErrorAdapter: LoadingErrorAdapter

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
        binding = FragmentArtistsBinding.inflate(layoutInflater)
    }

    private fun initViews() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        loadingArtistPageAdapter = SkeletonLoadingAdapter()
        artistsAdapter = ArtistsAdapter(object : ArtistsAdapter.ClickListener {
            override fun onArtistClicked(artist: Artist) {
                viewModel.onArtistClicked(artist)
            }
        })
        loadingArtistAdapter = SkeletonLoadingAdapter()
        loadingErrorAdapter = LoadingErrorAdapter(
            R.string.loading_artists_failed,
            object : LoadingErrorAdapter.ClickListener {
                override fun onRetryClicked() {
                    viewModel.onRetryClickedLoadMore()
                }
            })
        val adapter = ConcatAdapter(
            loadingArtistPageAdapter,
            artistsAdapter,
            loadingArtistAdapter,
            loadingErrorAdapter
        )
        binding.rvArtists.adapter = adapter
        binding.rvArtists.itemAnimator = null
    }

    private fun FragmentArtistsBinding.initListeners() {
        btnBack.setOnClickListener {
            viewModel.onBackButtonClicked()
        }

        binding.rvArtists.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val isNotLoadingArtists = viewModel.isLoadingArtists.value?.value != true
                if (!loadingErrorAdapter.isVisible && viewModel.hasNextPage && isNotLoadingArtists &&
                    layoutManager.findLastCompletelyVisibleItemPosition()
                    == binding.rvArtists.adapter?.itemCount?.minus(1) ?: false
                ) {
                    viewModel.onBottomReached()
                }
            }
        })

        btnRetry.setOnClickListener {
            viewModel.onRetryClickedLoadFirstPage()
        }

        CoroutineScope(Dispatchers.Main).launch {
            editSearch.textChanges().debounce(500L).collect { text ->
                viewModel.onSearch(text)
            }
        }
    }

    private fun FragmentArtistsBinding.initObservers() {
        viewModel.shouldNavigateBack.consume(viewLifecycleOwner) { shouldNavigateBack ->
            if (shouldNavigateBack) {
                findNavController().popBackStack()
            }
        }

        viewModel.shouldNavigateToArtistDetailsScreen.consume(viewLifecycleOwner) { artist ->
            if (artist != null) {
                findNavController().navigate(
                    ArtistsFragmentDirections.actionArtistsFragmentToArtistDetailsFragment(
                        artist
                    )
                )
            }
        }

        viewModel.artists.observe(viewLifecycleOwner) { artists ->
            if (artists != null) {
                artistsAdapter.updateArtistsFeedData(artists)
            }
        }

        viewModel.isLoadingArtists.consume(viewLifecycleOwner) { isLoading ->
            rvArtists.isVisible = viewModel.shouldShowFirstPageError.value ?: false == false
                    && viewModel.shouldShowNoResultsFound.value ?: false == false
            if (isLoading) {
                rvArtists.post {
                    if (viewModel.artists.value?.isNotEmpty() != true) {
                        loadingArtistAdapter.itemsCount = 0
                        loadingArtistPageAdapter.itemsCount = 10
                    }
                    rvArtists.isVisible = true
                    grpSearchBar.isVisible = true
                }
                grpArtistError.isVisible = false
                txtNoResultsFound.isVisible = false
            } else {
                if (!viewModel.hasNextPage) {
                    loadingArtistAdapter.itemsCount = 0
                    loadingArtistPageAdapter.itemsCount = 0
                } else {
                    if (viewModel.shouldShowLoadingMoreError.value != true
                        && viewModel.shouldShowFirstPageError.value != true
                    ) {
                        loadingArtistAdapter.itemsCount = 2
                        loadingArtistPageAdapter.itemsCount = 0
                    }
                }
            }
        }

        viewModel.shouldShowFirstPageError.observe(viewLifecycleOwner) { shouldShowFirstPageError ->
            if (shouldShowFirstPageError) {
                grpSearchBar.isVisible = false
                rvArtists.isVisible = false
                grpArtistError.isVisible = true
            }
        }

        viewModel.shouldShowLoadingMoreError.observe(viewLifecycleOwner) { shouldShowLoadingMoreError ->
            loadingErrorAdapter.isVisible = shouldShowLoadingMoreError
            if (shouldShowLoadingMoreError) {
                loadingArtistAdapter.itemsCount = 0
                rvArtists.scrollToPosition(viewModel.artists.value!!.size)
            } else {
                if (viewModel.hasNextPage) {
                    loadingArtistAdapter.itemsCount = 2
                }
            }
        }

        viewModel.shouldShowNoResultsFound.observe(viewLifecycleOwner) { shouldShowNoResultsFound ->
            if (shouldShowNoResultsFound) {
                rvArtists.isVisible = false
                txtNoResultsFound.isVisible = true
            }
        }
    }
}