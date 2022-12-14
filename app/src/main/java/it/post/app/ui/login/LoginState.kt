package it.post.app.ui.login

import it.post.app.ui.common.TransientMessage

data class LoginState(
    val isSubmitting: Boolean = false,
    val email: String = "",
    val isEmailValid: Boolean = false,
    val password: String = "",
    val isPasswordValid: Boolean = false,
    val message: TransientMessage? = null,
) {
    val canLogin: Boolean = !isSubmitting && isEmailValid && isPasswordValid
}
