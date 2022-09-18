package com.example.navigationtask.songs.mappers

import com.example.navigationtask.common.extentions.Convert
import com.example.navigationtask.data.database.model.SongEntity
import com.example.navigationtask.data.network.Song

class SongDomainToDatabaseMapper : Convert<List<Song>, List<SongEntity>> {
    override fun invoke(from: List<Song>): List<SongEntity> = from.map {
        SongEntity(
            it.id,
            it.songTitle,
            it.artistName,
            it.songCover,
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