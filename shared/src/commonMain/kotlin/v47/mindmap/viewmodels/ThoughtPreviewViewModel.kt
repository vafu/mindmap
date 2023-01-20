package v47.mindmap.viewmodels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import v47.mindmap.common.Id
import v47.mindmap.common.ViewModel
import v47.mindmap.common.log
import v47.mindmap.connections.ConnectionsRepository
import v47.mindmap.thought.Thought
import v47.mindmap.thought.ThoughtsRepository

abstract class ThoughtPreviewViewModel : ViewModel() {

    abstract val model: Flow<Model>

    abstract fun previewThought(id: Id.Known)
    abstract fun popBack()

    sealed class Model {

        data class WithPreview(
            val title: String,
            val preview: Preview,
            val thumbnails: List<Thumbnail>,
            val hasBackButton: Boolean,
        ) : Model()

        object Loading : Model()

        sealed class Preview {

            data class Text(
                val content: String,
            ) : Preview()
        }

        sealed class Thumbnail {

            abstract val id: Id

            data class Text(
                override val id: Id.Known,
                val title: String
            ) : Thumbnail()

            data class Error(
                val message: String
            ) : Thumbnail() {
                override val id = Id.Unknown
            }
        }
    }
}

class DefaultThoughtPreviewViewModel(
    private val thoughtsRepository: ThoughtsRepository,
    private val connectionsRepository: ConnectionsRepository,
) : ThoughtPreviewViewModel() {

    private val _model = MutableStateFlow<Model>(Model.Loading)
    override val model: Flow<Model> = _model

    init {
        scope.launch {
            loadThought(ConnectionsRepository.Criteria.Root)
        }
    }

    override fun previewThought(id: Id.Known) {
        scope.launch {
            loadThought(ConnectionsRepository.Criteria.ById(id))
        }
    }

    override fun popBack() {

    }

    private suspend fun loadThought(criteria: ConnectionsRepository.Criteria) {
        _model.emit(Model.Loading)
        val connection = connectionsRepository.query(criteria).getOrThrow()
        val children = connection.children().mapTo(mutableSetOf()) { it.id }
        val thoughts = thoughtsRepository.query(children + connection.id).getOrThrow()
        val current = thoughts[connection.id]
            ?: throw IllegalStateException("Couldn't load ${connection.id}")
        _model.emit(
            Model.WithPreview(
                title = current.title,
                preview = current.toPreview(),
                thumbnails = children.map {
                    thoughts[it]?.toThumbnail() ?: Model.Thumbnail.Error("Couldn't load $it")
                },
                hasBackButton = true
            )
        )
    }
}

private fun Thought.toPreview(): ThoughtPreviewViewModel.Model.Preview =
    when (this) {
        is Thought.Text ->
            ThoughtPreviewViewModel.Model.Preview.Text(
                content,
            )
    }

private fun Thought.toThumbnail(): ThoughtPreviewViewModel.Model.Thumbnail =
    when (this) {
        is Thought.Text ->
            ThoughtPreviewViewModel.Model.Thumbnail.Text(
                id,
                title,
            )
    }
