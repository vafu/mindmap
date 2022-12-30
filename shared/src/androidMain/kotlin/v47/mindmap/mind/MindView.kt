package v47.mindmap.mind

import kotlinx.coroutines.flow.Flow

actual interface MindView {
    actual val events: Flow<MindViewEvent>
    actual fun accept(model: MindViewModel)
}