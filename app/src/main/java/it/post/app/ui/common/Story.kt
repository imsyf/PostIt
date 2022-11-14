package it.post.app.ui.common

import android.text.SpannedString
import android.text.format.DateUtils
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import it.post.app.data.remote.response.StoryDto
import java.util.Calendar

data class Story(
    val id: String,
    val name: String,
    val photoUrl: String,
    val caption: SpannedString,
    val timeAgo: CharSequence,
) {
    companion object {
        fun from(dto: StoryDto): Story = Story(
            id = dto.id,
            name = dto.name,
            photoUrl = dto.photoUrl,
            caption = buildSpannedString {
                bold { append(dto.name) }
                append(" ")
                append(dto.description)
            },
            timeAgo = DateUtils.getRelativeTimeSpanString(
                dto.createdAt.time,
                Calendar.getInstance().timeInMillis,
                DateUtils.MINUTE_IN_MILLIS,
            ),
        )
    }
}
