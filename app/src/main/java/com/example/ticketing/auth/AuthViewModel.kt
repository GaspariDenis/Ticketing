package com.example.ticketing.auth

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.vo.User
import com.example.ticketing.vo.UserToken
import com.example.ticketing.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repo : AuthRepository
) : ViewModel() {
    val errorEvent = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)

    var userToken : UserToken = UserToken(null, null, null)

    var isLogged = MutableStateFlow(false)

    fun login(email : String, password : String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.update { true }
            val status = repo.loginAccount(
                User(
                    email = email,
                    password = password,
                    id = null,
                    name = null
                )
            )

            when(status){
                is APIStatus.Success -> {
                    isLogged.update { true }
                    userToken = status.data
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

    fun checkField(email : String, password : String) : Boolean {
        val email = email.trim()

        if(password.chars().count() < 8){
            //errorEvent.update { "La password deve avere minimo 8 caratteri." }
            return false
        }

        if(TextUtils.isEmpty(email)){
            //errorEvent.update { "Inserisci la tua mail." }
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //errorEvent.update { "Inserisci una mail valida." }
            return false
        }

        return true
    }

    fun getUserId() : String{
        return userToken.id ?: throw Exception("Id not found.")
    }

    fun checkLogged() {
        viewModelScope.launch {
            isLogged.update { repo.isLogged() }
        }
    }

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }
}