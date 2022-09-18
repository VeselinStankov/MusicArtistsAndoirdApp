package com.example.navigationtask.artists.mappers

import com.example.navigationtask.common.extentions.Convert
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.feed.DataItem

class ArtistUIToDomainMapper : Convert<DataItem.Artist, Artist> {
    override fun invoke(from: DataItem.Artist): Artist {
        return Artist(
            from.id,
            from.name,
            from.genre,
            from.albumCount,
            from.image,
            from.firstFanArt,
            from.secondFanArt,
            from.thirdFanArt,
            from.fourthFanArt,
            from.country,
            from.gender,
            from.fans,
            from.description
        )
    }
}