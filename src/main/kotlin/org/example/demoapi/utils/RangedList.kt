package org.example.demoapi.utils

class RangedList<E>(items: List<E>, val start: Int = 0, val total: Int? = null) : List<E> by items

fun <T> List<T>.ranged(range: Range?) = when {
    range == null -> this
    range.start != null -> RangedList(
            this.subList(range.start, (range.end ?: this.size - 1) + 1),
            range.start,
            this.size
    )
    else -> RangedList(
            this.subList(this.size - (range.end ?: 0), this.size),
            this.size - (range.end ?: 0),
            this.size
    )
}