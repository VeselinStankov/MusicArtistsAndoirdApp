package com.example.navigationtask.data

import android.content.SharedPreferences
import com.example.navigationtask.artists.mappers.ArtistDatabaseToDomainMapper
import com.example.navigationtask.artists.mappers.ArtistDomainToDatabaseMapper
import com.example.navigationtask.common.*
import com.example.navigationtask.data.database.dao.ArtistDao
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.data.network.MusicApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val PAGE_SIZE = 20
private const val ARTISTS_DATA_LAST_UPDATED = "artistsDataLastUpdated"
private const val DEFAULT_CACHE_EXPIRATION_TIME_IN_MS = 60 * 1000
private const val ARTISTS_DATA_LAST_UPDATED_NOT_SET = -1L

class ArtistRepositoryImpl @Inject constructor(
    private val musicApiService: MusicApiService,
    private val artistDao: ArtistDao,
    private val sharedPreferences: SharedPreferences
) : ArtistRepository, TryApiCall {
    override suspend fun getArtists(page: Int): Try<List<Artist>> {
        return withContext(Dispatchers.IO) {
            if (page == 1) {
                val currentTimeInMs = System.currentTimeMillis()
                val artistsCacheLastUpdated =
                    sharedPreferences.getLong(
                        ARTISTS_DATA_LAST_UPDATED,
                        ARTISTS_DATA_LAST_UPDATED_NOT_SET
                    )
                val isCacheExpired = artistsCacheLastUpdated == ARTISTS_DATA_LAST_UPDATED_NOT_SET ||
                        currentTimeInMs - artistsCacheLastUpdated > DEFAULT_CACHE_EXPIRATION_TIME_IN_MS
                if (isCacheExpired) {
                    val artistsResult = tryApiCall { musicApiService.getArtists(page, PAGE_SIZE) }
                    if (artistsResult.isOk()) {
                        val artists = artistsResult.forceOk()
                        artistDao.clear()
                        artistDao.insert(ArtistDomainToDatabaseMapper().invoke(artists))
                        refreshArtistsLastUpdatedTime(currentTimeInMs)
                    }
                    artistsResult
                } else {
                    ArtistDatabaseToDomainMapper().invoke(artistDao.getArtists()).ok()
                }
            } else {
                tryApiCall { musicApiService.getArtists(page, PAGE_SIZE) }
            }
        }
    }

    private fun refreshArtistsLastUpdatedTime(currentTimeInMs: Long) {
        sharedPreferences.edit().putLong(
            ARTISTS_DATA_LAST_UPDATED,
            currentTimeInMs
        ).apply()
    }

    override suspend fun searchArtists(name: String, page: Int): Try<List<Artist>> =
        withContext(Dispatchers.IO) {
            tryApiCall { musicApiService.searchArtists(name, page, PAGE_SIZE) }
        }
}