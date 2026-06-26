package com.example.ticketing.register

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ticketing.network.APIStatus
import com.example.ticketing.repository.AuthRepository
import com.example.ticketing.vo.RegisterUser
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    val repo : AuthRepository
) : ViewModel() {
    private val tag = "RegisterViewModel"

    val errorEvent = MutableStateFlow("")

    val created = MutableStateFlow(false)

    fun resetErrorEvent() {
        errorEvent.update { "" }
    }

    fun registerUser(user: String, email : String, password : String){
        viewModelScope.launch(Dispatchers.IO) {
            val status = repo.registerAccount(RegisterUser(
                name = user,
                email = email,
                password = password
            ))

            when(status){
                is APIStatus.Loading -> {}
                is APIStatus.Error -> { errorEvent.update { status.e.message ?: "Unexpected error" } }
                is APIStatus.ErrorAPI -> { errorEvent.update { status.errorMessage() } }
                is APIStatus.Success -> { created.update { true } }
            }
        }
    }

    fun checkField(user : String, email : String, password : String) : Boolean {
        val email = email.trim()

        if(user.isEmpty()){
            return false
        }

        if(password.chars().count() < 8){
            return false
        }

        if(TextUtils.isEmpty(email)){
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }

        return true
    }
}