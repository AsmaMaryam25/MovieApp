package com.example.blackbeard.data.remote

import android.util.Log
import com.example.blackbeard.BuildConfig
import com.example.blackbeard.data.model.QueryDao
import retrofit2.Retrofit

class RemoteMovieDataSource(private val retrofit: Retrofit) {

    private val apiKey = BuildConfig.TMDB_API

    private val movieApi: MovieApiService = retrofit.create(MovieApiService::class.java)

    suspend fun getMovie(externalId: String) = movieApi.getMovie(externalId, apiKey)

    suspend fun getCredits(externalId: String) = movieApi.getCredits(externalId, apiKey)

    suspend fun getVideos(externalId: String) = movieApi.getVideos(externalId, apiKey)

    suspend fun getReleaseDates(externalId: String) = movieApi.getReleaseDates(externalId, apiKey)

    suspend fun getNowPlayingMovies() = movieApi.getNowPlayingMovies(apiKey)

    suspend fun getPopularMovies() = movieApi.getPopularMovies(apiKey)

    suspend fun getTopRatedMovies() = movieApi.getTopRatedMovies(apiKey)

    suspend fun getUpcomingMovies() = movieApi.getUpcomingMovies(apiKey)

    suspend fun searchMovies(query: String, pageNum: Int) =
        movieApi.searchMovies(query, pageNum, apiKey)

    suspend fun discoverMovies(
        pageNum: Int,
        releaseDateGte: String?,
        releaseDateLte: String?,
        sortBy: String?,
        watchRegion: String?,
        withGenres: String?,
        withKeywords: String?,
        withWatchProviders: String?,
    ): QueryDao {
        val queryParams = mutableMapOf<String, String>().apply {
            put("page", pageNum.toString())
            releaseDateGte?.let { put("release_date.gte", it) }
            releaseDateLte?.let { put("release_date.lte", it) }
            sortBy?.let { put("sort_by", it) }
            watchRegion?.let { put("watch_region", it) }
            withGenres?.let { put("with_genres", it) }
            withKeywords?.let { put("with_keywords", it) }
            withWatchProviders?.let { put("with_watch_providers", it) }
            put("api_key", apiKey)
        }

        Log.d("RemoteMovieDataSource", "discoverMovies: $queryParams")

        return movieApi.discoverMovies(queryParams)
    }

    suspend fun getStreamingServices(externalId: String) =
        movieApi.getStreamingServices(externalId, apiKey)
}