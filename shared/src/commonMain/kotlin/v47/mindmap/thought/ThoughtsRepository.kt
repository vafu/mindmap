package v47.mindmap.thought

import v47.mindmap.FIXED_THOUGHTS
import v47.mindmap.common.Id

typealias Thoughts = Map<Id.Known, Thought>

interface ThoughtsRepository {

    suspend fun query(ids: Set<Id.Known>): Result<Thoughts>
}

suspend fun ThoughtsRepository.query(id: Id.Known): Result<Thought> =
    query(setOf(id)).map { it.values.first() }

/**
 * tmp
 */
object StaticThoughtRepository : ThoughtsRepository {

    override suspend fun query(ids: Set<Id.Known>): Result<Thoughts> =
        Result.success(
            ids.mapNotNull { id ->
                FIXED_THOUGHTS[id]?.let { id to it }
            }.toMap()
        )
}
