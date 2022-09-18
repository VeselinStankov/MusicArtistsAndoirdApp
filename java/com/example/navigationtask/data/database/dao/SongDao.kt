package com.example.navigationtask.data.database.dao

import androidx.room.*
import com.example.navigationtask.data.database.model.SongEntity

@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(songs: List<SongEntity>)

    @Query("DELETE FROM songs")
    fun clear()

    @Query("SELECT * from songs")
    fun getSongs(): List<SongEntity>
}