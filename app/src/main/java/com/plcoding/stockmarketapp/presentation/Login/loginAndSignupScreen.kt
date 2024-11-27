
package com.plcoding.stockmarketapp.presentation.Login

import android.provider.Settings.Global.putString
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import android.content.Context
import android.content.SharedPreferences

import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Destination
@Composable
fun LoginAndSignUpScreen(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSigningUp by remember { mutableStateOf(false) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val passwordMinLength = 6

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        username = ""
        password = ""
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
                        if (password.length >= passwordMinLength) {
                            // Save credentials to SharedPreferences
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
                        val savedUsername = sharedPreferences.getString("username", "")
                        val savedPassword = sharedPreferences.getString("password", "")
                        if (username == savedUsername && password == savedPassword) {
                            isLoggedIn.value = true
                            username = ""
                            password = ""
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
                        snackbarHostState.showSnackbar("Google sign-in clicked. Implement the logic.")
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
            Text(
                text = "Welcome, ${sharedPreferences.getString("username", "User")!!}!",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoggedIn.value = false
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




