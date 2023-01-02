package v47.mindmap.common

actual fun <T: Any> T.log(message: () -> String): T {
    android.util.Log.d(this::class.java.simpleName, message())
    return this
}