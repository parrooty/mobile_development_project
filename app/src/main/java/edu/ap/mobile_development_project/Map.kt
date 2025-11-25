package edu.ap.mobile_development_project

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun Map(modifier: Modifier) {
    // This manages the current camera position, which includes: LatLng (center of the map),
    // zoom level, bearing (rotation) & tilt (3D perspective).
    val cameraPositionState = rememberCameraPositionState()

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
            // compassEnabled = true,
            // zoomControlsEnabled = true,
            // scrollGesturesEnabled = true,
            // rotateGesturesEnabled = true,
            // tiltGesturesEnabled = false
        )
    )
}