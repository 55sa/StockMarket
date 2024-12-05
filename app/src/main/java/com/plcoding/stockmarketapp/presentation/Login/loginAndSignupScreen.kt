
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
import com.plcoding.stockmarketapp.presentation.Main_Screen.BottomNavigationBar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun LoginAndSignUpScreen(
    navigator: DestinationsNavigator,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navigator = navigator
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (state.isLoggedIn) "Welcome Back" else if (state.username.isBlank()) "Sign Up" else "Login",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoggedIn) {
                Text(
                    text = "Welcome, ${state.username.ifBlank { "Google User" }}!",
                    style = MaterialTheme.typography.h5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            } else {
                OutlinedTextField(
                    value = state.username,
                    onValueChange = { viewModel.updateState(username = it) },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.updateState(password = it) },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { viewModel.handleSignUp() }, modifier = Modifier.weight(1f)) {
                        Text("Sign Up")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = { viewModel.handleLogin() }, modifier = Modifier.weight(1f)) {
                        Text("Login")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val intentSender = viewModel.googleSignIn()
                            intentSender?.let {
                                googleSignInLauncher.launch(IntentSenderRequest.Builder(it).build())
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In with Google")
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.errorMessage!!, color = MaterialTheme.colors.error)
                }
            }
        }
    }
}