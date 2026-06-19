package com.example.ticketing.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.network.User
import com.example.ticketing.network.UserToken
import com.example.ticketing.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repo : AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    val errorEvent = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)

    private var _userToken : UserToken = UserToken(null, null)

    fun setEmail(email: String) {
        _email.update { email }
    }

    fun setPassword(password : String) {
        _password.update { password }
    }

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.update { true }
            val status = repo.loginAccount(
                User(
                    email = _email.value,
                    password = _password.value,
                    id = null
                )
            )

            when(status){
                is APIStatus.Success -> {
                    _userToken = status.data
                    isLoading.update { false }
                }
                is APIStatus.ErrorAPI -> {
                    errorEvent.update { status.errorMessage() }
                    isLoading.update { false }
                }
                is APIStatus.Error -> {
                    errorEvent.update { status.e.message ?: "Unexpected error." }
                    isLoading.update { false }
                }
                is APIStatus.Loading -> {}
            }
        }
    }
}