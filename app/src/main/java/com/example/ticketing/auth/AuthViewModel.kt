package com.example.ticketing.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.vo.User
import com.example.ticketing.vo.UserToken
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

    private var _email = mutableStateOf("")
    private var _password = mutableStateOf("")

    val errorEvent = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)

    val loginSuccess = MutableStateFlow(false)

    private var _userToken : UserToken = UserToken(null, null)

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password : String) {
        _password.value = password
    }

    fun getEmail() : String{
        return _email.value
    }

    fun getPassword() : String{
        return _password.value
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
                    loginSuccess.update { true }
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

    fun checkField() : Boolean {
        return _password.value.chars().count() > 8
    }

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }
}