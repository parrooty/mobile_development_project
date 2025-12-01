package edu.ap.mobile_development_project.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val listeners = mutableListOf<(FirebaseUser?) -> Unit>()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _currentUser.value = user

        listeners.forEach { it(user) }
    }

    init {
        // TODO: if user is already logged in previous sessions, handle accordingly
        signOut()
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener { Log.e("Auth", "Sign in failed", it) }
    }

    fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener { Log.e("Auth", "Create account failed", it) }
    }

    fun signOut() {
        auth.signOut()
    }

    fun addAuthStateCallback(callback: (FirebaseUser?) -> Unit) {
        listeners.add(callback)
    }

    fun removeAuthStateCallback(callback: (FirebaseUser?) -> Unit) {
        listeners.remove(callback)
    }
}