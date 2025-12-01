package edu.ap.mobile_development_project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.ap.mobile_development_project.domain.City
import edu.ap.mobile_development_project.screens.AddCityScreen
import edu.ap.mobile_development_project.screens.LoginScreen
import edu.ap.mobile_development_project.screens.OverviewScreen
import edu.ap.mobile_development_project.viewModels.AuthViewModel
import edu.ap.mobile_development_project.viewModels.CitiesViewModel

enum class Screen {
    Login,
    Overview,
    AddCity
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen.name) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            }
        }
    )
}

@Composable
fun App(
    authViewModel: AuthViewModel,
    citiesViewModel: CitiesViewModel,
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Login.name
    )

    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        AppBar(
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() }

        )
    } ) { innerPadding ->

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