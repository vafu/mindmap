package v47.mindmap.connections

import v47.mindmap.FIXED_MAPPING
import v47.mindmap.common.Id
import v47.mindmap.common.id
import v47.mindmap.common.log

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

    private val connections =
        FIXED_MAPPING.mapValues { it.value.toMutableSet() }.toMutableMap()

    override suspend fun query(criteria: ConnectionsRepository.Criteria): Result<Connection> =
        when (criteria) {
            ConnectionsRepository.Criteria.Root ->
                Result.success(createConnection("entry".id))
            is ConnectionsRepository.Criteria.ById -> {
                log { "querying $criteria" }
                val result = createConnection(criteria.id)
                result.let { Result.success(it) }
            }
        }

    override suspend fun connect(from: Id.Known, to: Id.Known): Boolean {
        log { "connecting $from to $to" }
        connections.getOrPut(from, ::mutableSetOf)
            .log { "$it for $from" }
            .also { it.add(to) }
            .log { "$it for $from" }
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