package it.post.app.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import it.post.app.data.StoryRepository
import it.post.app.ui.common.Story
import it.post.app.ui.common.TransientMessage.Plain
import it.post.app.ui.common.TransientMessage.Type.Error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    private val storyId: String,
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PostState())
    val state: StateFlow<PostState> = _state.asStateFlow()

    init {
        fetch()
    }

    fun onMessageShown() = _state.update {
        it.copy(message = null)
    }

    fun fetch() {
        viewModelScope.launch {
            _state.update { it.copy(isFetching = true) }

            when (val result = storyRepository.getStory(storyId)) {
                is Ok -> _state.update {
                    it.copy(story = result.value.let(Story::from), isFailed = false)
                }
                is Err -> _state.update {
                    it.copy(
                        story = null,
                        message = Plain(Error, result.error.message),
                        isFailed = true,
                    )
                }
            }

            _state.update { it.copy(isFetching = false) }
        }
    }
}
