package com.example.movieapp.domain

import com.example.movieapp.data.local.FavoriteMovieDataSource
import com.example.movieapp.data.model.CollectionMovieDao
import com.example.movieapp.data.model.GenreDao
import com.example.movieapp.data.model.MovieDao
import com.example.movieapp.data.model.ProductionCompanyDao
import com.example.movieapp.data.model.ProductionCountryDao
import com.example.movieapp.data.model.SpokenLanguageDao
import com.example.movieapp.data.remote.RemoteMovieDataSource
import com.example.movieapp.models.CollectionMovie
import com.example.movieapp.models.Genre
import com.example.movieapp.models.Movie
import com.example.movieapp.models.MovieCategory
import com.example.movieapp.models.ProductionCompany
import com.example.movieapp.models.ProductionCountry
import com.example.movieapp.models.SpokenLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieRepository(
    private val remoteMovieDataSource: RemoteMovieDataSource,
    private val localMovieDataSource: FavoriteMovieDataSource,
) {
    val movieGenres = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )

    fun getNowPlayingMovies(): Flow<List<CollectionMovie>> = flow {
        emit(remoteMovieDataSource.getNowPlayingMovies().results
            .map { it.mapToMovie(MovieCategory.NOW_PLAYING, movieGenres) })
    }

    fun getPopularMovies(): Flow<List<CollectionMovie>> = flow {
        emit(remoteMovieDataSource.getPopularMovies().results
            .map { it.mapToMovie(MovieCategory.POPULAR, movieGenres) })
    }

    fun getTopRatedMovies(): Flow<List<CollectionMovie>> = flow {
        emit(remoteMovieDataSource.getTopRatedMovies().results
            .map { it.mapToMovie(MovieCategory.TOP_RATED, movieGenres) })
    }

    fun getUpcomingMovies(): Flow<List<CollectionMovie>> = flow {
        emit(remoteMovieDataSource.getUpcomingMovies().results
            .map { it.mapToMovie(MovieCategory.UPCOMING, movieGenres) })
    }

    fun getMovie(externalId: Int): Flow<Movie> = flow {
        emit(remoteMovieDataSource.getMovie(externalId.toString()).mapToMovie(MovieCategory.SPECIFIC))
    }

    fun getFavourites() = localMovieDataSource.getFavourites()
        .map { movies ->
            movies.map {
                //Movie()
            }
        }

    suspend fun toggleFavourite(url: String) = localMovieDataSource.toggleFavorite(url)
}

fun CollectionMovieDao.mapToMovie(category: MovieCategory, movieGenres: Map<Int, String>) = CollectionMovie(
    genres = genreIds.map { Genre(it, movieGenres[it].toString()) },
    id = id,
    title = title,
    overview = overview,
    posterPath = "https://image.tmdb.org/t/p/original/$posterPath",
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    releaseDate = LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    adult = adult,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    popularity = popularity.toDouble(),
    video = video,
    category = category
)

fun MovieDao.mapToMovie(category: MovieCategory) = Movie(
    id = id,
    title = originalTitle,
    overview = overview,
    posterPath = "https://image.tmdb.org/t/p/original/$posterPath",
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    releaseDate = LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    adult = adult,
    budget = budget,
    genres = genreDaos.map { it.mapToGenre() },
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    popularity = popularity,
    productionCompanies = productionCompanies.map { it.mapToProductionCompany() },
    productionCountries = productionCountries.map { it.mapToProductionCountry() },
    revenue = revenue,
    runtime = runtime,
    spokenLanguages = spokenLanguageDaos.map { it.mapToSpokenLanguage() },
    status = status,
    video = video,
    category = category
)

fun GenreDao.mapToGenre() = Genre(
    id = id,
    name = name
)

fun ProductionCompanyDao.mapToProductionCompany() = ProductionCompany(
    id = id,
    logoPath = logoPath,
    name = name,
    originCountry = originCountry
)

fun ProductionCountryDao.mapToProductionCountry() = ProductionCountry(
    iso31661 = iso31661,
    name = name
)

fun SpokenLanguageDao.mapToSpokenLanguage() = SpokenLanguage(
    iso6391 = iso6391,
    name = name
)