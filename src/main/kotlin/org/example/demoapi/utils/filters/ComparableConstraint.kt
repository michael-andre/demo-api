package org.example.demoapi.utils.filters

sealed class ComparableConstraint<T : Comparable<T>> {
    data class Value<T : Comparable<T>>(val value: T?) : ComparableConstraint<T>()
    data class Bounds<T : Comparable<T>>(val min: T?, val max: T?) : ComparableConstraint<T>()
}

fun <T : Comparable<T>> T?.matches(constraints: Collection<ComparableConstraint<T>>?) = when {
    constraints == null -> true
    constraints.isEmpty() -> this != null
    else -> constraints.any {
        when (it) {
            is ComparableConstraint.Value -> when (it.value) {
                null -> this == null
                else -> this?.compareTo(it.value) == 0
            }
            is ComparableConstraint.Bounds -> this != null
                    && (if (it.min != null) (this >= it.min) else true)
                    && (if (it.max != null) (this <= it.max) else true)
        }
    }
}

fun <T> T?.matches(constraints: Collection<T?>?) = when {
    constraints == null -> true
    constraints.isEmpty() -> this != null
    else -> constraints.contains(this)
}