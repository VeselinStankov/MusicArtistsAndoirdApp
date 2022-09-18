package com.example.navigationtask.songs.mappers

import com.example.navigationtask.common.extentions.Convert
import com.example.navigationtask.data.network.Song
import com.example.navigationtask.feed.DataItem

class SongDomainToUIMapper : Convert<List<Song>, List<DataItem.Song>> {
    override fun invoke(from: List<Song>): List<DataItem.Song> = from.map {
        DataItem.Song(
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
            it.comments,
            it.views,
            it.lyrics
        )
    }
}