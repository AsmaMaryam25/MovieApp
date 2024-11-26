package com.example.movieapp.data.remote

import retrofit2.Retrofit
import com.example.movieapp.BuildConfig

class RemoteMovieDataSource(private val retrofit: Retrofit) {

    private val apiKey = BuildConfig.TMDB_API

    private val movieApi: MovieApiService = retrofit.create(MovieApiService::class.java)

    suspend fun getMovie(externalId: String) = movieApi.getMovie(externalId, apiKey)

    suspend fun getNowPlayingMovies() = movieApi.getNowPlayingMovies(apiKey)

    suspend fun getPopularMovies() = movieApi.getPopularMovies(apiKey)

    suspend fun getTopRatedMovies() = movieApi.getTopRatedMovies(apiKey)

    suspend fun getUpcomingMovies() = movieApi.getUpcomingMovies(apiKey)


}