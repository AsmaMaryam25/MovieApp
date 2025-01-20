package com.example.blackbeard.domain

import androidx.lifecycle.MutableLiveData
import com.example.blackbeard.R
import com.example.blackbeard.data.local.FavoriteMovieDataSource
import com.example.blackbeard.data.local.ThemeDataSource
import com.example.blackbeard.data.local.WatchListMovieDataSource
import com.example.blackbeard.data.model.*
import com.example.blackbeard.data.remote.RemoteFirebaseDataSource
import com.example.blackbeard.data.remote.RemoteMovieDataSource
import com.example.blackbeard.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieRepository(
    private val remoteMovieDataSource: RemoteMovieDataSource,
    private val remoteFirebaseDataSource: RemoteFirebaseDataSource,
    private val localFavoriteMovieDataSource: FavoriteMovieDataSource,
    private val localWatchlistMovieDataSource: WatchListMovieDataSource,
    private val localThemeDataSource: ThemeDataSource,
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

    fun getNowPlayingMovies(): Flow<Result<List<CollectionMovie>, NetworkError>> = flow {
        val response = remoteMovieDataSource.getNowPlayingMovies().results?.map {
            it.mapToMovie(
                MovieCategory.NOW_PLAYING,
                movieGenres
            )
        }
        emit(handleCollectionResponse(response))
    }.catch {
        emit(Result.Error(NetworkError.UNKNOWN))
    }

    fun getPopularMovies(): Flow<Result<List<CollectionMovie>, NetworkError>> = flow {
        val response = remoteMovieDataSource.getPopularMovies().results?.map {
            it.mapToMovie(
                MovieCategory.POPULAR,
                movieGenres
            )
        }
        emit(handleCollectionResponse(response))
    }.catch {
        emit(Result.Error(NetworkError.UNKNOWN))
    }

    fun getTopRatedMovies(): Flow<Result<List<CollectionMovie>, NetworkError>> = flow {
        val response = remoteMovieDataSource.getTopRatedMovies().results?.map {
            it.mapToMovie(
                MovieCategory.TOP_RATED,
                movieGenres
            )
        }
        emit(handleCollectionResponse(response))
    }.catch {
        emit(Result.Error(NetworkError.UNKNOWN))
    }

    fun getUpcomingMovies(): Flow<Result<List<CollectionMovie>, NetworkError>> = flow {
        val response = remoteMovieDataSource.getUpcomingMovies().results?.map {
            it.mapToMovie(
                MovieCategory.UPCOMING,
                movieGenres
            )
        }
        emit(handleCollectionResponse(response))
    }.catch {
        emit(Result.Error(NetworkError.UNKNOWN))
    }

    private fun <T> handleCollectionResponse(response: List<T>?): Result<List<T>, NetworkError> {
        return if (!response.isNullOrEmpty()) {
            Result.Success(response)
        } else {
            Result.Error(NetworkError.EMPTY_RESPONSE)
        }
    }

    fun searchMovies(query: String, pageNum: Int): Flow<MovieSearchResult> = flow {
        val response = remoteMovieDataSource.searchMovies(query, pageNum)

        val movies = response.results?.map { it.mapToMovie() } ?: emptyList()

        val totalPages = response.totalPages

        emit(MovieSearchResult(movies, totalPages))
    }

    fun getMovie(externalId: Int): Flow<LocalMovie> = flow {
        emit(
            remoteMovieDataSource.getMovie(externalId.toString())
                .mapToMovie(MovieCategory.SPECIFIC, this@MovieRepository)
        )
    }.catch {
        emit(LocalMovie())
    }

    fun discoverMovies(
        pageNum: Int,
        releaseDateGte: String?,
        releaseDateLte: String?,
        sortBy: String?,
        watchRegion: String?,
        withGenres: String?,
        withWatchProviders: String?,
    ): Flow<MovieSearchResult> = flow {
        val response = remoteMovieDataSource.discoverMovies(
            pageNum,
            releaseDateGte,
            releaseDateLte,
            sortBy,
            watchRegion,
            withGenres,
            withWatchProviders,
        )
        val movies = response.results?.map { it.mapToMovie() } ?: emptyList()
        emit(MovieSearchResult(movies, response.totalPages))
    }

    fun getCredits(externalId: Int): Flow<Credits> = flow {
        emit(remoteMovieDataSource.getCredits(externalId.toString()).mapToCredits())
    }.catch {
        emit(Credits())
    }

    fun getVideoLink(externalId: Int): Flow<String?> = flow {
        emit(remoteMovieDataSource.getVideos(externalId.toString())
            .results
            ?.firstOrNull { it.official == true && it.type == "Trailer" && it.site == "YouTube" }?.key
        )
    }.catch {
        emit("")
    }

    fun getStreamingServices(externalId: Int): Flow<List<StreamingService>?> = flow {
        emit(
            remoteMovieDataSource.getStreamingServices(externalId.toString()).results?.getValue(
                "DK"
            )?.mapToStreamingServices()
        )
    }.catch {
        emit(emptyList())
    }

    fun getFavorites() = localFavoriteMovieDataSource.getFavorites()

    suspend fun toggleFavorite(id: String?, title: String, posterPath: String?, rating: Double) =
        localFavoriteMovieDataSource.toggleFavorite(id, title, posterPath, rating)

    fun getWatchlist() = localWatchlistMovieDataSource.getWatchlist()

    suspend fun toggleWatchlist(id: String?, title: String, posterPath: String?, rating: Double) =
        localWatchlistMovieDataSource.toggleWatchlist(id, title, posterPath, rating)

    fun getTheme() = localThemeDataSource.isDarkModeEnabled()

    fun getAgeRating(externalId: Int): Flow<AgeRating> = flow {
        emit(remoteMovieDataSource.getReleaseDates(externalId.toString()).mapToAgeRating())
    }.catch {
        emit(
            AgeRating(
                "",
                -1
            )
        )
    }

    suspend fun setTheme(enabled: Boolean) = localThemeDataSource.setDarkModeEnabled(enabled)

    suspend fun getAverageRating(id: String): Double {
        return remoteFirebaseDataSource.getAverageRating(id)
    }

    fun getMovieRating(movieId: String, userId: String): Flow<Double?> = flow {
        emit(getRating(movieId, userId))
    }

    private suspend fun getRating(movieId: String, userId: String): Double? {
        return remoteFirebaseDataSource.getRating(movieId, userId)
    }

    suspend fun addRating(id: String, rating: Double, installationID: String) {
        remoteFirebaseDataSource.addRating(id, rating, installationID)
    }

    suspend fun getVoterCount(id: String, voterCount: MutableLiveData<Int>) {
        return remoteFirebaseDataSource.getVoterCount(id, voterCount)
    }

    suspend fun getInstallationID(): String {
        return remoteFirebaseDataSource.getInstallationID()
    }
}

