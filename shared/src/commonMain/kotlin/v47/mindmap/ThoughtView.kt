package v47.mindmap

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import v47.mindmap.common.Id
import v47.mindmap.common.log
import v47.mindmap.connections.Connection
import v47.mindmap.connections.ConnectionsRepository
import v47.mindmap.thought.ThoughtsRepository
import v47.mindmap.thought.query

interface ThoughtView {

    val events: Flow<Event>

    fun accept(model: Model)

    sealed class Event {

        // should it be in event explicitly like this?
        data class Select(val id: Id.Known) : Event()
    }

    sealed class Model {

        data class Default(
            val thought: Thought
        ) : Model()

        object Empty : Model()

        data class Thought(
            val title: String,
            val next: Id,
            val prev: Id,
            val parent: Id,
            val children: List<Id.Known>,
        )
    }

}

class ThoughtPresenter(
    private val connectionsRepository: ConnectionsRepository,
    private val thoughtRepository: ThoughtsRepository,
    private val mindView: ThoughtView,
) {
    private val model = MutableStateFlow<CurrentState>(CurrentState.Empty)

    suspend fun start(): Unit = coroutineScope {
        launch {
            model
                .map(::displayModel)
                .collect(mindView::accept)
        }

        launch {
            merge(
                flowOf(ConnectionsRepository.Criteria.Root),
                mindView.events
                    .filterIsInstance<ThoughtView.Event.Select>()
                    .map { ConnectionsRepository.Criteria.ById(it.id) }
            )
                .map { id ->
                    CurrentState.WithConnection(
                        connectionsRepository.query(id).getOrThrow()
                    )
                }
                .collect(model::emit)
        }
    }

    private suspend fun displayModel(
        currentState: CurrentState,
    ): ThoughtView.Model {
        if (currentState !is CurrentState.WithConnection) return ThoughtView.Model.Empty
        val current = currentState.connection
        val parent = if (current is Connection.Interim) {
            current.parent()
        } else {
            null
        }
        val siblings = parent?.children?.invoke() ?: emptyList()
        // ineffective?
        val index = siblings.indexOfFirst { it.id == current.id }

        fun connectionForIndex(i: Int): Id =
            if (i in siblings.indices) {
                siblings[i].id
            } else {
                Id.Unknown
            }

        val next = connectionForIndex(index + 1)
        val prev = connectionForIndex(index - 1)

        log { "nextId: $next, prev: $prev, index: $index" }
        val children = current.children().map { it.id }

        children.log { children.toString() }
        return ThoughtView.Model.Default(
            ThoughtView.Model.Thought(
                thoughtRepository.query(current.id).getOrThrow().title,
                next = next,
                prev = prev,
                children = children,
                parent = parent?.id ?: Id.Unknown
            )
        )
    }
}

private sealed class CurrentState {

    object Empty : CurrentState()

    data class WithConnection(val connection: Connection) : CurrentState()
}