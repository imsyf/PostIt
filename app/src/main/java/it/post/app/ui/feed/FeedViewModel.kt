package it.post.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import it.post.app.R
import it.post.app.data.StoryRepository
import it.post.app.ui.common.Story
import it.post.app.ui.common.TransientMessage.Localized
import it.post.app.ui.common.TransientMessage.Plain
import it.post.app.ui.common.TransientMessage.Type.Error
import it.post.app.ui.common.TransientMessage.Type.Info
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    fun onMessageShown() = _state.update {
        it.copy(message = null)
    }

    fun fetch() {
        viewModelScope.launch {
            when (val result = storyRepository.getStories(state.value.page, state.value.perPage)) {
                is Ok -> _state.update {
                    it.copy(
                        message = Localized(Info, R.string.page_loaded, it.page),
                        stories = it.stories + result.value.map(Story::from),
                        page = it.page + 1,
                    )
                }
                is Err -> _state.update {
                    it.copy(message = Plain(Error, result.error.message), shouldFetch = false)
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(isRefreshing = true, shouldFetch = true, page = 1, stories = emptyList())
            }

            // Artificial delay, just to keep refresh indicator displayed for a bit.
            // New fetch will be triggered by the loading epoxy model.
            delay(200)

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (val result = storyRepository.logout()) {
                is Ok -> Unit
                is Err -> _state.update { it.copy(message = Plain(Error, result.error.message)) }
            }
        }
    }
}
