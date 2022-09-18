package com.example.navigationtask.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "genre")
    val genre: String,

    @ColumnInfo(name = "album_count")
    val albumCount: Int,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "fan_art_1")
    val firstFanArt: String,

    @ColumnInfo(name = "fan_art_2")
    val secondFanArt: String,

    @ColumnInfo(name = "fan_art_3")
    val thirdFanArt: String,

    @ColumnInfo(name = "fan_art_4")
    val fourthFanArt: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "gender")
    val gender: Boolean,

    @ColumnInfo(name = "fans")
    val fans: Int,

    @ColumnInfo(name = "description")
    val description: String,
)