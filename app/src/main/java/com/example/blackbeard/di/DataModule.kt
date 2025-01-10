package com.example.blackbeard.di

import android.annotation.SuppressLint
import android.content.Context
import com.example.blackbeard.data.local.FavoriteMovieDataSource
import com.example.blackbeard.data.local.ThemeDataSource
import com.example.blackbeard.data.local.WatchListMovieDataSource
import com.example.blackbeard.data.remote.RemoteMovieDataSource
import com.example.blackbeard.domain.MovieRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object DataModule {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val CONTENT_TYPE = "application/json; charset=UTF8"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(
            json.asConverterFactory(CONTENT_TYPE.toMediaType())
        )
        .baseUrl(BASE_URL)
        .build()

    private val remoteMovieDataSource = RemoteMovieDataSource(retrofit)

    @SuppressLint("StaticFieldLeak")
    private lateinit var localFavoriteMovieDataSource: FavoriteMovieDataSource

    @SuppressLint("StaticFieldLeak")
    private lateinit var localWatchlistMovieDataSource: WatchListMovieDataSource

    @SuppressLint("StaticFieldLeak")
    private lateinit var localThemeDataSource: ThemeDataSource

    lateinit var movieRepository: MovieRepository

    fun initialize(context: Context) {
        localFavoriteMovieDataSource = FavoriteMovieDataSource(context)
        localWatchlistMovieDataSource = WatchListMovieDataSource(context)
        localThemeDataSource = ThemeDataSource(context)
        val firestore = FirebaseFirestore.getInstance()
        movieRepository = MovieRepository(
            remoteMovieDataSource,
            localFavoriteMovieDataSource,
            localWatchlistMovieDataSource,
            localThemeDataSource,
            firestore
        )
    }
}