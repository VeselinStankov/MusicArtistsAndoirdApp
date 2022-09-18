package com.example.navigationtask.artists.mappers

import com.example.navigationtask.common.extentions.Convert
import com.example.navigationtask.data.database.model.ArtistEntity
import com.example.navigationtask.data.network.Artist

class ArtistDatabaseToDomainMapper : Convert<List<ArtistEntity>, List<Artist>> {
    override fun invoke(from: List<ArtistEntity>): List<Artist> = from.map {
        Artist(
            it.id,
            it.name,
            it.genre,
            it.albumCount,
            it.image,
            it.firstFanArt,
            it.secondFanArt,
            it.thirdFanArt,
            it.fourthFanArt,
            it.country,
            it.gender,
            it.fans,
            it.description
        )
    }
}