package org.example.demoapi.model

import java.time.LocalDate

data class Item(
        val id: Long,
        val name: String,
        val date: LocalDate
)