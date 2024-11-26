package com.example.movieapp.domain

import com.example.movieapp.data.local.FavoriteMovieDataSource
import com.example.movieapp.data.model.MovieDao
import com.example.movieapp.data.remote.RemoteMovieDataSource
import com.example.movieapp.models.Movie
import com.example.movieapp.models.MovieCategory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MovieRepository(
    private val remoteMovieDataSource: RemoteMovieDataSource,
    private val localMovieDataSource: FavoriteMovieDataSource
) {

    private val mutableMoviesFlow = MutableSharedFlow<List<Movie>>()
    val moviesFlow = mutableMoviesFlow.asSharedFlow()

    suspend fun getNowPlayingMovies() = mutableMoviesFlow.emit(
        remoteMovieDataSource.getNowPlayingMovies().results
            .map { it.mapToMovie(MovieCategory.NOW_PLAYING) }
    )

    suspend fun getPopularMovies() = mutableMoviesFlow.emit(
        remoteMovieDataSource.getPopularMovies().results
            .map { it.mapToMovie(MovieCategory.POPULAR) }
    )

    suspend fun getTopRatedMovies() = mutableMoviesFlow.emit(
        remoteMovieDataSource.getTopRatedMovies().results
            .map { it.mapToMovie(MovieCategory.TOP_RATED) }
    )

    suspend fun getUpcomingMovies() = mutableMoviesFlow.emit(
        remoteMovieDataSource.getUpcomingMovies().results
            .map { it.mapToMovie(MovieCategory.UPCOMING) }
    )

    /*
    TODO: Implement getMovie
    suspend fun getMovie(externalId: String) = remoteMovieDataSource.getMovie(externalId)
        .map(MovieDao::mapToMovie)
     */

    fun getFavourites() = localMovieDataSource.getFavourites()
        .map { movies ->
            movies.map {
                //Movie()
            }
        }

    suspend fun toggleFavourite(url: String) = localMovieDataSource.toggleFavorite(url)
}

fun MovieDao.mapToMovie(category: MovieCategory) = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = "https://image.tmdb.org/t/p/original/$posterPath",
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    releaseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(releaseDate) ?: Date(),
    adult = adult,
    budget = 0, //TODO get budget from movie details by using id
    genres = emptyList(), //TODO get genres from movie details by using id
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    popularity = popularity.toDouble(),
    productionCompanies = emptyList(), //TODO get production companies from movie details by using id
    productionCountries = emptyList(), //TODO get production countries from movie details by using id
    revenue = 0, //TODO get revenue from movie details by using id
    runtime = null, //TODO get runtime from movie details by using id
    spokenLanguages = emptyList(), //TODO get spoken languages from movie details by using id
    status = "", //TODO get status from movie details by using id
    video = video,
    category = category
)