package com.example.navigationtask.songs.mappers

import com.example.navigationtask.common.extentions.Convert
import com.example.navigationtask.data.network.Song
import com.example.navigationtask.feed.DataItem

class SongUIToDomainMapper : Convert<DataItem.Song, Song> {
    override fun invoke(from: DataItem.Song): Song {
        return Song(
            from.id,
            from.title,
            from.artistName,
            from.songCover,
            from.firstFanCover,
            from.secondFanCover,
            from.thirdFanCover,
            from.fourthFanCover,
            from.likes,
            from.genre,
            from.comments,
            from.views,
            from.lyrics
        )
    }
}