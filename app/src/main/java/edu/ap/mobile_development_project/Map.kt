package edu.ap.mobile_development_project


import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.cameraPosition
import edu.ap.mobile_development_project.domain.PointOfInterest
import edu.ap.mobile_development_project.viewModels.MapViewModel
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun Map(
    pointsOfInterest: List<PointOfInterest> = emptyList(),
    navController: NavHostController,
    mapViewModel: MapViewModel,
    modifier: Modifier
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
        )
    )
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    if (locationPermissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                // Use .await() to safely get the location in a coroutine
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    // We have the location, now tell the ViewModel to find the city
                    mapViewModel.getReverse(location.latitude, location.longitude)
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition(userLatLng, 15f, 0f, 0f)
                        ),
                        durationMs = 1000
                    )
                }
            } catch (e: Exception) {
                // Handle cases where location is not available
                e.printStackTrace()
            }
        }
        // This manages the current camera position, which includes: LatLng (center of the map),
        // zoom level, bearing (rotation) & tilt (3D perspective).
//        val cameraPositionState = rememberCameraPositionState()

        GoogleMap(
            // Modifier defines how the map should be laid out in the UI.
            // fillMaxSize() ensures it takes up all available screen space.
            modifier = modifier,

            // Holds the current camera position state, including location (LatLng), zoom, tilt, and bearing.
            // You can also control or animate the camera from here.
            cameraPositionState = cameraPositionState,

            // MapProperties define the functional behavior of the map.
            // This includes things like enabling the user's current location,
            // choosing the map type (normal, satellite, terrain), traffic overlays, etc.
            properties = MapProperties(
                // Optional extras:
                isMyLocationEnabled = true,
                // mapType = MapType.NORMAL,
                // isTrafficEnabled = true,
                // isBuildingsEnabled = true,
                // minZoomPreference = 10f,
                // maxZoomPreference = 20f
            ),

            // MapUiSettings control the user interface and gestures available on the map.
            // This includes zoom buttons, gestures (scrolling, rotating, tilting),
            // compass visibility, and whether the "My Location" button appears.
            uiSettings = MapUiSettings(
                // Shows the "My Location" button if permission is granted.
                // Tapping it animates the camera to the user's current location.
                // Optional extras:
                myLocationButtonEnabled = true,
                compassEnabled = true,
                zoomControlsEnabled = true,
                scrollGesturesEnabled = true,
    //             rotateGesturesEnabled = true,
                tiltGesturesEnabled = false
            ),
        ) {
            if (pointsOfInterest.isNotEmpty()) {
                pointsOfInterest.forEach { poi ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(poi.lat, poi.lon)
                        ),
                        title = poi.name,
                        snippet = poi.description,
    //                    onClick = {
    //
    //                    },
                        onInfoWindowClick = {
                            navController.navigate(Screen.CommentScreen.name + "/${poi.id}")
                        }
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Location permission is needed to use the map.")
        }

        LaunchedEffect(Unit) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }
}