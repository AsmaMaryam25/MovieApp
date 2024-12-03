package com.example.movieapp.domain

import com.example.movieapp.data.local.FavoriteMovieDataSource
import com.example.movieapp.data.local.WatchListMovieDataSource
import com.example.movieapp.data.model.*
import com.example.movieapp.data.remote.RemoteMovieDataSource
import com.example.movieapp.models.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieRepository(
    private val remoteMovieDataSource: RemoteMovieDataSource,
    private val localFavoriteMovieDataSource: FavoriteMovieDataSource,
    private val localWatchlistMovieDataSource: WatchListMovieDataSource,
    val firestore: FirebaseFirestore
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
            ?.map { it.mapToMovie(MovieCategory.NOW_PLAYING, movieGenres) } ?: emptyList())
    }

    fun getPopularMovies(): Flow<List<CollectionMovie>> = flow {
        emit(remoteMovieDataSource.getPopularMovies().results
            ?.map { it.mapToMovie(MovieCategory.POPULAR, movieGenres) } ?: emptyList())
    }

    fun getTopRatedMovies(): Flow<List<CollectionMovie>> = flow {
        emit(remoteMovieDataSource.getTopRatedMovies().results
            ?.map { it.mapToMovie(MovieCategory.TOP_RATED, movieGenres) } ?: emptyList())
    }

    fun getUpcomingMovies(): Flow<List<CollectionMovie>> = flow {
        emit(remoteMovieDataSource.getUpcomingMovies().results
            ?.map { it.mapToMovie(MovieCategory.UPCOMING, movieGenres) } ?: emptyList())
    }

    fun searchMovies(query: String): Flow<List<SearchMovie>> = flow {
        emit(remoteMovieDataSource.searchMovies(query).results
            ?.map { it.mapToMovie() } ?: emptyList())
    }

    fun getMovie(externalId: Int): Flow<LocalMovie> = flow {
        emit(
            remoteMovieDataSource.getMovie(externalId.toString())
                ?.mapToMovie(MovieCategory.SPECIFIC, this@MovieRepository) ?: LocalMovie()
        )
    }

    fun getCredits(externalId: Int): Flow<Credits> = flow {
        emit(remoteMovieDataSource.getCredits(externalId.toString())?.mapToCredits() ?: Credits())
    }

    fun getVideoLink(externalId: Int): Flow<String?> = flow {
        emit(remoteMovieDataSource.getVideos(externalId.toString()).results
            ?.filter { it.official == true && it.type == "Trailer" && it.site == "YouTube" }
            ?.firstOrNull()?.key)
    }

    fun getFavorites() = localFavoriteMovieDataSource.getFavorites()

    suspend fun toggleFavorite(id: String?, title: String, posterPath: String?, rating: Double) =
        localFavoriteMovieDataSource.toggleFavorite(id, title, posterPath, rating)

    fun getWatchlist() = localWatchlistMovieDataSource.getWatchlist()

    suspend fun toggleWatchlist(id: String?, title: String, posterPath: String?, rating: Double) =
        localWatchlistMovieDataSource.toggleWatchlist(id, title, posterPath, rating)

    suspend fun getAverageRating(id: String): Double {
        val ratingsRef = firestore.collection("ratings").document(id)
        val snapshot = ratingsRef.get().await()
        return if (snapshot.exists()) {
            val currentData = snapshot.data
            currentData?.get("averageRating") as? Double ?: 0.0
        } else {
            0.0
        }
    }
}

fun CollectionMovieDao.mapToMovie(category: MovieCategory, movieGenres: Map<Int, String>) =
    CollectionMovie(
        genres = genreIds?.map { Genre(it, movieGenres[it].toString()) } ?: emptyList(),
        id = id ?: 0,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = "https://image.tmdb.org/t/p/original/${posterPath.orEmpty()}",
        backdropPath = "https://image.tmdb.org/t/p/original/${backdropPath.orEmpty()}",
        releaseDate = if (releaseDate.isNullOrEmpty()) LocalDate.MIN else LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        adult = adult == true,
        originalLanguage = originalLanguage.orEmpty(),
        originalTitle = originalTitle.orEmpty(),
        popularity = popularity ?: 0.0,
        video = video ?: false,
        category = category,
    )

