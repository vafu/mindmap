package v47.mindmap.thought

import v47.mindmap.common.Id

sealed class Thought {

    abstract val id: Id.Known
    abstract val title: String

    data class Text(
        override val id: Id.Known,
        override val title: String,
        val content: String,
    ) : Thought()
}