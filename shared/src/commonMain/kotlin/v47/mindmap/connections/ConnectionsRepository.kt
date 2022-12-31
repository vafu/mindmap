package v47.mindmap.connections

import kotlinx.coroutines.flow.*
import v47.mindmap.FIXED_CONNECTIONS
import v47.mindmap.common.Id
import v47.mindmap.common.log

interface ConnectionsRepository {

    fun query(id: Id.Known, depth: Int = 1): Flow<Result<Connection>>
}

sealed class Connection {

    abstract val id: Id.Known

    data class Terminal(override val id: Id.Known) : Connection()
    data class Interim(
        override val id: Id.Known,
        val children: List<Connection>,
    ) : Connection()
}

object StaticConnectionsRepository : ConnectionsRepository {

    override fun query(id: Id.Known, depth: Int): Flow<Result<Connection>> =
        log("asdf", "searching for : $id").let {
            flowOf(
                Result.success(
                    FIXED_CONNECTIONS.find(id)
                        ?: throw IllegalStateException("didn't find connection for $id")
                )
            )
        }

    private fun Connection.find(id: Id.Known): Connection? {
        log("asdf", "in: $id, $this")
        return when {
            this.id == id -> this
            this is Connection.Interim -> {
                children.firstNotNullOfOrNull { it.find(id) }
            }
            else -> null
        }
    }
}