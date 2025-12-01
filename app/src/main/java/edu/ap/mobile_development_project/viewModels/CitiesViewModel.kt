package edu.ap.mobile_development_project.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import edu.ap.mobile_development_project.domain.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CitiesViewModel : ViewModel() {

    private val db: DatabaseReference = Firebase.database.reference
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities

    fun loadCities() {
        viewModelScope.launch {
            try {
                val snapshot = db.child("cities").get().await()
                val loaded = snapshot.children.mapNotNull { it.getValue(City::class.java) }
                _cities.value = loaded
            } catch (e: Exception) {
                Log.e("CitiesViewModel", "Failed to load cities", e)
            }
        }
    }

    fun addCity(city: City) {
        db.child("cities").push().setValue(city)
    }
}