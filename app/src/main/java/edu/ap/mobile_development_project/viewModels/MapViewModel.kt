package edu.ap.mobile_development_project.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ap.mobile_development_project.retrofit.ReverseEntry
import edu.ap.osm.retrofit.Entry
import edu.ap.osm.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private var _entries : List<Entry>? = null
    private var _firstAddress: Entry? = null
    val firstAddress get() = _firstAddress
//    private var _reverseEntry: ReverseEntry? = null
//    val reverseEntry get() = _reverseEntry

    var reverseEntry: ReverseEntry? by mutableStateOf(null)
        private set


    fun getAddress(address: String) {
        viewModelScope.launch {
//           _entries = RetrofitClient.instance.getAddress(address, "json").execute().body()
//           _firstAddress = _entries?.get(0) ?: Entry(0.0, 0.0)
        }
    }

    fun getReverse(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = RetrofitClient.instance.getReverse(lat, lon, "json")
                reverseEntry = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}