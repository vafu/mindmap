package v47.mindmap.viewmodels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import v47.mindmap.common.Id
import v47.mindmap.common.ViewModel
import v47.mindmap.common.log
import v47.mindmap.connections.Connection
import v47.mindmap.connections.ConnectionsRepository
import v47.mindmap.thought.ThoughtsRepository
import v47.mindmap.thought.query

abstract class ThoughtViewModel : ViewModel() {

    abstract val models: Flow<Model>

    abstract fun loadThought(id: Id.Known)

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

class DefaultThoughtViewModel(
    private val connectionsRepository: ConnectionsRepository,
    private val thoughtRepository: ThoughtsRepository,
) : ThoughtViewModel() {

    private val _models = MutableStateFlow<Model>(Model.Empty)
    override val models: Flow<Model> = _models

    init {
        loadThought(ConnectionsRepository.Criteria.Root)
    }

    override fun loadThought(id: Id.Known) {
        loadThought(ConnectionsRepository.Criteria.ById(id))
    }

    private fun loadThought(criteria: ConnectionsRepository.Criteria) {
        scope.launch {
            connectionsRepository.query(criteria)
                .getOrThrow()
                .asModel()
                .let { _models.emit(it) }
        }
    }

    private suspend fun Connection.asModel(): Model {
        val current = this
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
        return Model.Default(
            Model.Thought(
                thoughtRepository.query(current.id).getOrThrow().title,
                next = next,
                prev = prev,
                children = children,
                parent = parent?.id ?: Id.Unknown
            )
        )
    }
}