private fun getImageId(certification: String?): Int {
    return when (certification) {
        "A" -> R.drawable.det_tilladt_for_alle
        "7" -> R.drawable.det_tilladt_for_alle__men_frar_des_b_rn_under_7__r
        "11" -> R.drawable.tilladt_for_born_over_11r
        "15" -> R.drawable.tilladt_for_b_rn_over_15__r
        else -> -1
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
        releaseDate = if (releaseDate.isNullOrEmpty()) LocalDate.MIN else LocalDate.parse(
            releaseDate,
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ),
        adult = adult == true,
        originalLanguage = originalLanguage.orEmpty(),
        originalTitle = originalTitle.orEmpty(),
        popularity = popularity ?: 0.0,
        video = video == true,
        category = category,
    )

suspend fun MovieDao.mapToMovie(category: MovieCategory, movieRepository: MovieRepository) =
    LocalMovie(
        id = id ?: 0,
        title = originalTitle.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = "https://image.tmdb.org/t/p/original/${posterPath.orEmpty()}",
        backdropPath = "https://image.tmdb.org/t/p/original/${backdropPath.orEmpty()}",
        releaseDate = if (releaseDate.isNullOrEmpty()) LocalDate.MIN else LocalDate.parse(
            releaseDate,
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ),
        adult = adult == true,
        budget = budget ?: 0,
        genres = genreDaos?.map { it.mapToGenre() } ?: emptyList(),
        originalLanguage = originalLanguage.orEmpty(),
        originalTitle = originalTitle.orEmpty(),
        popularity = popularity ?: 0.0,
        productionCompanies = productionCompanies?.map { it.mapToProductionCompany() }
            ?: emptyList(),
        productionCountries = productionCountries?.map { it.mapToProductionCountry() }
            ?: emptyList(),
        revenue = revenue ?: 0,
        runtime = runtime ?: 0,
        spokenLanguages = spokenLanguageDaos?.map { it.mapToSpokenLanguage() } ?: emptyList(),
        status = status.orEmpty(),
        video = video == true,
        category = category,
        avgRating = movieRepository.getAverageRating(id.toString())
    )

fun SearchMovieDao.mapToMovie() = SearchMovie(
    id = id ?: 0,
    title = title.orEmpty(),
    overview = overview.orEmpty(),
    posterPath = "https://image.tmdb.org/t/p/original/${posterPath.orEmpty()}",
    backdropPath = "https://image.tmdb.org/t/p/original/${backdropPath.orEmpty()}",
    releaseDate = if (releaseDate.isNullOrEmpty()) LocalDate.MIN else LocalDate.parse(
        releaseDate,
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    ),
    adult = adult == true,
    originalLanguage = originalLanguage.orEmpty(),
    originalTitle = originalTitle.orEmpty(),
    popularity = popularity ?: 0.0,
    video = video == true,
    voteAverage = voteAverage ?: 0.0,
    genres = genreIds,
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
    englishName = englishName.orEmpty(),
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

fun ReleaseDatesDao.mapToAgeRating() = AgeRating(
    rating = results.firstOrNull { it.iso31661 == "DK" }?.releaseDates?.firstOrNull()?.certification,
    imageResource = getImageId(results.firstOrNull { it.iso31661 == "DK" }?.releaseDates?.firstOrNull()?.certification)
)

fun CountryDao.mapToStreamingServices() = flatrate?.map { it.mapToStreamingService() }

fun ProviderDao.mapToStreamingService() = StreamingService(
    logoPath = "https://image.tmdb.org/t/p/original/$logoPath",
    providerName = providerName.orEmpty()
)

enum class NetworkError : Error {
    UNKNOWN,
    EMPTY_RESPONSE
}