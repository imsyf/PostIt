package it.post.app.ui.common

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty.error
import es.dmoral.toasty.Toasty.info
import es.dmoral.toasty.Toasty.success
import es.dmoral.toasty.Toasty.warning
import it.post.app.ui.common.TransientMessage.Localized
import it.post.app.ui.common.TransientMessage.Plain
import it.post.app.ui.common.TransientMessage.Type.Error
import it.post.app.ui.common.TransientMessage.Type.Info
import it.post.app.ui.common.TransientMessage.Type.Success
import it.post.app.ui.common.TransientMessage.Type.Warning

fun Fragment.showToasty(
    transient: TransientMessage,
    onShown: (() -> Unit)? = null,
) {
    val message = when (transient) {
        is Localized<*> -> getString(transient.stringRes, *transient.args)
        is Plain -> transient.text
    }

    val toast: (Context, CharSequence, Int, Boolean) -> Toast = when (transient.type) {
        Error -> ::error
        Info -> ::info
        Success -> ::success
        Warning -> ::warning
    }

    toast(requireContext(), message, Toast.LENGTH_LONG, true).show()
    onShown?.invoke()
}
