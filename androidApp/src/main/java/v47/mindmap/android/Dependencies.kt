package v47.mindmap.android

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import v47.mindmap.viewmodels.DefaultThoughtPreviewViewModel
import v47.mindmap.viewmodels.ThoughtPreviewViewModel

val viewModels = module {
    viewModel<ThoughtPreviewViewModel> {
        DefaultThoughtPreviewViewModel(get(), get())
    }
}