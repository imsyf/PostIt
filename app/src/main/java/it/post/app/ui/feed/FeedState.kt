package it.post.app.ui.feed

import it.post.app.ui.common.Story
import it.post.app.ui.common.TransientMessage

data class FeedState(
    val isRefreshing: Boolean = false,
    val shouldFetch: Boolean = true,
    val page: Int = 1,
    val perPage: Int = 10,
    val stories: List<Story> = emptyList(),
    val message: TransientMessage? = null,
)
