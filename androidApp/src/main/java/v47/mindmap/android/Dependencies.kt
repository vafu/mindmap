package v47.mindmap.android

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import v47.mindmap.viewmodels.DefaultNewThoughtViewModel
import v47.mindmap.viewmodels.DefaultThoughtViewModel
import v47.mindmap.viewmodels.NewThoughtViewModel
import v47.mindmap.viewmodels.ThoughtViewModel

val viewModels = module {
    viewModel<ThoughtViewModel> {
        DefaultThoughtViewModel(get(), get())
    }
    viewModel<NewThoughtViewModel> {
        DefaultNewThoughtViewModel(get(), get())
    }
}