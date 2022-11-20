package it.post.app.ui.common

import android.content.Context
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.post.app.R

class PasswordInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : TextInputLayout(context, attrs) {

    init {
        endIconMode = END_ICON_PASSWORD_TOGGLE
        hint = context.getString(R.string.password)
        isErrorEnabled = true

        addView(
            /**
             * Deliberately using TextInputLayout's `context` to create the view. This will allow
             * TextInputLayout to pass along the appropriate styling to the TextInputEditText.
             */
            TextInputEditText(this.context).apply {
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                transformationMethod = PasswordTransformationMethod.getInstance()
            },
        )
    }

    inline fun bind(
        value: String,
        crossinline onValueChanged: (password: String, isValid: Boolean) -> Unit,
    ) {
        // Restore previous non-empty value
        if (value.isNotBlank()) {
            editText?.setText(value)
        }

        editText?.doOnTextChanged { text, _, _, _ ->
            val password = "$text"

            error = if (password.trim().length >= 6) {
                null
            } else {
                context.getString(R.string.password_validation_error)
            }

            onValueChanged(password, error == null)
        }
    }
}
