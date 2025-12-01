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
import edu.ap.mobile_development_project.viewModels.CitiesViewModel

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private val citiesViewModel: CitiesViewModel by viewModels()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // User is authenticated AND the token is valid
                citiesViewModel.loadCities()
            }
        }

        enableEdgeToEdge()
        setContent {
            Mobile_development_projectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.name,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.name) {
                            LoginScreen(
                                onSignIn = { email, password ->
                                    signIn(email, password, navController)
                                },
                                onCreateAccount = { email, password ->
                                    createAccount(email, password)
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

    public override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    public override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String, navController: NavHostController) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    navController.navigate(Screen.Overview.name)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}

enum class Screen {
    Login,
    Overview,
    AddCity
}