package it.post.app.ui.common

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.post.app.R

class EmailInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : TextInputLayout(context, attrs) {

    init {
        endIconMode = END_ICON_CLEAR_TEXT
        hint = context.getString(R.string.email)
        isErrorEnabled = true

        addView(
            /**
             * Deliberately using TextInputLayout's `context` to create the view. This will allow
             * TextInputLayout to pass along the appropriate styling to the TextInputEditText.
             */
            TextInputEditText(this.context).apply {
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            },
        )
    }

    inline fun bind(
        value: String,
        crossinline onValueChanged: (email: String, isValid: Boolean) -> Unit,
    ) {
        // Restore previous non-empty value
        if (value.isNotBlank()) {
            editText?.setText(value)
        }

        editText?.doOnTextChanged { text, _, _, _ ->
            val email = "$text"

            error = if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                null
            } else {
                context.getString(R.string.email_validation_error)
            }

            onValueChanged(email, error == null)
        }
    }
}
