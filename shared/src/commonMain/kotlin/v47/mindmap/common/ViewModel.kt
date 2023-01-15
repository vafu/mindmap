package v47.mindmap.common

import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {

    val scope: CoroutineScope

    protected open fun onCleared()
}