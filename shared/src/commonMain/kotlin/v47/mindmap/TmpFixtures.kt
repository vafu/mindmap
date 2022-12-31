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
    )

internal val FIXED_CONNECTIONS: Connection =
    Connection.Interim(
        "entry".id,
        listOf(
            Connection.Interim(
                "A".id,
                listOf(
                    Connection.Terminal("AA".id),
                    Connection.Terminal("AB".id),
                )
            ),
            Connection.Interim(
                "B".id,
                listOf(
                    Connection.Terminal("BA".id),
                    Connection.Terminal("BB".id),
                )
            ),
        )
    )

internal fun fixedThough(title: String): Pair<Id.Known, Thought> =
    title.id.let { it to Thought(it, title) }
