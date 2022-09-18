package com.example.navigationtask.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L,

    @ColumnInfo(name = "song_title")
    val title: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "song_cover")
    val cover: String,

    @ColumnInfo(name = "first_fan_cover")
    val firstFanCover: String,

    @ColumnInfo(name = "second_fan_cover")
    val secondFanCover: String,

    @ColumnInfo(name = "third_fan_cover")
    val thirdFanCover: String,

    @ColumnInfo(name = "fourth_fan_cover")
    val fourthFanCover: String,

    @ColumnInfo(name = "likes")
    val likes: Int,

    @ColumnInfo(name = "genre")
    val genre: String,

    @ColumnInfo(name = "views")
    val views: Int,

    @ColumnInfo(name = "comments")
    val comments: Int,

    @ColumnInfo(name = "lyrics")
    val lyrics: String,
)