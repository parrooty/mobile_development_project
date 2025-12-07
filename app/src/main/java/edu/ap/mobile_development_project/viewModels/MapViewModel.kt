package edu.ap.mobile_development_project.viewModels

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
    private var _reverseEntry: ReverseEntry? = null
    val reverseEntry get() = _reverseEntry


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
                _reverseEntry = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}