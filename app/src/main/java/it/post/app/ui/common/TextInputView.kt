package it.post.app.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout

class TextInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : TextInputLayout(context, attrs) {

    init {
        isErrorEnabled = true
    }

    inline fun bind(
        value: String,
        crossinline onValueChanged: (value: String, isValid: Boolean) -> Unit,
        crossinline validation: (value: String) -> Boolean,
        @StringRes errorMessage: Int,
    ) {
        // Restore previous non-empty value
        if (value.isNotBlank()) {
            editText?.setText(value)
        }

        editText?.doOnTextChanged { charSequence, _, _, _ ->
            val text = "$charSequence"

            error = if (validation(text)) {
                null
            } else {
                context.getString(errorMessage)
            }

            onValueChanged(text, error == null)
        }
    }
}
