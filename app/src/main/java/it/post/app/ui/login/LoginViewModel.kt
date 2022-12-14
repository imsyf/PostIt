package it.post.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import it.post.app.R
import it.post.app.data.StoryRepository
import it.post.app.ui.common.TransientMessage.Localized
import it.post.app.ui.common.TransientMessage.Plain
import it.post.app.ui.common.TransientMessage.Type.Error
import it.post.app.ui.common.TransientMessage.Type.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(email = value, isEmailValid = isValid)
    }

    fun onPasswordChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(password = value, isPasswordValid = isValid)
    }

    fun onMessageShown() = _state.update {
        it.copy(message = null)
    }

    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }

            when (val result = storyRepository.login(state.value.email, state.value.password)) {
                is Ok -> _state.update {
                    it.copy(message = Localized(Success, R.string.login_welcome, result.value))
                }
                is Err -> _state.update { it.copy(message = Plain(Error, result.error.message)) }
            }

            _state.update { it.copy(isSubmitting = false) }
        }
    }
}
