package it.post.app.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(email = value, isEmailValid = isValid)
    }

    fun onPasswordChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(password = value, isPasswordValid = isValid)
    }
}
