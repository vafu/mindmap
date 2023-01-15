package v47.mindmap.common

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as AndroidXViewModel

actual abstract class ViewModel : AndroidXViewModel() {

    actual val scope: CoroutineScope = viewModelScope

    actual override fun onCleared() {}
}