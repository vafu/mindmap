package v47.mindmap.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import v47.mindmap.mind.MindView
import v47.mindmap.mind.MindViewEvent
import v47.mindmap.mind.MindViewPresenter
import v47.mindmap.mind.StaticMindRepository
import v47.mindmap.mind.StaticThoughtRepository
import v47.mindmap.mind.MindViewModel

class MainActivity : ComponentActivity(), MindView {

    // tmp, need better arch
    private val presenter = MindViewPresenter(
        StaticMindRepository,
        StaticThoughtRepository,
        this,
    )

    private var state by mutableStateOf<MindViewModel>(MindViewModel.Empty)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DefaultThought(model = state)
                }
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            presenter.start()
        }
    }

    override val events: Flow<MindViewEvent>
        get() = TODO("Not yet implemented")

    override fun accept(model: MindViewModel) {
        state = model
    }
}


@Composable
fun DefaultThought(model: MindViewModel) {
    when (model) {
        is MindViewModel.Empty -> Text(text = "Empty!")
        is MindViewModel.Default -> Column {
            model.thoughts.forEach {
                Thought(model = it)
            }
        }
    }
}

@Composable
fun Thought(model: MindViewModel.Thought) {
    Row {
        Text(text = model.title)
        Text(text = model.connectedTo.toString(), color = Color.Red)
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {

    }
}
