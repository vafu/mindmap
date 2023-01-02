package v47.mindmap.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import v47.mindmap.ThoughtPresenter
import v47.mindmap.ThoughtView
import v47.mindmap.common.Id
import v47.mindmap.connections.StaticConnectionsRepository
import v47.mindmap.thought.StaticThoughtRepository

class MainActivity : ComponentActivity(), ThoughtView {

    // tmp, need better arch
    private val presenter = ThoughtPresenter(
        StaticConnectionsRepository,
        StaticThoughtRepository,
        this,
    )

    private var state by mutableStateOf<ThoughtView.Model>(ThoughtView.Model.Empty)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DefaultThought(model = state) { current ->
                        // crap
                        GlobalScope.launch(Dispatchers.Main) {
                            _events.emit(ThoughtView.Event.Select(current))
                        }
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            presenter.start()
        }
    }

    private val _events = MutableSharedFlow<ThoughtView.Event>()
    override val events: Flow<ThoughtView.Event>
        get() = _events

    override fun accept(model: ThoughtView.Model) {
        state = model
    }
}


@Composable
fun DefaultThought(model: ThoughtView.Model, onSelected: (Id.Known) -> Unit) {
    when (model) {
        is ThoughtView.Model.Empty -> Text(text = "Empty!")
        is ThoughtView.Model.Default -> Thought(model.thought, onSelected)
    }
}

@Composable
fun Thought(
    model: ThoughtView.Model.Thought,
    selected: (Id.Known) -> Unit,
) {
    Row {
        Text(text = model.title)
        // shouldn't be here, go sleep
        val nextId = model.next
        if (nextId is Id.Known) {
            Button(onClick = { selected(nextId) }) {
                Text(text = "Next")
            }
        }
        val prevId = model.prev
        if (prevId is Id.Known) {
            Button(onClick = { selected(prevId) }) {
                Text(text = "prev")
            }
        }
        val parent = model.parent
        if (parent is Id.Known) {
            Button(onClick = { selected(parent) }) {
                Text(text = "parent")
            }
        }
        val children = model.children
        if (children.isNotEmpty()) {
            Children(children = children, selected)
        }
    }
}

@Composable
private fun Children(
    children: List<Id.Known>,
    selected: (Id.Known) -> Unit
) {
    Column {
        children.forEach { child ->
            Button(onClick = { selected(child) }) {
                Text(text = child.toString())
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {

    }
}
