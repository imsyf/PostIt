package it.post.app.ui.common

import androidx.annotation.StringRes

sealed interface TransientMessage {

    val type: Type

    data class Plain(
        override val type: Type,
        val text: String,
    ) : TransientMessage

    class Localized<T>(
        override val type: Type,
        @StringRes val stringRes: Int,
        vararg val args: T,
    ) : TransientMessage

    enum class Type {
        Error,
        Info,
        Success,
        Warning,
    }
}
