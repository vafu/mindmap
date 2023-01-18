package v47.mindmap.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import v47.mindmap.ThoughtViewModel
import v47.mindmap.common.Id

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThoughtScreen(
    navController: NavController,
    viewModel: ThoughtViewModel = koinViewModel(),
) {
    val state by viewModel.models.collectAsState(ThoughtViewModel.Model.Empty)
    Scaffold(
        topBar = { TopBar(model = state, navController) },
    ) { padding ->
        Surface(Modifier.padding(padding)) {
            DefaultThought(model = state) { current ->
                viewModel.loadThought(current)
            }
        }
    }
}

@Composable
private fun DefaultThought(
    model: ThoughtViewModel.Model,
    onSelected: (Id.Known) -> Unit
) {
    when (model) {
        is ThoughtViewModel.Model.Empty -> Text(text = "Empty!")
        is ThoughtViewModel.Model.Default -> Thought(model.thought, onSelected)
    }
}

@Composable
fun Thought(
    model: ThoughtViewModel.Model.Thought,
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
    model: ThoughtViewModel.Model,
    navController: NavController,
) {
    SmallTopAppBar(
        actions = {
            IconButton(
                onClick = {
                    navController.navigate(Nav.ADD)
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
                is ThoughtViewModel.Model.Empty -> "empty"
                is ThoughtViewModel.Model.Default ->
                    model.thought.title
            }
            Text(text = title)
        }
    )
}
