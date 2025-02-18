package com.readzzz.viewmodel

// src/main/java/com/yourpackage/viewmodel/AuthViewModel.kt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.readzzz.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    fun login(email: String, password: String) {
        authRepository.login(email, password) { success, message ->
            if (success) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String) {
        authRepository.register(email, password) { success, message ->
            if (success) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(message ?: "Registration failed")
            }
        }
    }

    sealed class AuthState {
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}