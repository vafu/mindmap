package v47.mindmap.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import v47.mindmap.DefaultThoughtViewModel
import v47.mindmap.ThoughtViewModel
import v47.mindmap.android.ui.ThoughtScreen
import v47.mindmap.android.ui.theme.MyApplicationTheme
import v47.mindmap.common.Id
import v47.mindmap.connections.StaticConnectionsRepository
import v47.mindmap.thought.StaticThoughtRepository

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val viewModel: ThoughtViewModel =
        DefaultThoughtViewModel(
            StaticConnectionsRepository,
            StaticThoughtRepository,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                ThoughtScreen(viewModel)
            }
        }

        //crap
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.loadThought(Id.Known.String("entry"))
        }
    }
}
