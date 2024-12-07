package com.plcoding.stockmarketapp.presentation.Login

import android.content.Context
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        loadUserState()
    }

    private fun loadUserState() {
        val savedUsername = sharedPreferences.getString("username", null)
        val savedUserId = sharedPreferences.getString("userId", null)
        val googleUser = googleAuthUiClient.getSignedInUser()
        _state.value = AuthState(
            isLoggedIn = !savedUsername.isNullOrEmpty() || googleUser != null,
            username = savedUsername.orEmpty(),
            userId = savedUserId.orEmpty()
        )
    }

    fun updateState(username: String? = null, password: String? = null) {
        _state.value = _state.value.copy(
            username = username ?: _state.value.username,
            password = password ?: _state.value.password
        )
    }

    fun handleSignUp() {
        val username = _state.value.username
        val password = _state.value.password
        if (password.length < 6) {
            _state.value = _state.value.copy(errorMessage = "Password must be at least 6 characters.")
            return
        }
        sharedPreferences.edit().apply {
            putString("username", username)
            putString("password", password)
            apply()
        }
        _state.value = _state.value.copy(username = "", password = "", errorMessage = null)
    }

    fun handleLogin() {
        val username = _state.value.username
        val password = _state.value.password
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)

        if (username == savedUsername && password == savedPassword) {
            sharedPreferences.edit().putString("userId", "local_user").apply() // Add a userId for local login
            _state.value = _state.value.copy(isLoggedIn = true, errorMessage = null, userId = "local_user")
        } else {
            _state.value = _state.value.copy(errorMessage = "Invalid credentials. Please try again.")
        }
    }

    suspend fun googleSignIn(): IntentSender? {
        return try {
            googleAuthUiClient.signIn()
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = "Google Sign-In failed: ${e.message}")
            null
        }
    }

    fun handleGoogleSignInResult(intentData: android.content.Intent?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val signInResult = intentData?.let { googleAuthUiClient.signInWithIntent(it) }

            if (signInResult != null && signInResult.data != null) {
                val userData = signInResult.data

                // Save username and userId in SharedPreferences
                sharedPreferences.edit().apply {
                    putString("username", userData.username)
                    putString("userId", userData.userId)
                    apply()
                }

                _state.value = _state.value.copy(
                    isLoggedIn = true,
                    userId = userData.userId,
                    username = userData.username.orEmpty(),
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = signInResult?.errorMessage
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Clear user data from SharedPreferences
            sharedPreferences.edit().clear().apply()

            // Sign out from Google and reset the state
            googleAuthUiClient.signOut()

            // Update the state to reset all values
            _state.value = AuthState()
        }
    }
}