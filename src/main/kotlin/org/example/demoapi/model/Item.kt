package org.example.demoapi.model

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class Item(
        val id: Long,
        val name: String,
        val active: Boolean,
        val value: BigDecimal?,
        val date: LocalDate
) {

    data class Patch(
        val name: String?,
        val active: Boolean?,
        val value: Optional<BigDecimal>?,
        val date: LocalDate?
    )

}