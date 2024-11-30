package com.plcoding.stockmarketapp.presentation.Login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.plcoding.stockmarketapp.R
import kotlinx.coroutines.tasks.await


class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth // Firebase Authentication instance

    /**
     * Initiates the Google sign-in process and returns an IntentSender
     * to launch the One Tap UI for authentication.
     */
    suspend fun signIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(buildSignInRequest()).await()
            result?.pendingIntent?.intentSender
        } catch (e: Exception) {
            Log.e("GoogleAuthUiClient", "Error during sign-in: ${e.message}", e)
            null
        }
    }

    /**
     * Completes the sign-in process using the result Intent.
     * Authenticates with Firebase using Google credentials.
     */
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        return try {
            // Extract Google credentials from the Intent
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
                ?: throw Exception("Google ID token not found")

            // Authenticate with Firebase using the Google ID token
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val user = auth.signInWithCredential(firebaseCredential).await().user

            SignInResult(
                data = user?.let {
                    UserData(
                        userId = it.uid,
                        username = it.displayName,
                        profilePictureUrl = it.photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            Log.e("GoogleAuthUiClient", "Error during sign-in with intent: ${e.message}", e)
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    /**
     * Signs out the current user from both Firebase and the One Tap client.
     */
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await() // Sign out from the One Tap client
            auth.signOut() // Sign out from Firebase
        } catch (e: Exception) {
            Log.e("GoogleAuthUiClient", "Error during sign-out: ${e.message}", e)
        }
    }

    /**
     * Retrieves the currently signed-in user's information, if available.
     */
    fun getSignedInUser(): UserData? {
        return auth.currentUser?.let {
            UserData(
                userId = it.uid,
                username = it.displayName,
                profilePictureUrl = it.photoUrl?.toString()
            )
        }
    }

    /**
     * Builds a sign-in request for the One Tap client with the necessary configuration.
     */
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true) // Enable Google ID token authentication
                    .setServerClientId(context.getString(R.string.web_client_id)) // Use your web client ID
                    .setFilterByAuthorizedAccounts(false) // Allow any account, not just authorized ones
                    .build()
            )
            .setAutoSelectEnabled(true) // Automatically select accounts if possible
            .build()
    }
}
