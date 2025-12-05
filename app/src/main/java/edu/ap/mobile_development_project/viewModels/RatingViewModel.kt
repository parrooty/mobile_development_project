package edu.ap.mobile_development_project.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import edu.ap.mobile_development_project.domain.Rating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RatingViewModel : ViewModel() {
    private val db: DatabaseReference = Firebase.database.reference
    private val _ratings = MutableStateFlow<List<Rating>>(emptyList())
    val ratings: MutableStateFlow<List<Rating>> = _ratings

    val ratingListener = object : ChildEventListener {
        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {
            _ratings.value += snapshot.getValue(Rating::class.java)!!
        }

        override fun onChildChanged(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            _ratings.value -= snapshot.getValue(Rating::class.java)!!
        }

        override fun onChildMoved(
            snapshot: DataSnapshot,
            previousChildName: String?
        ) {

        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    init {
        loadRatings()
        db.child("ratings").addChildEventListener(ratingListener)
    }

    fun loadRatings() {
        viewModelScope.launch {
            try {
                val snapshot = db.child("ratings").get().await()
                val loaded = snapshot.children.mapNotNull { it.getValue(Rating::class.java) }
                _ratings.value = loaded
            } catch (e: Exception) {
                Log.e("RatingViewModel", "Failed to load ratings", e)
            }
        }
    }

    fun addRating(rating: Rating) {
        val key = db.child("ratings").push().key

        if (key == null) {
            Log.w("RatingViewModel", "Couldn't get push key for ratings")
            return
        }

        rating.id = key

        db.child("ratings").child(key).push().setValue(rating)
    }
}