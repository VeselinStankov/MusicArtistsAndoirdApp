package com.example.navigationtask.songs

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
import com.example.navigationtask.data.network.Song
import com.example.navigationtask.databinding.FragmentSongsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongsFragment : Fragment() {

    private lateinit var binding: FragmentSongsBinding
    private val viewModel: SongsViewModel by viewModels()

    private lateinit var loadingSongPageAdapter: SkeletonLoadingAdapter
    private lateinit var loadingSongsAdapter: SkeletonLoadingAdapter
    private lateinit var songsAdapter: SongsAdapter
    lateinit var loadingErrorAdapter: LoadingErrorAdapter

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
        binding = FragmentSongsBinding.inflate(layoutInflater)
    }

    private fun initViews() {
        (activity as AppCompatActivity).supportActionBar?.hide()
        loadingSongPageAdapter = SkeletonLoadingAdapter()
        songsAdapter = SongsAdapter(object : SongsAdapter.ClickListener {
            override fun onSongCLicked(song: Song) {
                viewModel.onSongClicked(song)
            }
        })
        loadingSongsAdapter = SkeletonLoadingAdapter()
        loadingErrorAdapter = LoadingErrorAdapter(
            R.string.loading_songs_failed,
            object : LoadingErrorAdapter.ClickListener {
                override fun onRetryClicked() {
                    viewModel.onRetryClickedLoadMore()
                }
            })
        val adapter = ConcatAdapter(
            loadingSongPageAdapter,
            songsAdapter,
            loadingSongsAdapter,
            loadingErrorAdapter
        )
        binding.rvSongs.adapter = adapter
        binding.rvSongs.itemAnimator = null
    }

    private fun FragmentSongsBinding.initListeners() {
        btnBack.setOnClickListener {
            viewModel.onBackButtonClicked()
        }

        binding.rvSongs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val isNotLoadingSongs = viewModel.isLoadingSongs.value?.value != true
                if (!loadingErrorAdapter.isVisible && viewModel.hasNextPage && isNotLoadingSongs &&
                    layoutManager.findLastCompletelyVisibleItemPosition()
                    == binding.rvSongs.adapter?.itemCount?.minus(1) ?: false
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

    private fun FragmentSongsBinding.initObservers() {
        viewModel.shouldNavigateBack.consume(viewLifecycleOwner) { shouldNavigateBack ->
            if (shouldNavigateBack) {
                findNavController().popBackStack()
            }
        }

        viewModel.shouldNavigateToSongDetailsScreen.consume(viewLifecycleOwner) { song ->
            if (song != null) {
                findNavController().navigate(
                    SongsFragmentDirections.actionSongsFragmentToDetailsFragment(
                        song
                    )
                )
            }
        }

        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            if (songs != null) {
                songsAdapter.updateSongsFeedData(songs)
            }
        }

        viewModel.isLoadingSongs.consume(viewLifecycleOwner) { isLoading ->
            rvSongs.isVisible = viewModel.shouldShowFirstPageError.value ?: false == false
                    && viewModel.shouldShowNoResultsFound.value ?: false == false
            if (isLoading) {
                rvSongs.post {
                    if (viewModel.songs.value?.isNotEmpty() != true) {
                        loadingSongsAdapter.itemsCount = 0
                        loadingSongPageAdapter.itemsCount = 10
                    }
                    rvSongs.isVisible = true
                    grpSearchBar.isVisible = true
                }
                grpSongError.isVisible = false
                txtNoResultsFound.isVisible = false
            } else {
                if (!viewModel.hasNextPage) {
                    loadingSongsAdapter.itemsCount = 0
                    loadingSongPageAdapter.itemsCount = 0
                } else {
                    if (viewModel.shouldShowLoadingMoreError.value != true
                        && viewModel.shouldShowFirstPageError.value != true
                    ) {
                        loadingSongsAdapter.itemsCount = 2
                        loadingSongPageAdapter.itemsCount = 0
                    }
                }
            }
        }

        viewModel.shouldShowFirstPageError.observe(viewLifecycleOwner) { shouldShowFirstPageError ->
            if (shouldShowFirstPageError) {
                grpSearchBar.isVisible = false
                rvSongs.isVisible = false
                grpSongError.isVisible = true
            }
        }

        viewModel.shouldShowLoadingMoreError.observe(viewLifecycleOwner) { shouldShowLoadingMoreError ->
            loadingErrorAdapter.isVisible = shouldShowLoadingMoreError
            if (shouldShowLoadingMoreError) {
                loadingSongsAdapter.itemsCount = 0
                rvSongs.scrollToPosition(viewModel.songs.value!!.size)
            } else {
                if (viewModel.hasNextPage) {
                    loadingSongsAdapter.itemsCount = 2
                }
            }
        }

        viewModel.shouldShowNoResultsFound.observe(viewLifecycleOwner) { shouldShowNoResultsFound ->
            if (shouldShowNoResultsFound) {
                rvSongs.isVisible = false
                txtNoResultsFound.isVisible = true
            }
        }
    }
}