package edu.ap.mobile_development_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import edu.ap.mobile_development_project.ui.theme.Mobile_development_projectTheme
import edu.ap.mobile_development_project.viewModels.AuthViewModel
import edu.ap.mobile_development_project.viewModels.CitiesViewModel
import edu.ap.mobile_development_project.viewModels.PoIViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val citiesViewModel: CitiesViewModel by viewModels()
    private val poiViewModel: PoIViewModel by viewModels()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Mobile_development_projectTheme {
                App(
                    authViewModel,
                    citiesViewModel,
                    poiViewModel
                )
            }
        }
    }
}
