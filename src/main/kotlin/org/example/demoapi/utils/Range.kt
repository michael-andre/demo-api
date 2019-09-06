package org.example.demoapi.utils

data class Range(
    val start: Int?,
    val end: Int?
) {

    init {
        assert(start != null || end != null)
        if (start != null && end != null) assert(start <= end)
    }

}