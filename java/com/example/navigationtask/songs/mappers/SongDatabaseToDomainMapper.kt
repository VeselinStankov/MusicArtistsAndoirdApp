package com.example.navigationtask.songs.mappers

import com.example.navigationtask.common.extentions.Convert
import com.example.navigationtask.data.database.model.SongEntity
import com.example.navigationtask.data.network.Song

class SongDatabaseToDomainMapper : Convert<List<SongEntity>, List<Song>> {
    override fun invoke(from: List<SongEntity>): List<Song> = from.map {
        Song(
            it.id,
            it.title,
            it.artistName,
            it.cover,
            it.firstFanCover,
            it.secondFanCover,
            it.thirdFanCover,
            it.fourthFanCover,
            it.likes,
            it.genre,
            it.views,
            it.comments,
            it.lyrics
        )
    }
}