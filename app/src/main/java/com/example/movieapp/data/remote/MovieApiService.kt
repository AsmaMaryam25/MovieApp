package com.example.movieapp.data.remote

import com.example.movieapp.data.model.CollectionDao
import com.example.movieapp.models.Movie
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("find/{external_id}")
    suspend fun getMovie(
        @Path("external_id") externalId: String,
        @Query("api_key") apiKey: String
    ): Movie

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
}