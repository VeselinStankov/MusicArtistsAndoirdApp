package com.example.navigationtask.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent
import com.example.navigationtask.data.ArtistRepository
import com.example.navigationtask.data.network.Artist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

const val PAGE_SIZE = 20

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val artistRepository: ArtistRepository
) : ViewModel() {

    private var page = 1
    private var lastSearchedValue = ""
    private var searchingJob: Job? = null

    private val _shouldNavigateBack = MutableLiveEvent<Boolean>()
    val shouldNavigateBack: LiveEvent<Boolean>
        get() = _shouldNavigateBack

    private val _shouldNavigateToArtistDetailsScreen = MutableLiveEvent<Artist>()
    val shouldNavigateToArtistDetailsScreen: LiveEvent<Artist>
        get() = _shouldNavigateToArtistDetailsScreen

    private var _hasNextPage = true
    val hasNextPage: Boolean
        get() = _hasNextPage

    private val _isLoadingArtists = MutableLiveEvent<Boolean>()
    val isLoadingArtists: LiveEvent<Boolean>
        get() = _isLoadingArtists

    private val _shouldShowFirstPageError = MutableLiveData<Boolean>()
    val shouldShowFirstPageError: LiveData<Boolean>
        get() = _shouldShowFirstPageError

    private val _shouldShowLoadingMoreError = MutableLiveData<Boolean>()
    val shouldShowLoadingMoreError: LiveData<Boolean>
        get() = _shouldShowLoadingMoreError

    private val _shouldShowNoResultsFound = MutableLiveData<Boolean>()
    val shouldShowNoResultsFound: LiveData<Boolean>
        get() = _shouldShowNoResultsFound

    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>>
        get() = _artists

    init {
        loadFirstPage()
    }

    fun onBackButtonClicked() {
        _shouldNavigateBack.fire(true)
    }

    fun onRetryClickedLoadFirstPage() {
        loadFirstPage()
    }

    fun onRetryClickedLoadMore() {
        onBottomReached()
    }

    fun onArtistClicked(artist: Artist) {
        _shouldNavigateToArtistDetailsScreen.fire(artist)
    }

    private fun loadFirstPage() {
        page = 1
        viewModelScope.launch {
            _isLoadingArtists.fire(true)
            _shouldShowFirstPageError.value = false
            val artistsResult = artistRepository.getArtists(page)
            artistsResult.fold(
                { _shouldShowFirstPageError.value = true },
                { artists ->
                    _artists.value = artists
                    _hasNextPage = artists.size == PAGE_SIZE
                }
            )
            _isLoadingArtists.fire(false)
        }
    }

    fun onBottomReached() {
        viewModelScope.launch {
            _isLoadingArtists.fire(true)
            _shouldShowLoadingMoreError.value = false
            page++
            if (lastSearchedValue.isEmpty()) {
                val artistsResult = artistRepository.getArtists(page)
                artistsResult.fold(
                    {
                        page--
                        _shouldShowLoadingMoreError.value = true
                    },
                    { artists ->
                        _artists.value = _artists.value?.plus(artists)
                        _hasNextPage = artists.size == PAGE_SIZE
                    }
                )
                _isLoadingArtists.fire(false)
            } else {
                if (searchingJob?.isActive == true) {
                    searchingJob?.cancel()
                }
                searchingJob = launch {
                    val searchResult = artistRepository.searchArtists(lastSearchedValue, page)
                    searchResult.fold(
                        {
                            page--
                            _shouldShowLoadingMoreError.value = true
                        },
                        { artists ->
                            _artists.value = _artists.value?.plus(artists)
                            _hasNextPage = artists.size == PAGE_SIZE
                            _shouldShowNoResultsFound.value = false
                        }
                    )
                    _isLoadingArtists.fire(false)
                }
            }
        }
    }

    fun onSearch(name: String) {
        lastSearchedValue = name
        page = 1
        if (searchingJob?.isActive == true) {
            searchingJob?.cancel()
        }
        searchingJob = viewModelScope.launch {
            _artists.value = mutableListOf()
            _isLoadingArtists.fire(true)
            if (lastSearchedValue.isEmpty()) {
                loadFirstPage()
            } else {
                val searchResult = artistRepository.searchArtists(lastSearchedValue, page)
                searchResult.fold(
                    { _shouldShowFirstPageError.value = true },
                    { artists ->
                        _artists.value = artists
                        _hasNextPage = artists.size == PAGE_SIZE
                        _shouldShowFirstPageError.value = false
                    }
                )
            }
            if (_artists.value!!.isEmpty()) {
                _shouldShowNoResultsFound.value = true
            }
            _isLoadingArtists.fire(false)
            _shouldShowNoResultsFound.value = false
        }
    }
}