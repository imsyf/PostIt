package it.post.app.ui.upload

import android.net.Uri
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
import it.post.app.ui.common.TransientMessage.Type.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UploadViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UploadState())
    val state: StateFlow<UploadState> = _state.asStateFlow()

    fun onImageChanged(value: Uri) = _state.update {
        it.copy(imageUri = value)
    }

    fun onDescriptionChanged(value: String, isValid: Boolean) = _state.update {
        it.copy(description = value, isDescriptionValid = isValid)
    }

    fun onMessageShown() = _state.update {
        it.copy(message = null)
    }

    fun setBusy(isBusy: Boolean) = _state.update {
        it.copy(isBusy = isBusy)
    }

    fun upload() {
        viewModelScope.launch {
            setBusy(true)

            when (
                val result = state.value.imageFile.andThen { file ->
                    storyRepository.upload(state.value.description, file)
                }
            ) {
                is Ok -> _state.update {
                    it.copy(
                        message = Localized<Nothing>(Success, R.string.image_uploaded),
                        isSucceed = true,
                    )
                }
                is Err -> _state.update {
                    it.copy(
                        message = Plain(Error, result.error.message),
                        isSucceed = false,
                    )
                }
            }

            setBusy(false)
        }
    }
}
