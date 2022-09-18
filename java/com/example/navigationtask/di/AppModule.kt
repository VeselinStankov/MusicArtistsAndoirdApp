package com.example.navigationtask.di

import android.content.Context
import android.content.SharedPreferences
import com.example.navigationtask.data.*
import com.example.navigationtask.data.database.NavigationTaskDatabase
import com.example.navigationtask.data.database.dao.AccountDao
import com.example.navigationtask.data.database.dao.ArtistDao
import com.example.navigationtask.data.database.dao.SongDao
import com.example.navigationtask.data.network.MusicApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideAccountRepository(accountDao: AccountDao): AccountRepository {
        return AccountRepositoryImpl(accountDao)
    }

    @Provides
    fun provideArtistRepository(
        musicApiService: MusicApiService,
        artistDao: ArtistDao,
        sharedPreferences: SharedPreferences
    ): ArtistRepository {
        return ArtistRepositoryImpl(musicApiService, artistDao, sharedPreferences)
    }

    @Provides
    fun provideSongRepository(
        musicApiService: MusicApiService,
        songDao: SongDao,
        sharedPreferences: SharedPreferences
    ): SongRepository {
        return SongRepositoryImpl(musicApiService, songDao, sharedPreferences)
    }

    @Provides
    fun provideAccountDao(@ApplicationContext appContext: Context): AccountDao {
        return NavigationTaskDatabase.getInstance(appContext).accountDao
    }

    @Provides
    fun provideArtistDao(@ApplicationContext appContext: Context): ArtistDao {
        return NavigationTaskDatabase.getInstance(appContext).artistDao
    }

    @Provides
    fun provideSongDao(@ApplicationContext appContext: Context): SongDao {
        return NavigationTaskDatabase.getInstance(appContext).songDao
    }

    @Provides
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("MUSIC_APP_SHARED_PREFS", Context.MODE_PRIVATE)
    }
}