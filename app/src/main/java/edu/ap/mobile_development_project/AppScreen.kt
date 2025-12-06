package edu.ap.mobile_development_project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.ap.mobile_development_project.domain.City
import edu.ap.mobile_development_project.enums.Category
import edu.ap.mobile_development_project.screens.AddCityScreen
import edu.ap.mobile_development_project.screens.AddPoIScreen
import edu.ap.mobile_development_project.screens.LoginScreen
import edu.ap.mobile_development_project.screens.CityOverviewScreen
import edu.ap.mobile_development_project.domain.PointOfInterest
import edu.ap.mobile_development_project.domain.Rating
import edu.ap.mobile_development_project.screens.PointOfInterestOverview
import edu.ap.mobile_development_project.viewModels.AuthViewModel
import edu.ap.mobile_development_project.viewModels.CitiesViewModel
import edu.ap.mobile_development_project.viewModels.PoIViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class Screen {
    Login,
    Overview,
    AddCity,
    PointOfInterestOverview,
    AddPointOfInterest
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
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
        },
        actions = {
            if (currentScreen != Screen.Login) {
                IconButton(onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            }
        }
    )
}

@Composable
fun App(
    authViewModel: AuthViewModel,
    citiesViewModel: CitiesViewModel,
    poiViewModel: PoIViewModel,
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Login.name
    )

    val currentUser by authViewModel.currentUser.collectAsState()
    val error by authViewModel.error.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    HamburgerMenu(
        drawerState = drawerState,
        onNavigateToScreen = { screen ->
            scope.launch {
                drawerState.close()
                navController.navigate(screen.name) {
                    popUpTo(Screen.Overview.name) { inclusive = true }
                }
            }
        },
        onSignOut = {
            scope.launch {
                drawerState.close()
                authViewModel.signOut()
                navController.navigate(Screen.Login.name) {
                    popUpTo(Screen.Overview.name) { inclusive = true }
                }
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                drawerState = drawerState,
                scope = scope
            )
        } ) { innerPadding ->
            // Navigate based on auth state
            LaunchedEffect(currentUser) {
                if (currentUser != null) {
                    navController.navigate(Screen.Overview.name) {
                        popUpTo(Screen.Login.name) { inclusive = true }
                    }
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
                        error = error,
                        clearError = { authViewModel.clearError() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                composable(Screen.Overview.name) {
                    val cities by citiesViewModel.cities.collectAsState()
                    CityOverviewScreen(
                        cities = cities,
                        navController = navController,
                    )
                }

                composable(Screen.AddCity.name) {
                    AddCityScreen(
                        navController = navController,
                        onAddCity = { name ->
                            citiesViewModel.addCity(
                                City(
                                    name
                                )
                            )
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                }

                composable(Screen.PointOfInterestOverview.name) {
                    PointOfInterestOverview(
                        pointsOfInterest = listOf(
                            PointOfInterest(
                                "afaskhaoifu",
                                "Point of Interest 1",
                                1.0,
                                1.0,
                                "image",
                                listOf(
                                    Category.Cafe,
                                ),
                                listOf(
                                    Rating(
                                        "oufhenef",
                                        5,
                                        "asdfadsfadsf",
                                        "1"
                                    )
                                ),
                                "1"
                            ),PointOfInterest(
                                "i8egweiodvh",
                                "Point of Interest 2",
                                1.0,
                                1.0,
                                "image",
                                listOf(
                                    Category.Cafe,
                                ),
                                listOf(
                                    Rating(
                                        "aieurfnoe",
                                        5,
                                        "asdfadsfadsf",
                                        "1"
                                    )
                                ),
                                "1"
                            ),PointOfInterest(
                                "iuebnlviesufef",
                                "Point of Interest 3",
                                1.0,
                                1.0,
                                "image",
                                listOf(
                                    Category.Cafe,
                                ),
                                listOf(
                                    Rating(
                                        "fiueyfuhlkse",
                                        4,
                                        "ousehfsieulf",
                                        "1"
                                    )
                                ),
                                "1"
                            )
                        ),
                        navController = navController
                    )
                }

                composable(Screen.AddPointOfInterest.name) {
                    AddPoIScreen(
                        navController = navController,
                        onAddPoI = {},
                        categories = listOf(Category.Cafe, Category.Museum),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun HamburgerMenu(
    drawerState: DrawerState,
    onNavigateToScreen: (Screen) -> Unit,
    onSignOut: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Cities") },
                    selected = false,
                    onClick = { onNavigateToScreen(Screen.Overview) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "POI's") },
                    selected = false,
                    onClick = { onNavigateToScreen(Screen.PointOfInterestOverview) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Logout") },
                    selected = false,
                    onClick = { onSignOut() }
                )
            }
        },
        drawerState = drawerState
    ) {
        content()
    }
}
