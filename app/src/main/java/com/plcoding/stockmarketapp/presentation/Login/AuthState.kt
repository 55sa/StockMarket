package com.plcoding.stockmarketapp.presentation.Login

data class AuthState(
    val username: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userId: String? = null
)
