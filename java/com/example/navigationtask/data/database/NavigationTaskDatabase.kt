package com.example.navigationtask.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.navigationtask.data.database.model.Account
import com.example.navigationtask.data.database.dao.AccountDao
import com.example.navigationtask.data.database.dao.ArtistDao
import com.example.navigationtask.data.database.model.ArtistEntity
import com.example.navigationtask.data.database.model.SongEntity
import com.example.navigationtask.data.database.dao.SongDao

@Database(
    entities = [Account::class, SongEntity::class, ArtistEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NavigationTaskDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val songDao: SongDao
    abstract val artistDao: ArtistDao

    companion object {
        @Volatile
        private var INSTANCE: NavigationTaskDatabase? = null

        fun getInstance(context: Context): NavigationTaskDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NavigationTaskDatabase::class.java,
                        "navigation_task_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}