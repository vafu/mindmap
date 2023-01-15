package v47.mindmap.connections

import v47.mindmap.FIXED_MAPPING
import v47.mindmap.common.Id
import v47.mindmap.common.id

interface ConnectionsRepository {

    suspend fun query(criteria: Criteria): Result<Connection>

    suspend fun connect(from: Id.Known, to: Id.Known): Boolean

    sealed class Criteria {

        object Root : Criteria()
        data class ById(val id: Id.Known) : Criteria()
    }
}

suspend fun ConnectionsRepository.query(id: Id.Known) =
    query(ConnectionsRepository.Criteria.ById(id))

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

    private val connections = FIXED_MAPPING.toMutableMap()

    override suspend fun query(criteria: ConnectionsRepository.Criteria): Result<Connection> =
        when (criteria) {
            ConnectionsRepository.Criteria.Root ->
                Result.success(createConnection("entry".id))
            is ConnectionsRepository.Criteria.ById -> {
                val result = createConnection(criteria.id)
                result.let { Result.success(it) }
            }
        }

    override suspend fun connect(from: Id.Known, to: Id.Known): Boolean {
        connections.getOrPut(from, ::mutableSetOf).toMutableSet()
            .add(to)
        return true
    }

    private fun createConnection(id: Id.Known): Connection {
        // ineffective as FUCK
        val parent = connections.entries.find { (_, v) -> v.any { it == id } }
        return if (parent != null) {
            Connection.Interim(
                id,
                { createConnection(parent.key) },
                {
                    connections[id]?.map {
                        createConnection(it)
                    } ?: emptyList()
                }
            )
        } else {
            Connection.Root(id) {
                connections[id]?.map {
                    createConnection(it)
                } ?: emptyList()
            }
        }
    }
}