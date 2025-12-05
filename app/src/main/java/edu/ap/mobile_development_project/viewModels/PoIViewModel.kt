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
import edu.ap.mobile_development_project.screens.PointOfInterest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PoIViewModel : ViewModel() {
    private val db: DatabaseReference = Firebase.database.reference
    private val _pois = MutableStateFlow<List<PointOfInterest>>(emptyList())
    val pois: StateFlow<List<PointOfInterest>> = _pois

    val poiListener = object : ChildEventListener {
        override fun onChildAdded(
            snapshot: DataSnapshot, previousChildName: String?
        ) {
            _pois.value += snapshot.getValue(PointOfInterest::class.java)!!
        }

        override fun onChildChanged(
            snapshot: DataSnapshot, previousChildName: String?
        ) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            _pois.value -= snapshot.getValue(PointOfInterest::class.java)!!
        }

        override fun onChildMoved(
            snapshot: DataSnapshot, previousChildName: String?
        ) {

        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("PoIViewModel", "POIs Database Error", error.toException())
        }

    }

    init {
        loadPoIs()
        db.child("pois").addChildEventListener(poiListener)
    }

    fun loadPoIs() {
        viewModelScope.launch {
            try {
                val snapshot = db.child("pois").get().await()
                val loaded =
                    snapshot.children.mapNotNull { it.getValue(PointOfInterest::class.java) }
                _pois.value = loaded
            } catch (e: Exception) {
                Log.e("PoIViewModel", "Failed to load POIs", e)
            }
        }
    }

    fun addPoI(poi: PointOfInterest) {
        db.child("pois").push().setValue(poi)
    }

}