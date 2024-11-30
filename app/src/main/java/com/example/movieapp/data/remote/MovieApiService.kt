package com.example.movieapp.data.remote

import com.example.movieapp.data.model.CollectionDao
import com.example.movieapp.data.model.CreditsDao
import com.example.movieapp.data.model.MovieDao
import com.example.movieapp.data.model.QueryDao
import com.example.movieapp.data.model.VideosDao
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/{external_id}")
    suspend fun getMovie(
        @Path("external_id") externalId: String,
        @Query("api_key") apiKey: String,
    ): MovieDao

    @GET("movie/{external_id}/credits")
    suspend fun getCredits(
        @Path("external_id") externalId: String,
        @Query("api_key") apiKey: String,
    ): CreditsDao

    @GET("movie/{external_id}/videos")
    suspend fun getVideos(
        @Path("external_id") externalId: String,
        @Query("api_key") apiKey: String,
    ): VideosDao

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String
    ): CollectionDao

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String
    ): CollectionDao

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String
    ): CollectionDao

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String
    ): CollectionDao

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): QueryDao
}