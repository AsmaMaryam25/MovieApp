package com.example.blackbeard.domain

import RecentSearchDataSource
import kotlinx.coroutines.flow.Flow

class RecentSearchRepository(private val recentSearchDataSource: RecentSearchDataSource) {

    fun getRecentSearches(): Flow<List<String>> {
        return recentSearchDataSource.getRecentSearches()
    }

    suspend fun addRecentSearch(query: String) {
        recentSearchDataSource.addRecentSearch(query)
    }

    suspend fun removeRecentSearch(query: String) {
        recentSearchDataSource.removeRecentSearch(query)
    }

    suspend fun clearRecentSearches() {
        recentSearchDataSource.clearRecentSearches()
    }
}