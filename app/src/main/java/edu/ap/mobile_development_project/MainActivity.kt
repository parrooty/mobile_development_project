package edu.ap.mobile_development_project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import edu.ap.mobile_development_project.domain.City
import edu.ap.mobile_development_project.screens.AddCityScreen
import edu.ap.mobile_development_project.screens.LoginScreen
import edu.ap.mobile_development_project.screens.OverviewScreen
import edu.ap.mobile_development_project.ui.theme.Mobile_development_projectTheme
import edu.ap.mobile_development_project.viewModels.AuthViewModel
import edu.ap.mobile_development_project.viewModels.CitiesViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val citiesViewModel: CitiesViewModel by viewModels()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Mobile_development_projectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()
                    val currentUser by authViewModel.currentUser.collectAsState()

                    // Navigate based on auth state
                    LaunchedEffect(currentUser) {
                        if (currentUser != null) {
                            navController.navigate(Screen.Overview.name) {
                                popUpTo(Screen.Login.name) { inclusive = true }
                            }
                            citiesViewModel.loadCities()
                        } else {
                            navController.navigate(Screen.Login.name) {
                                popUpTo(Screen.Overview.name) { inclusive = true }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.name,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.name) {
                            LoginScreen(
                                onSignIn = { email, password ->
                                    authViewModel.signIn(email, password)
                                },
                                onCreateAccount = { email, password ->
                                    authViewModel.createAccount(email, password)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        composable(Screen.Overview.name) {
                            val cities by citiesViewModel.cities.collectAsState()
                            OverviewScreen(
                                cities = cities,
                                navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        composable(Screen.AddCity.name) {
                            AddCityScreen(
                                navController = navController,
                                onAddCity = { name, longitude, latitude ->
                                    citiesViewModel.addCity(
                                        City(
                                            name,
                                            longitude.toDouble(),
                                            latitude.toDouble()
                                        )
                                    )
                                },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Screen {
    Login,
    Overview,
    AddCity
}