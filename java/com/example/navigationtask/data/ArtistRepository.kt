package com.example.navigationtask.data

import com.example.navigationtask.common.Try
import com.example.navigationtask.data.network.Artist

interface ArtistRepository {

    suspend fun getArtists(page: Int): Try<List<Artist>>

    suspend fun searchArtists(name: String, page: Int): Try<List<Artist>>
}