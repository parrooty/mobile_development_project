package edu.ap.mobile_development_project.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.ap.osm.retrofit.Entry
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private var _entries : List<Entry>? = null
    private var _firstAddress: Entry? = null
    val firstAddress get() = _firstAddress

    fun getAddress(address: String) {
        viewModelScope.launch {

//           _entries = RetrofitClient.instance.getAddress(address, "json")
//            _firstAddress = _entries?.get(0) ?: Entry(0.0, 0.0)
        }
    }
}