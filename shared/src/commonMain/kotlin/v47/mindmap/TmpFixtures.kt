package v47.mindmap

import v47.mindmap.common.Id
import v47.mindmap.common.id
import v47.mindmap.connections.Connection
import v47.mindmap.thought.Thought

internal val FIXED_THOUGHTS =
    mapOf(
        fixedThough("entry"),
        fixedThough("A"),
        fixedThough("B"),
        fixedThough("AA"),
        fixedThough("AB"),
        fixedThough("BA"),
        fixedThough("BB"),
        fixedThough("BAB"),
        fixedThough("BAA"),
    )

val FIXED_MAPPING = hashMapOf<Id.Known, Set<Id.Known>>(
    "entry".id to setOf("A".id, "B".id),
    "A".id to setOf("AA".id, "AB".id),
    "B".id to setOf("BA".id, "BB".id),
    "BA".id to setOf("BAA".id, "BAB".id),
)

internal fun fixedThough(title: String): Pair<Id.Known, Thought> =
    title.id.let { it to Thought(it, title) }
