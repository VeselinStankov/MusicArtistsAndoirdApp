package com.example.navigationtask.artists.mappers

import com.example.navigationtask.common.extentions.Convert
import com.example.navigationtask.data.network.Artist
import com.example.navigationtask.feed.DataItem

class ArtistDomainToUIMapper : Convert<List<Artist>, List<DataItem.Artist>> {
    override fun invoke(from: List<Artist>): List<DataItem.Artist> = from.map {
        DataItem.Artist(
            it.id,
            it.name,
            it.albumCount,
            it.image,
            it.firstFanArt,
            it.secondFanArt,
            it.thirdFanArt,
            it.fourthFanArt,
            it.country,
            it.genre,
            it.gender,
            it.fans,
            it.description
        )
    }
}