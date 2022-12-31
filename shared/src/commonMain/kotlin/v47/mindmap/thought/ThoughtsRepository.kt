package v47.mindmap.thought

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import v47.mindmap.FIXED_THOUGHTS
import v47.mindmap.common.Id
import v47.mindmap.connections.Connection

typealias Thoughts = Map<Id.Known, Thought>

interface ThoughtsRepository {

    fun query(ids: Set<Id.Known>): Flow<Result<Thoughts>>
}

fun ThoughtsRepository.query(id: Id.Known): Flow<Result<Thought>> =
    query(setOf(id)).map { it.map { it.values.first() } }

/**
 * tmp
 */
object StaticThoughtRepository : ThoughtsRepository {

    override fun query(ids: Set<Id.Known>): Flow<Result<Thoughts>> =
        flowOf(
            Result.success(
                ids.mapNotNull { id ->
                    FIXED_THOUGHTS[id]?.let { id to it }
                }.toMap()
            )
        )

}
