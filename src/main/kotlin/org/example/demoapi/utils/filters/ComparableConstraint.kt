package org.example.demoapi.utils.filters

sealed class ComparableConstraint<T : Comparable<T>> {
    data class Value<T : Comparable<T>>(val value: T?) : ComparableConstraint<T>()
    data class Bounds<T : Comparable<T>>(val min: T?, val max: T?) : ComparableConstraint<T>()
}