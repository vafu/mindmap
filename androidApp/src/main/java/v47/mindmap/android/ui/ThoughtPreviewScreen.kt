package v47.mindmap.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import v47.mindmap.android.R
import v47.mindmap.common.Id
import v47.mindmap.viewmodels.ThoughtPreviewViewModel.*
import v47.mindmap.viewmodels.ThoughtPreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThoughtPreviewScreen(
    viewModel: ThoughtPreviewViewModel = koinViewModel()
) {
    val state by viewModel.model.collectAsState(initial = Model.Loading)

    Scaffold(
        Modifier,
        topBar = {
            ThoughtBar(model = state) {
                viewModel.popBack()
            }
        }
    ) { padding ->
        ThoughtContainer(
            modifier = Modifier.padding(8.dp),
            model = state,
        ) {
            viewModel.previewThought(it)
        }
    }
}

@Composable
private fun ThoughtContainer(
    modifier: Modifier,
    model: Model,
    loadThought: (Id.Known) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        when (model) {
            is Model.WithPreview -> ThoughtPreview(model) { thumbnail ->
                when (val id = thumbnail.id) {
                    is Id.Known -> loadThought(id)
                    else -> {}
                }
            }
            is Model.Loading -> Loading()
        }
    }
}

@Composable
private fun ThoughtPreview(
    model: Model.WithPreview,
    onThumbnailSelect: (Model.Thumbnail) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
    ) {
        when (val preview = model.preview) {
            is Model.Preview.Text ->
                ThoughtPreviewText(preview)
        }
        model.thumbnails.forEach { thumbnail ->
            Thumbnail(thumbnail = thumbnail) {
                onThumbnailSelect(thumbnail)
            }
        }
    }
}

@Composable
private fun Thumbnail(
    thumbnail: Model.Thumbnail,
    click: () -> Unit
) {
    val color = if (thumbnail is Model.Thumbnail.Error) Color.Red else Color.White
    val text = when (thumbnail) {
        is Model.Thumbnail.Text -> thumbnail.title
        is Model.Thumbnail.Error -> thumbnail.message
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(onClick = click),
        backgroundColor = color,
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 8.dp),
            text = text,
        )
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ThoughtPreviewText(
    preview: Model.Preview.Text
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Text(text = stringResource(id = R.string.lorem_ipsum))
    }
}

@Composable
private fun ThoughtBar(
    model: Model,
    onBackClick: () -> Unit
) {
    SmallTopAppBar(
        actions = {
        },
        navigationIcon = {
            if (model is Model.WithPreview && model.hasBackButton) {
                IconButton(onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "go back",
                    )
                }
            }
        },
        title = {
            val title = when (model) {
                is Model.Loading ->
                    stringResource(id = R.string.preview_screen_loading)
                is Model.WithPreview -> model.title
            }
            Text(text = title)
        }
    )
}