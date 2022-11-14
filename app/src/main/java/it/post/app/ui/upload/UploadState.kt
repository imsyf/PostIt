package it.post.app.ui.upload

import android.net.Uri
import androidx.core.net.toFile
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import it.post.app.data.common.GenericError
import it.post.app.ui.common.TransientMessage
import java.io.File

data class UploadState(
    val isBusy: Boolean = false,
    val isSucceed: Boolean = false,
    val imageUri: Uri = Uri.EMPTY,
    val description: String = "",
    val isDescriptionValid: Boolean = false,
    val message: TransientMessage? = null,
) {
    val imageFile: Result<File, GenericError> = runCatching {
        imageUri.toFile()
    }.mapError {
        GenericError.fromThrowable(it)
    }

    val imageSizeBytes: Long = when (imageFile) {
        is Ok -> imageFile.value.length()
        is Err -> 0
    }

    val isImageSelected: Boolean = when (imageFile) {
        is Ok -> true
        is Err -> false
    }

    val canUpload: Boolean = !isBusy && isImageSelected && isDescriptionValid
}
