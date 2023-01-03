package v47.mindmap.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
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
import v47.mindmap.android.ui.theme.MyApplicationTheme
import v47.mindmap.common.Id
import v47.mindmap.connections.StaticConnectionsRepository
import v47.mindmap.thought.StaticThoughtRepository

@OptIn(ExperimentalMaterial3Api::class)
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
                Scaffold(
                    topBar = { TopBar(model = state) },
                ) { padding ->
                    Surface(Modifier.padding(padding)) {
                        DefaultThought(model = state) { current ->
                            // crap
                            GlobalScope.launch(Dispatchers.Main) {
                                _events.emit(ThoughtView.Event.Select(current))
                            }
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

@Composable
private fun TopBar(
    model: ThoughtView.Model
) {
    SmallTopAppBar(
        actions = {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "desc"
                )
            }
        },
        title = {
            val title = when (model) {
                is ThoughtView.Model.Empty -> "empty"
                is ThoughtView.Model.Default ->
                    model.thought.title
            }
            Text(text = title)
        }
    )
}
