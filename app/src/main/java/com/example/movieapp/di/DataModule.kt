package com.example.movieapp.di

import android.annotation.SuppressLint
import android.content.Context
import com.example.movieapp.data.local.FavoriteMovieDataSource
import com.example.movieapp.data.remote.RemoteMovieDataSource
import com.example.movieapp.domain.MovieRepository
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType

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
    private lateinit var localMovieDataSource: FavoriteMovieDataSource

    lateinit var movieRepository: MovieRepository

    fun initialize(context: Context) {
        localMovieDataSource = FavoriteMovieDataSource(context)
        movieRepository = MovieRepository(remoteMovieDataSource, localMovieDataSource)
    }
}