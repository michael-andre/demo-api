package org.example.demoapi.api

import org.example.demoapi.utils.filters.ComparableConstraint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalQuery

open class ConstraintConverter<T : Comparable<T>>(private val type: Class<T>) : Converter<String, ComparableConstraint<T>> {

    @Autowired
    lateinit var conversionService: ConversionService

    override fun convert(src: String): ComparableConstraint<T>? {
        if (src.equals("null", ignoreCase = true)) {
            return ComparableConstraint.Value(null)
        }
        val parts = src.split("~", limit = 2)
        return if (parts.size == 1) {
            ComparableConstraint.Value(conversionService.convert(src, type))
        } else {
            ComparableConstraint.Bounds(
                conversionService.convert(parts[0], type),
                conversionService.convert(parts[1], type)
            )
        }
    }

}

@Component
object BigDecimalConstraintConverter : ConstraintConverter<BigDecimal>(BigDecimal::class.java)

@Component
object DateConstraintConverter : Converter<String, ComparableConstraint<LocalDate>> {

    private val queries = arrayOf(
            TemporalQuery(LocalDate::from),
            TemporalQuery(YearMonth::from),
            TemporalQuery(Year::from)
    )

    override fun convert(src: String): ComparableConstraint<LocalDate>? {
        if (src.equals("null", ignoreCase = true)) {
            return ComparableConstraint.Value(null)
        }
        val parts = src.split("~", limit = 2)
        return if (parts.size == 1) {
            when (val d = parse(src)) {
                is LocalDate -> ComparableConstraint.Value(d)
                else -> ComparableConstraint.Bounds(d.startDate, d.endDate)
            }
        } else {
            ComparableConstraint.Bounds(
                    parse(parts[0]).startDate,
                    parse(parts[1]).endDate
            )
        }
    }

    private fun parse(src: String) = DateTimeFormatter.ofPattern("yyyy[-MM[-dd]]").parseBest(src, *queries)

    private val TemporalAccessor.startDate: LocalDate?
        get() = when (this) {
            is LocalDate -> this
            is YearMonth -> this.atDay(1)
            is Year -> this.atDay(1)
            else -> null
        }

    private val TemporalAccessor.endDate: LocalDate?
        get() = when (this) {
            is LocalDate -> this
            is YearMonth -> this.atEndOfMonth()
            is Year -> this.atMonth(Month.DECEMBER).atEndOfMonth()
            else -> null
        }

}