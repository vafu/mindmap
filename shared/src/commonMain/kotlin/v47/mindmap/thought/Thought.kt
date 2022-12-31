package v47.mindmap.thought

import v47.mindmap.common.Id

// should it be typed?
data class Thought(
    val id: Id.Known,
    val title: String,
)