suspend fun MovieDao.mapToMovie(category: MovieCategory, movieRepository: MovieRepository) = LocalMovie(
    id = id ?: 0,
    title = originalTitle.orEmpty(),
    overview = overview.orEmpty(),
    posterPath = "https://image.tmdb.org/t/p/original/${posterPath.orEmpty()}",
    backdropPath = "https://image.tmdb.org/t/p/original/${backdropPath.orEmpty()}",
    releaseDate = if (releaseDate.isNullOrEmpty()) LocalDate.MIN else LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    adult = adult ?: false,
    budget = budget ?: 0,
    genres = genreDaos?.map { it.mapToGenre() } ?: emptyList(),
    originalLanguage = originalLanguage.orEmpty(),
    originalTitle = originalTitle.orEmpty(),
    popularity = popularity ?: 0.0,
    productionCompanies = productionCompanies?.map { it.mapToProductionCompany() } ?: emptyList(),
    productionCountries = productionCountries?.map { it.mapToProductionCountry() } ?: emptyList(),
    revenue = revenue ?: 0,
    runtime = runtime ?: 0,
    spokenLanguages = spokenLanguageDaos?.map { it.mapToSpokenLanguage() } ?: emptyList(),
    status = status.orEmpty(),
    video = video ?: false,
    category = category,
    avgRating = movieRepository.getAverageRating(id.toString())
)

fun SearchMovieDao.mapToMovie() = SearchMovie(
    id = id ?: 0,
    title = title.orEmpty(),
    overview = overview.orEmpty(),
    posterPath = "https://image.tmdb.org/t/p/original/${posterPath.orEmpty()}",
    backdropPath = "https://image.tmdb.org/t/p/original/${backdropPath.orEmpty()}",
    releaseDate = if (releaseDate.isNullOrEmpty()) LocalDate.MIN else LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    adult = adult ?: false,
    originalLanguage = originalLanguage.orEmpty(),
    originalTitle = originalTitle.orEmpty(),
    popularity = popularity ?: 0.0,
    video = video ?: false,
)

fun GenreDao.mapToGenre() = Genre(
    id = id ?: 0,
    name = name.orEmpty()
)

fun ProductionCompanyDao.mapToProductionCompany() = ProductionCompany(
    id = id ?: 0,
    logoPath = logoPath.orEmpty(),
    name = name.orEmpty(),
    originCountry = originCountry.orEmpty()
)

fun ProductionCountryDao.mapToProductionCountry() = ProductionCountry(
    iso31661 = iso31661.orEmpty(),
    name = name.orEmpty()
)

fun SpokenLanguageDao.mapToSpokenLanguage() = SpokenLanguage(
    iso6391 = iso6391.orEmpty(),
    name = name.orEmpty()
)

fun CreditsDao.mapToCredits() = Credits(
    id = id ?: 0,
    cast = cast?.map { it.mapToCast() } ?: emptyList(),
    crew = crew?.map { it.mapToCrew() } ?: emptyList(),
)

fun CastDao.mapToCast() = Cast(
    id = id ?: 0,
    name = name.orEmpty(),
    originalName = originalName.orEmpty(),
    popularity = popularity ?: 0.0,
    profilePath = "https://image.tmdb.org/t/p/original/${profilePath.orEmpty()}",
    character = character.orEmpty(),
    order = order ?: 0
)

fun CrewDao.mapToCrew() = Crew(
    id = id ?: 0,
    name = name.orEmpty(),
    popularity = popularity ?: 0.0,
    profilePath = "https://image.tmdb.org/t/p/original/${profilePath.orEmpty()}",
    department = department.orEmpty(),
    job = job.orEmpty()
)