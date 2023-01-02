package v47.mindmap.common

inline fun <reified T, reified R : T> Result<T>.unwrapAs(mapper: (T) -> R?): R =
    getOrThrow().let(mapper) ?: throw IllegalArgumentException(
        "Can't unwrap ${T::class.simpleName} as ${R::class.simpleName}!"
    )
