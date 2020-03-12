package org.example.demoapi.utils

sealed class Range
data class OffsetRange(
    val start: Int,
    val end: Int?
) : Range() {

    init {
        if (end != null) assert(start <= end)
    }

}
data class EndOffsetRange(val count: Int) : Range()