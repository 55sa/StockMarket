package com.plcoding.stockmarketapp.presentation.Login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination



    @Destination
    @Composable
    fun LoginAndSignUpScreen(onBack: () -> Unit) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isSigningUp by remember { mutableStateOf(false) }
        val isLoggedIn = remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoggedIn.value) {
                Text(
                    text = if (isSigningUp) "Sign Up" else "Login",
                    style = MaterialTheme.typography.h5,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username.orEmpty(),
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password.orEmpty(),
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isSigningUp) {

                        } else {

                        }
                        isLoggedIn.value = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSigningUp) "Sign Up" else "Login")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login with Google")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        isSigningUp = !isSigningUp
                    }
                ) {
                    Text(
                        if (isSigningUp) "Already have an account? Login" else "Don't have an account? Sign Up"
                    )
                }
            } else {
                Text(
                    text = "Welcome, ${username.orEmpty()}!",
                    style = MaterialTheme.typography.h5,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isLoggedIn.value = false
                        username = ""
                        password = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }

