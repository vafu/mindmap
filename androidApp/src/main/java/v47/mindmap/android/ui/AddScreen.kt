package v47.mindmap.android.ui

import android.view.Display.Mode
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import v47.mindmap.viewmodels.NewThoughtViewModel

@Composable
internal fun NewThoughtScreen(
    navigator: Navigator,
    viewModel: NewThoughtViewModel = koinViewModel()
) {
    val state by viewModel.models.collectAsState(NewThoughtViewModel.Model.Editing(""))
    EditView(
        model = state,
        updateField = viewModel::update,
        save = viewModel::save,
        navigator = navigator
    )
}

@Composable
private fun EditView(
    model: NewThoughtViewModel.Model,
    updateField: (String) -> Unit,
    save: () -> Unit,
    navigator: Navigator,
) {
    when (model) {
        is NewThoughtViewModel.Model.Editing -> {
            Row {
                TextField(
                    value = model.string,
                    onValueChange = updateField
                )
                Button(onClick = save) {
                    Text(text = "Save!")
                }
            }
        }

        is NewThoughtViewModel.Model.Done -> {
            navigator(Navigation.Main)
        }
    }
}