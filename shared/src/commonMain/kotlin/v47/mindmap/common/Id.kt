package v47.mindmap.common

sealed class Id {

    sealed class Known(val value: Any) : Id() {

        class String(string: kotlin.String) : Known(string)
        class Long(long: kotlin.Long) : Known(long)
        class Int(int: kotlin.Int) : Known(int)

        override fun toString(): kotlin.String = "Id[$value]"
        override fun hashCode(): kotlin.Int {
            return value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Known) return false

            if (value != other.value) return false

            return true
        }
    }

    object Unknown : Id() {
        override fun toString(): String = "Id.Unknown"
    }
}

val Long.id
    get() = Id.Known.Long(this)

val String.id
    get() = Id.Known.String(this)

val Int.id
    get() = Id.Known.Int(this)

