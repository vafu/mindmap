package v47.mindmap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import v47.mindmap.common.Id
import v47.mindmap.common.id
import v47.mindmap.common.log
import v47.mindmap.connections.Connection
import v47.mindmap.connections.ConnectionsRepository
import v47.mindmap.thought.Thought
import v47.mindmap.thought.ThoughtsRepository
import v47.mindmap.thought.query

interface ThoughtView {

    val events: Flow<Event>

    fun accept(model: Model)

    sealed class Event {

        data class Select(
            // shouldn't be here, go sleep
            val parent: Id.Known,
            val id: Id.Known,
        ) : Event()
    }

    sealed class Model {

        data class Default(
            val thought: Thought
        ) : Model()

        object Empty : Model()

        data class Thought(
            val title: String,
            // shouldn't be here, go sleep
            val parent: Id.Known,
            val next: Id,
            val previous: Id,
            val child: Id,
        )
    }

}

class ThoughtPresenter(
    private val connectionsRepository: ConnectionsRepository,
    private val thoughtRepository: ThoughtsRepository,
    private val mindView: ThoughtView,
) {

    suspend fun start(): Unit =
        merge(
            connectionsRepository.query(ENTRY_ID).map {
                val entry = it.getOrThrow()
                when (entry) {
                    is Connection.Interim ->
                        entry.id to entry.children.first().id
                    is Connection.Terminal ->
                        throw IllegalStateException("terminal entry, how?")
                }
            },
            mindView.events.filterIsInstance<ThoughtView.Event.Select>().map {
                log("asdf", "event: $it")
                it.parent to it.id
            }
        )
            .flatMapLatest { (parent, current) ->
                log("asdf", "current to query: $current")
                parentAndChildConnection(parent, current)
            }
            .flatMapLatest { (parent, current) ->
                log("asdf", "current: $current")
                thoughtRepository.query(current.id).map { thought ->
                    generateModel(parent, thought.getOrThrow(), current)
                }
            }
            .collect(mindView::accept)

    private fun generateModel(
        parent: Connection.Interim,
        thought: Thought,
        currentConnection: Connection,
    ): ThoughtView.Model {
        // ineffective crap
        log("generateModel", "$thought, $currentConnection")
        val index = parent.children.indexOfFirst { it.id == thought.id }
        val prev = if (index > 0) {
            parent.children[index - 1].id
        } else {
            Id.Unknown
        }
        val next = if (index < parent.children.size - 1) {
            parent.children[index + 1].id
        } else {
            Id.Unknown
        }
        val child = when (currentConnection) {
            is Connection.Interim -> currentConnection.children.first().id
            is Connection.Terminal -> Id.Unknown
        }
        return ThoughtView.Model.Default(
            ThoughtView.Model.Thought(
                thought.title,
                next = next,
                previous = prev,
                child = child,
                parent = parent.id,
            )
        )
    }

    // awful, just awful
    private fun parentAndChildConnection(
        parent: Id.Known,
        child: Id.Known
    ): Flow<Pair<Connection.Interim, Connection>> =
        connectionsRepository.query(parent).flatMapLatest {
            val parent = it.getOrThrow()
            when (parent) {
                is Connection.Interim ->
                    connectionsRepository.query(child).map { child ->
                        log("asdf", "resolved child: $child")
                        parent to child.getOrThrow()
                    }
                is Connection.Terminal ->
                    throw IllegalStateException("terminal parent, shouldn't ever be here")
            }

        }
}

private val ENTRY_ID = "entry".id