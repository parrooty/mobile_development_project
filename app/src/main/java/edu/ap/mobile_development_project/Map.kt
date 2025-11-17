package edu.ap.mobile_development_project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import edu.ap.osm.retrofit.Entry
import edu.ap.osm.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Map(viewModel : OSMViewModel) {
    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val ap = GeoPoint(51.230167, 4.416129)
        val scope = rememberCoroutineScope()
        // AndroidView for MapView
        AndroidView(
            factory = {
                val map = MapView(it).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    controller.setZoom(20.0)
                    controller.setCenter(ap)
                }
                mapView = map // Save reference for later
                map
            },
            update = { view ->
                mapView = view
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                scope.launch {
                    /*viewModel.getAddress(inputText)
                    delay(3000) // better to use callback : see below
                    viewModel.firstAddress?.let {
                        mapView?.controller?.setCenter(GeoPoint(it.lat, it.lon))
                    }*/

                    val call = RetrofitClient.instance.getAddress(inputText, "json")
                    call.enqueue(object : Callback<List<Entry>> {
                        override fun onResponse(call: Call<List<Entry>>, response: Response<List<Entry>>) {
                            if (response.isSuccessful && response.body()!=null){
                                mapView?.controller?.setCenter(GeoPoint(response.body()?.get(0)?.lat ?: 0.0, response.body()?.get(0)?.lon ?: 0.0))
                            }
                        }

                        override fun onFailure(call: Call<List<Entry>>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }
    }
}