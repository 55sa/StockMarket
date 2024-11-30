
package com.plcoding.stockmarketapp.presentation.Login

import android.content.Context
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun LoginAndSignUpScreen(
    navigator: DestinationsNavigator,
//    googleAuthUiClient: GoogleAuthUiClient
) {

    val context = LocalContext.current
    val googleAuthUiClient = GoogleAuthUiClient(
        context = LocalContext.current,
        oneTapClient = Identity.getSignInClient(LocalContext.current)
    )
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSigningUp by remember { mutableStateOf(false) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val passwordMinLength = 6

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val googleUser = googleAuthUiClient.getSignedInUser()
        isLoggedIn.value = !savedUsername.isNullOrEmpty() || googleUser != null
    }

    val onSignInResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        coroutineScope.launch {
            val intent = result.data ?: return@launch
            val signInResult = googleAuthUiClient.signInWithIntent(intent)
            if (signInResult.data != null) {
                isLoggedIn.value = true
                snackbarHostState.showSnackbar("Welcome, ${signInResult.data.username}")
            } else {
                snackbarHostState.showSnackbar("Google Sign-In failed: ${signInResult.errorMessage}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navigator.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (isSigningUp) "Sign Up" else "Login",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLoggedIn.value) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isSigningUp) {
                        // Sign-Up logic
                        if (password.length >= passwordMinLength) {
                            val editor = sharedPreferences.edit()
                            editor.putString("username", username)
                            editor.putString("password", password)
                            editor.apply()

                            isSigningUp = false
                            username = ""
                            password = ""
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Signup successful. Please log in.")
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Password must be at least $passwordMinLength characters.")
                            }
                        }
                    } else {
                        // Login logic
                        val savedUsername = sharedPreferences.getString("username", null)
                        val savedPassword = sharedPreferences.getString("password", null)

                        if (savedUsername.isNullOrEmpty() || savedPassword.isNullOrEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("You must sign up before logging in.")
                            }
                        } else if (username == savedUsername && password == savedPassword) {
                            isLoggedIn.value = true
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Login successful.")
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Invalid credentials. Please try again.")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSigningUp) "Sign Up" else "Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val intentSender = googleAuthUiClient.signIn()
                        if (intentSender != null) {
                            onSignInResult.launch(
                                IntentSenderRequest.Builder(intentSender).build()
                            )
                        } else {
                            snackbarHostState.showSnackbar("Google Sign-In failed.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In with Google")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { isSigningUp = !isSigningUp }
            ) {
                Text(
                    if (isSigningUp) "Already have an account? Login" else "Don't have an account? Sign Up"
                )
            }
        } else {
            val googleUser = googleAuthUiClient.getSignedInUser()
            Text(
                text = "Welcome, ${googleUser?.username ?: sharedPreferences.getString("username", "User")!!}!",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        googleAuthUiClient.signOut()
                        isLoggedIn.value = false
                        username = ""
                        password = ""
                        snackbarHostState.showSnackbar("Logged out successfully.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(hostState = snackbarHostState)
    }
}