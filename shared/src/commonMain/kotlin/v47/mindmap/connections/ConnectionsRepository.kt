package v47.mindmap.connections

import v47.mindmap.FIXED_MAPPING
import v47.mindmap.common.Id
import v47.mindmap.common.id

interface ConnectionsRepository {

    suspend fun query(criteria: Criteria): Result<Connection>

    sealed class Criteria {

        object Root : Criteria()
        data class ById(val id: Id.Known) : Criteria()
    }
}

sealed class Connection {

    abstract val id: Id.Known
    abstract val children: suspend () -> List<Connection>

    data class Root(
        override val id: Id.Known,
        override val children: suspend () -> List<Connection>
    ) : Connection()

    data class Interim(
        override val id: Id.Known,
        val parent: suspend () -> Connection,
        override val children: suspend () -> List<Connection>
    ) : Connection()
}

object StaticConnectionsRepository : ConnectionsRepository {

    override suspend fun query(criteria: ConnectionsRepository.Criteria): Result<Connection> =
        when (criteria) {
            ConnectionsRepository.Criteria.Root ->
                Result.success(createConnection("entry".id))
            is ConnectionsRepository.Criteria.ById -> {
                val result = createConnection(criteria.id)
                result.let { Result.success(it) }
            }
        }

    private fun createConnection(id: Id.Known): Connection {
        // ineffective as FUCK
        val parent = FIXED_MAPPING.entries.find { (_, v) -> v.any { it == id } }
        return if (parent != null) {
            Connection.Interim(
                id,
                { createConnection(parent.key) },
                {
                    FIXED_MAPPING[id]?.map {
                        createConnection(it)
                    } ?: emptyList()
                }
            )
        } else {
            Connection.Root(id) {
                FIXED_MAPPING[id]?.map {
                    createConnection(it)
                } ?: emptyList()
            }
        }
    }
}