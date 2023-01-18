package v47.mindmap.android.ui

internal sealed class Navigation(val tag: String) {

    object Main : Navigation("MAIN")
    object NewThought : Navigation("NEW")
}

internal typealias Navigator = (Navigation) -> Unit