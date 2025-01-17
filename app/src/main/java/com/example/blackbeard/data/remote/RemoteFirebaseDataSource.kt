package com.example.blackbeard.data.remote

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await

class RemoteFirebaseDataSource {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addRating(id: String, rating: Double, installationID: String) {
        val ratingsRef = firestore.collection("ratings").document(id)

        val snapshot = ratingsRef.get().await()
        if (snapshot.exists()) {
            val currentData = snapshot.data
            val currentRating = currentData?.get("rating") as Double
            val currentTotalRating = currentData["totalRating"] as Double
            val currentUserRatings = currentData["userRatings"] as Map<*, *>

            var newRating: Double
            var newTotalRating: Double
            val newCurrentUserRatings = currentUserRatings.toMutableMap()
            newCurrentUserRatings[installationID] = mapOf("rating" to rating)

            if (currentUserRatings.containsKey(installationID)) {
                newRating =
                    currentRating + rating - (currentUserRatings[installationID] as Map<*, *>)["rating"] as Double
                newTotalRating = currentTotalRating
            } else {
                newRating = currentRating + rating
                newTotalRating = currentTotalRating + 1
            }
            val newAverageRating = newRating / newTotalRating


            ratingsRef.update(
                "rating",
                newRating,
                "totalRating",
                newTotalRating,
                "averageRating",
                newAverageRating,
                "userRatings",
                newCurrentUserRatings
            ).await()
        } else {
            val initialData = mapOf(
                "rating" to rating,
                "totalRating" to 1.0,
                "averageRating" to rating,
                "userRatings" to mapOf(installationID to mapOf("rating" to rating))
            )
            ratingsRef.set(initialData).await()
        }
    }

    suspend fun getRating(movieId: String, userId: String): Double? {
        return try {
            val document = firestore
                .collection("ratings")
                .document(movieId.toString())
                .get()
                .await()

            if (document.exists()) {
                val userRatings = document.get("userRatings") as? Map<*, *>
                val userRating = userRatings?.get(userId) as? Map<*, *>
                val rating = userRating?.get("rating") as? Double
                rating
            } else {
                null
            }
        } catch (exception: Exception) {
            println("Error getting user rating: $exception")
            null
        }
    }

    suspend fun getVoterCount(id: String, voterCount: MutableLiveData<Int>) {
        val ratingsRef = firestore.collection("ratings").document(id)

        val snapshot = ratingsRef.get().await()
        if (snapshot.exists()) {
            val currentData = snapshot.data
            val currentUserRatings =
                currentData?.get("userRatings") as? Map<*, *> ?: emptyMap<Any, Any>()
            voterCount.postValue(currentUserRatings.size)
        } else {
            voterCount.postValue(0)
        }
    }

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

    suspend fun getInstallationID(): String {
        return FirebaseInstallations.getInstance().id.await()
    }
}