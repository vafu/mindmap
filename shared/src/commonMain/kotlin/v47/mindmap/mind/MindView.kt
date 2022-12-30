package v47.mindmap.mind

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import v47.mindmap.common.Id

expect interface MindView {

    val events: Flow<MindViewEvent>

    fun accept(model: MindViewModel)
}

sealed class MindViewModel {

    data class Default(
        val thoughts: Set<Thought>
    ) : MindViewModel()

    object Empty : MindViewModel()

    data class Thought(
        val id: Id.Known,
        val title: String,
        val connectedTo: Set<Id.Known>,
    )
}

sealed class MindViewEvent {

}

// region tmp

class MindViewPresenter(
    private val mindRepository: MindRepository,
    private val thoughtRepository: ThoughtRepository,
    private val mindView: MindView,
) {

    suspend fun start(): Unit =
        mindRepository.mind
            .flatMapLatest { mind ->
                thoughtRepository.query(*mind.thoughts.toTypedArray())
                    .map { it to mind.connections }
            }
            .map { (thoughts, connections) ->
                MindViewModel.Default(
                    thoughts.values.map {
                        MindViewModel.Thought(
                            it.id,
                            it.title,
                            connections[it.id] ?: emptySet()
                        )
                    }.toSet()
                )
            }
            .collect(mindView::accept)
}

// endregion