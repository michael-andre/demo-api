package org.example.demoapi.model.patches

import java.time.LocalDate

data class ItemPatch(
        val name: String?,
        val date: LocalDate?
)