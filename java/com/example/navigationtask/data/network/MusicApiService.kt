package com.example.navigationtask.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("api/v1/songs")
    suspend fun getSongs(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Song>

    @GET("api/v1/artists")
    suspend fun getArtists(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Artist>

    @GET("api/v1/songs")
    suspend fun searchSongs(
        @Query("title") title: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Song>

    @GET("api/v1/artists")
    suspend fun searchArtists(
        @Query("name") name: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<Artist>
}