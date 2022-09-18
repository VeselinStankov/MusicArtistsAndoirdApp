package com.example.navigationtask.data

import com.example.navigationtask.common.Try
import com.example.navigationtask.data.network.Song

interface SongRepository {

    suspend fun getSongs(page: Int): Try<List<Song>>

    suspend fun searchSongs(title: String, page: Int): Try<List<Song>>
}