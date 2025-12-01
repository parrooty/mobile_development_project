package edu.ap.mobile_development_project.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val listeners = mutableListOf<(FirebaseUser?) -> Unit>()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _currentUser.value = user

        listeners.forEach { it(user) }
    }

    init {
        // TODO: if user is already logged in previous sessions, handle accordingly
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Log.e("Auth", "Email and password cannot be blank")
            _error.value = "Email and password cannot be blank"
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                Log.e("Auth", "Sign in failed", it)
                _error.value = "Sign in failed: Email or password is incorrect"
            }
    }

    fun createAccount(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Log.e("Auth", "Email and password cannot be blank")
            _error.value = "Email and password cannot be blank"
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener {
                Log.e("Auth", "Create account failed", it)
                _error.value = "Sign in failed: Invalid email or password"
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun clearError() {
        _error.value = null
    }

    fun addAuthStateCallback(callback: (FirebaseUser?) -> Unit) {
        listeners.add(callback)
    }

    fun removeAuthStateCallback(callback: (FirebaseUser?) -> Unit) {
        listeners.remove(callback)
    }
}