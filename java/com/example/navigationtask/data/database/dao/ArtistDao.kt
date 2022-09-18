package com.example.navigationtask.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.navigationtask.data.database.model.ArtistEntity

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artists: List<ArtistEntity>)

    @Query("DELETE FROM artists")
    fun clear()

    @Query("SELECT * from artists")
    fun getArtists(): List<ArtistEntity>
}