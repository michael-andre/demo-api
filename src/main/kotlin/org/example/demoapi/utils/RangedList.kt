package org.example.demoapi.utils

class RangedList<E>(items: List<E>, val start: Int = 0, val total: Int? = null) : List<E> by items

fun <T> List<T>.ranged(range: Range?) = when (range) {
    is OffsetRange -> RangedList(
            this.subList(range.start, (range.end ?: this.size - 1) + 1),
            range.start,
            this.size
    )
    is EndOffsetRange -> RangedList(
            this.subList(this.size - range.count, this.size),
            this.size - range.count,
            this.size
    )
    else -> this
}