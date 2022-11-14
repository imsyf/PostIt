package it.post.app.ui.post

import it.post.app.ui.common.Story
import it.post.app.ui.common.TransientMessage

data class PostState(
    val isFetching: Boolean = false,
    val isFailed: Boolean = false,
    val story: Story? = null,
    val message: TransientMessage? = null,
)
