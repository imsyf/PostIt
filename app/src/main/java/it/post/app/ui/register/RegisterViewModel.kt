package it.post.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import it.post.app.R
import it.post.app.data.StoryRepository
import it.post.app.ui.common.TransientMessage.Localized
import it.post.app.ui.common.TransientMessage.Plain
import it.post.app.ui.common.TransientMessage.Type.Error
import it.post.app.ui.common.TransientMessage.Type.Info
import it.post.app.ui.common.TransientMessage.Type.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onNameChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(name = value, isNameValid = isValid)
    }

    fun onEmailChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(email = value, isEmailValid = isValid)
    }

    fun onPasswordChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(password = value, isPasswordValid = isValid)
    }

    fun onMessageShown() = _state.update {
        it.copy(message = null)
    }

    fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }

            when (
                val result = storyRepository
                    .register(state.value.name, state.value.email, state.value.password)
                    .also { registration ->
                        if (registration is Ok) {
                            _state.update {
                                it.copy(message = Localized<Nothing>(Info, R.string.logging_you_in))
                            }
                        }
                    }
                    .andThen { storyRepository.login(state.value.email, state.value.password) }
            ) {
                is Ok -> _state.update {
                    it.copy(message = Localized(Success, R.string.register_welcome, result.value))
                }
                is Err -> _state.update { it.copy(message = Plain(Error, result.error.message)) }
            }

            _state.update { it.copy(isSubmitting = false) }
        }
    }
}
