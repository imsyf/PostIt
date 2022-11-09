package it.post.app.ui.login

data class LoginState(
    val email: String = "",
    val isEmailValid: Boolean = false,
    val password: String = "",
    val isPasswordValid: Boolean = false,
) {
    val canLogin: Boolean = isEmailValid && isPasswordValid
}
