package edu.ap.mobile_development_project.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import edu.ap.mobile_development_project.domain.Comment
import edu.ap.mobile_development_project.domain.PointOfInterest
import edu.ap.mobile_development_project.domain.Rating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PoIViewModel : ViewModel() {
    private val db: DatabaseReference = Firebase.database.reference
    private val poiRef = db.child("pois")
    private val ratingsRef = db.child("ratings")
    private val commentsRef = db.child("comments")
    private val _pois = MutableStateFlow<List<PointOfInterest>>(emptyList())
    val pois: StateFlow<List<PointOfInterest>> = _pois

    private var allPois = listOf<PointOfInterest>()
    private var allRatingsByPoiId = mapOf<String, List<Rating>>()
    private var allCommentsByPoiId = mapOf<String, List<Comment>>()

    private var poisReady = false
    private var ratingsReady = false
    private var commentsReady = false

//    val poiListener = object : ChildEventListener {
//        override fun onChildAdded(
//            snapshot: DataSnapshot, previousChildName: String?
//        ) {
//            _pois.value += snapshot.getValue(PointOfInterest::class.java)!!
//        }
//
//        override fun onChildChanged(
//            snapshot: DataSnapshot, previousChildName: String?
//        ) {
//
//        }
//
//        override fun onChildRemoved(snapshot: DataSnapshot) {
//            _pois.value -= snapshot.getValue(PointOfInterest::class.java)!!
//        }
//
//        override fun onChildMoved(
//            snapshot: DataSnapshot, previousChildName: String?
//        ) {
//
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            Log.e("PoIViewModel", "POIs Database Error", error.toException())
//        }
//
//    }

    private val poiValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            allPois = snapshot.children.mapNotNull { it.getValue(PointOfInterest::class.java) }
            poisReady = true
            combineData()
        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("PoIViewModel", "POIs Database Error", error.toException())
        }
    }

    private val ratingsValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val ratings = snapshot.children.mapNotNull { it.getValue(Rating::class.java) }
            allRatingsByPoiId = ratings.groupBy { it.pointOfInterestId }
            ratingsReady = true
            combineData()
        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("PoIViewModel", "Ratings Database Error", error.toException())
        }
    }

    private val commentsValueListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val comments = snapshot.children.mapNotNull { it.getValue(Comment::class.java) }
            allCommentsByPoiId = comments.groupBy { it.pointOfInterestId }
            commentsReady = true
            combineData()
        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("PoIViewModel", "Comments Database Error", error.toException())
        }
    }

    init {
//        loadPoIs()
//        db.child("pois").addChildEventListener(poiListener)
//        attachRatingsListener()
//        attachCommentsListener()
//        attachPoisListener()
    }

    fun refresh() {
        Log.d("PoIViewModel", "Refreshing ViewModel data...")

        // 1. DETACH existing listeners to prevent duplicates.
        poiRef.removeEventListener(poiValueListener)
        ratingsRef.removeEventListener(ratingsValueListener)
        commentsRef.removeEventListener(commentsValueListener)

        // 2. RESET the state. This is crucial.
        poisReady = false
        ratingsReady = false
        commentsReady = false
        // Optionally, clear the UI immediately while loading
        _pois.value = emptyList()

        // 3. RE-ATTACH the listeners to fetch fresh data.
        poiRef.addValueEventListener(poiValueListener)
        ratingsRef.addValueEventListener(ratingsValueListener)
        commentsRef.addValueEventListener(commentsValueListener)
    }

    private fun combineData() {
        // --- FIX 2: Add a guard to ensure all data is ready before combining ---
        // This stops combineData from running with incomplete information.
        if (!poisReady || !ratingsReady || !commentsReady) {
            return
        }
        // --- End of Fix 2 ---

        val combinedPois = allPois.map { poi ->
            val ratingsForPoi = allRatingsByPoiId[poi.id] ?: emptyList()
            val commentsForPoi = allCommentsByPoiId[poi.id] ?: emptyList()
            poi.copy(ratings = ratingsForPoi, comments = commentsForPoi)
        }
        _pois.value = combinedPois
    }

//    fun loadPoIs() {
//        viewModelScope.launch {
//            try {
//                val snapshot = db.child("pois").get().await()
//                val loaded =
//                    snapshot.children.mapNotNull { it.getValue(PointOfInterest::class.java) }
//                _pois.value = loaded
//            } catch (e: Exception) {
//                Log.e("PoIViewModel", "Failed to load POIs", e)
//            }
//        }
//    }

    fun getPoIByIdAsFlow(id: String): StateFlow<PointOfInterest?> {
        return _pois.map { poiList ->
            poiList.find { it.id == id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    }

    fun addPoI(poi: PointOfInterest) {
        val key = db.child("pois").push().key ?: throw Exception("Key is null")
        poi.id = key
        db.child("pois").child(key).setValue(poi)
    }

    fun addRating(rating: Rating) {
        val query = ratingsRef
            .orderByChild("pointOfInterestId")
            .equalTo(rating.pointOfInterestId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var existingRatingId: String? = null
                var ratingToUpdate: Rating? = null

                for (ratingSnapshot in snapshot.children) {
                    val existingRating = ratingSnapshot.getValue(Rating::class.java)
                    if (existingRating?.userId == rating.userId) {
                        existingRatingId = ratingSnapshot.key
                        ratingToUpdate = existingRating
                        break
                    }
                }

                if (existingRatingId != null && ratingToUpdate != null) {
                    val updatedRating = ratingToUpdate.copy(rating = rating.rating)
                    ratingsRef.child(existingRatingId).setValue(updatedRating)
                        .addOnSuccessListener {
                            Log.d(
                                "PoIViewModel",
                                "Rating updated successfully for PoI: ${rating.pointOfInterestId}"
                            )
                        }
                        .addOnFailureListener {
                            Log.e("PoIViewModel", "Failed to update rating", it)
                        }
                } else {
                    val key = ratingsRef.push().key ?: return
                    val ratingWithId = rating.copy(id = key)
                    ratingsRef.child(key).setValue(ratingWithId)
                        .addOnSuccessListener {
                            Log.d(
                                "PoIViewModel",
                                "Rating added successfully for PoI: ${rating.pointOfInterestId}"
                            )
                        }
                        .addOnFailureListener {
                            Log.e("PoIViewModel", "Failed to add rating", it)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PoIViewModel", "Failed to check for existing rating", error.toException())
            }
        })
    }

    fun addComment(comment: Comment) {
        val key = commentsRef.push().key ?: return
        val newComment = comment.copy(id = key)
        commentsRef.child(key).setValue(newComment)
            .addOnSuccessListener {
                Log.d(
                    "PoIViewModel",
                    "Comment added successfully for PoI: ${comment.pointOfInterestId}"
                )
            }
            .addOnFailureListener {
                Log.e("PoIViewModel", "Failed to add comment", it)
            }
    }
}