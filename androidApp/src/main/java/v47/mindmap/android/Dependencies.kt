package v47.mindmap.android

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import v47.mindmap.DefaultThoughtViewModel
import v47.mindmap.ThoughtViewModel

val viewModels = module {
    viewModel<ThoughtViewModel> {
        DefaultThoughtViewModel(get(), get())
    }
}