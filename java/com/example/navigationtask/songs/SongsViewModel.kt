package com.example.navigationtask.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent
import com.example.navigationtask.data.SongRepository
import com.example.navigationtask.data.network.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

const val PAGE_SIZE = 20

@HiltViewModel
class SongsViewModel @Inject constructor(private val songRepository: SongRepository) : ViewModel() {

    private var page = 1
    private var lastSearchedValue = ""
    private var searchingJob: Job? = null

    private var _hasNextPage = true
    val hasNextPage: Boolean
        get() = _hasNextPage

    private val _shouldNavigateBack = MutableLiveEvent<Boolean>()
    val shouldNavigateBack: LiveEvent<Boolean>
        get() = _shouldNavigateBack

    private val _shouldNavigateToSongDetailsScreen = MutableLiveEvent<Song>()
    val shouldNavigateToSongDetailsScreen: LiveEvent<Song>
        get() = _shouldNavigateToSongDetailsScreen

    private val _isLoadingSongs = MutableLiveEvent<Boolean>()
    val isLoadingSongs: LiveEvent<Boolean>
        get() = _isLoadingSongs

    private val _shouldShowFirstPageError = MutableLiveData<Boolean>()
    val shouldShowFirstPageError: LiveData<Boolean>
        get() = _shouldShowFirstPageError

    private val _shouldShowLoadingMoreError = MutableLiveData<Boolean>()
    val shouldShowLoadingMoreError: LiveData<Boolean>
        get() = _shouldShowLoadingMoreError

    private val _shouldShowNoResultsFound = MutableLiveData<Boolean>()
    val shouldShowNoResultsFound: LiveData<Boolean>
        get() = _shouldShowNoResultsFound

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>>
        get() = _songs

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

    fun onSongClicked(song: Song) {
        _shouldNavigateToSongDetailsScreen.fire(song)
    }

    private fun loadFirstPage() {
        page = 1
        viewModelScope.launch {
            _isLoadingSongs.fire(true)
            _shouldShowFirstPageError.value = false
            val songsResult = songRepository.getSongs(page)
            songsResult.fold(
                { _shouldShowFirstPageError.value = true },
                { songs ->
                    _songs.value = songs
                    _hasNextPage = songs.size == PAGE_SIZE
                }
            )
            _isLoadingSongs.fire(false)
        }
    }

    fun onBottomReached() {
        viewModelScope.launch {
            _isLoadingSongs.fire(true)
            _shouldShowLoadingMoreError.value = false
            page++
            if (lastSearchedValue.isEmpty()) {
                val songsResult = songRepository.getSongs(page)
                songsResult.fold(
                    {
                        page--
                        _shouldShowLoadingMoreError.value = true
                    },
                    { songs ->
                        _songs.value = _songs.value?.plus(songs)
                        _hasNextPage = songs.size == PAGE_SIZE
                    }
                )
                _isLoadingSongs.fire(false)
            } else {
                if (searchingJob?.isActive == true) {
                    searchingJob?.cancel()
                }
                searchingJob = launch {
                    val searchResult = songRepository.searchSongs(lastSearchedValue, page)
                    searchResult.fold(
                        {
                            page--
                            _shouldShowLoadingMoreError.value = true
                        },
                        { songs ->
                            _songs.value = _songs.value?.plus(songs)
                            _hasNextPage = songs.size == PAGE_SIZE
                            _shouldShowNoResultsFound.value = false
                        }
                    )
                    _isLoadingSongs.fire(false)
                }
            }
        }
    }

    fun onSearch(title: String) {
        lastSearchedValue = title
        page = 1
        if (searchingJob?.isActive == true) {
            searchingJob?.cancel()
        }
        searchingJob = viewModelScope.launch {
            _songs.value = mutableListOf()
            _isLoadingSongs.fire(true)
            if (lastSearchedValue.isEmpty()) {
                loadFirstPage()
            } else {
                val searchResult = songRepository.searchSongs(lastSearchedValue, page)
                searchResult.fold(
                    { _shouldShowFirstPageError.value = true },
                    { songs ->
                        _songs.value = songs
                        _hasNextPage = songs.size == PAGE_SIZE
                        _shouldShowFirstPageError.value = false
                    }
                )
            }
            if (_songs.value!!.isEmpty()) {
                _shouldShowNoResultsFound.value = true
            }
            _isLoadingSongs.fire(false)
            _shouldShowNoResultsFound.value = false
        }
    }
}