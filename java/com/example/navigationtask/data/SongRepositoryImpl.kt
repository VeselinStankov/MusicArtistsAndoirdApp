package com.example.navigationtask.data

import android.content.SharedPreferences
import com.example.navigationtask.songs.mappers.SongDatabaseToDomainMapper
import com.example.navigationtask.common.*
import com.example.navigationtask.songs.mappers.SongDomainToDatabaseMapper
import com.example.navigationtask.data.database.dao.SongDao
import com.example.navigationtask.data.network.MusicApiService
import com.example.navigationtask.data.network.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val PAGE_SIZE = 20
private const val SONGS_DATA_LAST_UPDATED = "songsDataLastUpdated"
private const val DEFAULT_CACHE_EXPIRATION_TIME_IN_MS = 60 * 1000
private const val SONGS_DATA_LAST_UPDATED_NOT_SET = -1L

class SongRepositoryImpl @Inject constructor(
    private val musicApiService: MusicApiService,
    private val songDao: SongDao,
    private val sharedPreferences: SharedPreferences
) : SongRepository, TryApiCall {
    override suspend fun getSongs(page: Int): Try<List<Song>> {
        return withContext(Dispatchers.IO) {
            if (page == 1) {
                val currentTimeInMs = System.currentTimeMillis()
                val songsCacheLastUpdated = sharedPreferences.getLong(
                    SONGS_DATA_LAST_UPDATED,
                    SONGS_DATA_LAST_UPDATED_NOT_SET
                )
                val isCacheExpired = songsCacheLastUpdated == SONGS_DATA_LAST_UPDATED_NOT_SET ||
                        currentTimeInMs - songsCacheLastUpdated > DEFAULT_CACHE_EXPIRATION_TIME_IN_MS
                if (isCacheExpired) {
                    val songsResult = tryApiCall { musicApiService.getSongs(page, PAGE_SIZE) }
                    if (songsResult.isOk()) {
                        val songs = songsResult.forceOk()
                        songDao.clear()
                        songDao.insert(SongDomainToDatabaseMapper().invoke(songs))
                        refreshSongsLastUpdatedTime(currentTimeInMs)
                    }
                    songsResult
                } else {
                    SongDatabaseToDomainMapper().invoke(songDao.getSongs()).ok()
                }
            } else {
                tryApiCall { musicApiService.getSongs(page, PAGE_SIZE) }
            }
        }
    }

    private fun refreshSongsLastUpdatedTime(currentTimeInMs: Long) {
        sharedPreferences.edit().putLong(
            SONGS_DATA_LAST_UPDATED,
            currentTimeInMs
        ).apply()
    }

    override suspend fun searchSongs(title: String, page: Int): Try<List<Song>> =
        withContext(Dispatchers.IO) {
            tryApiCall { musicApiService.searchSongs(title, page, PAGE_SIZE) }
        }
}