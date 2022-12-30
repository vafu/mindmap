package v47.mindmap.mind

import kotlinx.coroutines.flow.*
import v47.mindmap.common.Id
import v47.mindmap.common.id

data class Mind(
    val thoughts: Set<Id.Known>,
    val connections: ThoughtConnections
)

// should it be typed?
data class Thought(
    val id: Id.Known,
    val title: String,
)

typealias ThoughtConnections = Map<Id.Known, Set<Id.Known>>

// repository?
interface MindRepository {
    val mind: StateFlow<Mind>
}

interface ThoughtRepository {

    fun query(vararg ids: Id.Known): Flow<Map<Id.Known, Thought>>
}

// region tmp
object StaticThoughtRepository : ThoughtRepository {

    override fun query(vararg ids: Id.Known): Flow<Map<Id.Known, Thought>> =
        flowOf(
            ids.mapNotNull { id ->
                FIXED_THOUGHTS[id]?.let { id to it }
            }.toMap()
        )
}

// do I need this? maybe separate repo for thoughts and separate for connections for dyn querying?
object StaticMindRepository : MindRepository {

    private val _mind = MutableStateFlow(FIXED_MIND)
    override val mind: StateFlow<Mind> = _mind.asStateFlow()
}


private val FIXED_THOUGHTS =
    mapOf(
        fixedThough("entry"),
        fixedThough("A"),
        fixedThough("B"),
        fixedThough("AA"),
        fixedThough("AB"),
        fixedThough("BA"),
        fixedThough("BB"),
    )

private val FIXED_CONNECTIONS: ThoughtConnections = mapOf(
    "entry".id to setOf("A".id, "B".id),
    "A".id to setOf("AA".id, "AB".id),
    "B".id to setOf("BA".id, "BB".id)
)

private val FIXED_MIND = Mind(
    FIXED_THOUGHTS.keys,
    FIXED_CONNECTIONS
)

private fun fixedThough(title: String): Pair<Id.Known, Thought> =
    title.id.let { it to Thought(it, title) }
// endregion