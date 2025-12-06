package edu.ap.mobile_development_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import edu.ap.mobile_development_project.ui.theme.Mobile_development_projectTheme
import edu.ap.mobile_development_project.viewModels.AuthViewModel
import edu.ap.mobile_development_project.viewModels.CitiesViewModel
import edu.ap.mobile_development_project.viewModels.MapViewModel
import edu.ap.mobile_development_project.viewModels.PoIViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val citiesViewModel: CitiesViewModel by viewModels()
    private val poiViewModel: PoIViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        enableEdgeToEdge()
        setContent {
            Mobile_development_projectTheme {
                App(
                    authViewModel,
                    citiesViewModel,
                    poiViewModel,
                    mapViewModel,
                    fusedLocationClient
                )
            }
        }
    }
}
