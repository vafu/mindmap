package v47.mindmap.viewmodels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import v47.mindmap.common.Id
import v47.mindmap.common.ViewModel
import v47.mindmap.common.id
import v47.mindmap.connections.ConnectionsRepository
import v47.mindmap.thought.Thought
import v47.mindmap.thought.ThoughtsRepository

abstract class NewThoughtViewModel : ViewModel() {

    abstract val models: Flow<Model>

    abstract fun save()
    abstract fun update(title: String)

    sealed class Model {

        data class Editing(val string: String) : Model()

        object Done : Model()
    }
}

class DefaultNewThoughtViewModel(
    private val thoughtsRepository: ThoughtsRepository,
    private val connectionsRepository: ConnectionsRepository,
) : NewThoughtViewModel() {

    private val _models = MutableStateFlow<Model>(Model.Editing(""))
    override val models: Flow<Model> = _models

    override fun update(title: String) {
        scope.launch {
            _models.emit(Model.Editing(title))
        }
    }

    override fun save() {
        val model = _models.value
        if (model !is Model.Editing) return
        val id = Clock.System.now().epochSeconds.id

        scope.launch {
            thoughtsRepository.save(Thought(id, model.string))
            _models.emit(Model.Done)
        }
        scope.launch {
            connectionsRepository.connect("entry".id, id)
        }
    }
}