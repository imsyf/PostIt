package it.post.app.ui.register

import it.post.app.ui.common.TransientMessage

data class RegisterState(
    val isSubmitting: Boolean = false,
    val name: String = "",
    val isNameValid: Boolean = false,
    val email: String = "",
    val isEmailValid: Boolean = false,
    val password: String = "",
    val isPasswordValid: Boolean = false,
    val message: TransientMessage? = null,
) {
    val canRegister: Boolean = !isSubmitting && isNameValid && isEmailValid && isPasswordValid
}
