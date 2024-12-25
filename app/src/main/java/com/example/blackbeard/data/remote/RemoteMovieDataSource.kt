package com.example.blackbeard.data.remote

import com.example.blackbeard.BuildConfig
import retrofit2.Retrofit

class RemoteMovieDataSource(private val retrofit: Retrofit) {

    private val apiKey = BuildConfig.TMDB_API

    private val movieApi: MovieApiService = retrofit.create(MovieApiService::class.java)

    suspend fun getMovie(externalId: String) = movieApi.getMovie(externalId, apiKey)

    suspend fun getCredits(externalId: String) = movieApi.getCredits(externalId, apiKey)

    suspend fun getVideos(externalId: String) = movieApi.getVideos(externalId, apiKey)

    suspend fun getNowPlayingMovies() = movieApi.getNowPlayingMovies(apiKey)

    suspend fun getPopularMovies() = movieApi.getPopularMovies(apiKey)

    suspend fun getTopRatedMovies() = movieApi.getTopRatedMovies(apiKey)

    suspend fun getUpcomingMovies() = movieApi.getUpcomingMovies(apiKey)

    suspend fun searchMovies(query: String, pageNum: Int) = movieApi.searchMovies(query, pageNum, apiKey)
}