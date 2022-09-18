package com.example.navigationtask.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationtask.artists.mappers.ArtistDomainToUIMapper
import com.example.navigationtask.common.*
import com.example.navigationtask.common.livedata.LiveEvent
import com.example.navigationtask.common.livedata.MutableLiveEvent
import com.example.navigationtask.data.AccountRepository
import com.example.navigationtask.data.ArtistRepository
import com.example.navigationtask.data.SongRepository
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.data.network.Song
import com.example.navigationtask.songs.mappers.SongDomainToUIMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

private const val INITIAL_PAGE = 1

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository
) : ViewModel() {

    private var _feedData = MutableLiveData<MutableList<DataItem>>()
    val feedData: LiveData<MutableList<DataItem>>
        get() = _feedData

    private val _isLoading = MutableLiveEvent<Boolean>()
    val isLoading: LiveEvent<Boolean>
        get() = _isLoading

    private val _failedLoading = MutableLiveEvent<Boolean>()
    val failedLoading: LiveEvent<Boolean>
        get() = _failedLoading

    private val _shouldNavigateToLoginScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToLoginScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToLoginScreen

    private val _shouldShowSharingScreen = MutableLiveEvent<Boolean>()
    val shouldShowSharingScreen: LiveEvent<Boolean>
        get() = _shouldShowSharingScreen

    private val _shouldShowDeleteAccountSuccessMessage = MutableLiveEvent<Boolean>()
    val shouldShowDeleteAccountSuccessMessage: LiveEvent<Boolean>
        get() = _shouldShowDeleteAccountSuccessMessage

    private val _shouldShowDeleteAccountErrorMessage = MutableLiveEvent<Boolean>()
    val shouldShowDeleteAccountErrorMessage: LiveEvent<Boolean>
        get() = _shouldShowDeleteAccountErrorMessage

    private val _shouldNavigateToSongsScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToSongsScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToSongsScreen

    private val _shouldNavigateToArtistsScreen = MutableLiveEvent<Boolean>()
    val shouldNavigateToArtistsScreen: LiveEvent<Boolean>
        get() = _shouldNavigateToArtistsScreen

    private val _shouldNavigateToSongDetailsScreen = MutableLiveEvent<Song>()
    val shouldNavigateToSongDetailsScreen: LiveEvent<Song>
        get() = _shouldNavigateToSongDetailsScreen

    private val _shouldNavigateToArtistDetailsScreen = MutableLiveEvent<Artist>()
    val shouldNavigateToArtistDetailsScreen: LiveEvent<Artist>
        get() = _shouldNavigateToArtistDetailsScreen

    init {
        loadFeedData()
    }

    fun loadFeedData() {
        var data: MutableList<DataItem>
        viewModelScope.launch {
            _isLoading.fire(true)
            _failedLoading.fire(false)
            coroutineScope {
                val currentUserResult = accountRepository.getCurrentUser()
                currentUserResult.fold(
                    { _shouldNavigateToLoginScreen.fire(true) },
                    { account ->
                        getSongsAndArtistsData(viewModelScope).fold(
                            { _failedLoading.fire(true) },
                            {
                                data = buildFeedData(
                                    songs = it.first,
                                    artists = it.second,
                                    email = account.email
                                )
                                _failedLoading.fire(false)
                                _feedData.value = data
                                _isLoading.fire(false)
                            }
                        )
                    }
                )
            }
        }
    }

    private suspend fun getSongsAndArtistsData(scope: CoroutineScope): Try2<List<Song>, List<Artist>> {
        val songFeedData = scope.async {
            songRepository.getSongs(INITIAL_PAGE)
        }

        val artistsFeedData = scope.async {
            artistRepository.getArtists(INITIAL_PAGE)
        }

        val (songsResult, artistsResult) = songFeedData.await() to artistsFeedData.await()

        return if (songsResult.isOk() && artistsResult.isOk()) {
            (songsResult.forceOk() to artistsResult.forceOk()).ok()
        } else {
            Err.Unknown.err()
        }
    }

    private fun buildFeedData(
        songs: List<Song>,
        artists: List<Artist>,
        email: String
    ): MutableList<DataItem> {
        val data = mutableListOf<DataItem>()
        val songsConverted = SongDomainToUIMapper().invoke(songs)
        val artistsConverted = ArtistDomainToUIMapper().invoke(artists)

        data.add(DataItem.WelcomeCard(email))
        if (songsConverted.isNotEmpty()) {
            data.add(DataItem.Header(HeaderType.SONGS_VIEW_ALL))
        }
        data.addAll(songsConverted.take(2))
        if (artistsConverted.isNotEmpty()) {
            data.add(DataItem.Header(HeaderType.ARTISTS_VIEW_ALL))
        }
        data.addAll(artistsConverted.take(3))

        return data
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            accountRepository.logout()
            _shouldNavigateToLoginScreen.fire(true)
        }
    }

    fun onShareClicked() {
        _shouldShowSharingScreen.fire(true)
    }

    fun onViewAllSongsClicked() {
        _shouldNavigateToSongsScreen.fire(true)
    }

    fun onViewAllArtistsClicked() {
        _shouldNavigateToArtistsScreen.fire(true)
    }

    fun onSongClicked(song: Song) {
        _shouldNavigateToSongDetailsScreen.fire(song)
    }

    fun onArtistClicked(artist: Artist) {
        _shouldNavigateToArtistDetailsScreen.fire(artist)
    }

    fun onDeleteAccountClicked() {
        viewModelScope.launch {
            val deleteAccountResult = accountRepository.deleteCurrentAccount()

            deleteAccountResult.fold(
                { _shouldShowDeleteAccountErrorMessage.fire(true) },
                { _shouldShowDeleteAccountSuccessMessage.fire(true) }
            )
            _shouldNavigateToLoginScreen.fire(true)
        }
    }
}