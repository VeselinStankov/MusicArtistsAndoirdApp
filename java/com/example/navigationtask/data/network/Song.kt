package com.example.navigationtask.data.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: Long,
    @Json(name = "title")
    val songTitle: String,
    @Json(name = "artist_name")
    val artistName: String,
    @Json(name = "song_cover")
    val songCover: String,
    @Json(name = "fan_cover_1")
    val firstFanCover: String,
    @Json(name = "fan_cover_2")
    val secondFanCover: String,
    @Json(name = "fan_cover_3")
    val thirdFanCover: String,
    @Json(name = "fan_cover_4")
    val fourthFanCover: String,
    @Json(name = "likes")
    val likes: Int,
    @Json(name = "genre")
    val genre: String,
    @Json(name = "views")
    val views: Int,
    @Json(name = "comments")
    val comments: Int,
    @Json(name = "lyrics")
    val lyrics: String,
) : Parcelable