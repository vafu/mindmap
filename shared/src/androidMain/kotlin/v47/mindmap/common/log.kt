package v47.mindmap.common

actual fun log(tag: String, message: String) {
    android.util.Log.d(tag, message)
}