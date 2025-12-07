package edu.ap.mobile_development_project.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseDatabase.getInstance().reference
    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val listeners = mutableListOf<(FirebaseUser?) -> Unit>()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _currentUser.value = user

        listeners.forEach { it(user) }
    }

    init {
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
            .addOnSuccessListener { authResult ->
                // This block executes ONLY on successful account creation
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = mapOf(
                        "uid" to firebaseUser.uid,
                        "email" to firebaseUser.email
                    )
                    // Now, save the user info to the Realtime Database
                    db.child("users").child(firebaseUser.uid).setValue(user)
                        .addOnSuccessListener {
                            // This is the true success point
                            Log.d("AuthViewModel", "Create account and save user successful.")
                        }
                        .addOnFailureListener { e ->
                            // Handle the edge case where Auth user was created but DB save failed
                            Log.e("AuthViewModel", "User created but failed to save to DB.", e)
                            _error.value = "Failed to save user profile."
                        }
                } else {
                    // This is a rare case, but good to handle
                    Log.e("AuthViewModel", "User created but firebaseUser is null.")
                    _error.value = "An unknown error occurred during sign up."
                }
            }
            .addOnFailureListener { e ->
                // This block executes ONLY if createUserWithEmailAndPassword fails
                Log.e("AuthViewModel", "Create account failed", e)
                _error.value = e.localizedMessage ?: "Sign up failed. The email might be invalid or already in use."
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

    fun getUserEmailById(userId: String, onResult: (String?) -> Unit) {
        db.child("users").child(userId).child("email")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // When data is retrieved, call the onResult lambda with the email
                    onResult(snapshot.getValue(String::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    // If there's an error, return null
                    Log.e("AuthViewModel", "Failed to get user email by ID", error.toException())
                    onResult(null)
                }
            })
    }
}