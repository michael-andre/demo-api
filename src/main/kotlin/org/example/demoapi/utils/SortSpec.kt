package org.example.demoapi.utils

data class SortSpec<T : Enum<*>>(
        val key: T,
        val direction: Direction
) {

    enum class Direction {
        ASC,
        DESC
    }

}