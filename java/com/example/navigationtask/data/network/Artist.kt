package com.example.navigationtask.data.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    @Json(name = "id")
    val id: Long,
    @Json(name = "name")
    val name: String,
    @Json(name = "genre")
    val genre: String,
    @Json(name = "album_count")
    val albumCount: Int,
    @Json(name = "image")
    val image: String,
    @Json(name = "fanart_1")
    val firstFanArt: String,
    @Json(name = "fanart_2")
    val secondFanArt: String,
    @Json(name = "fanart_3")
    val thirdFanArt: String,
    @Json(name = "fanart_4")
    val fourthFanArt: String,
    @Json(name = "country")
    val country: String,
    @Json(name = "gender")
    val gender: Boolean,
    @Json(name = "fans")
    val fans: Int,
    @Json(name = "description")
    val description: String
) : Parcelable