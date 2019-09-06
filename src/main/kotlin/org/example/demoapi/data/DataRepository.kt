package org.example.demoapi.data

import org.example.demoapi.model.Item
import org.example.demoapi.utils.Range
import org.example.demoapi.utils.filters.ComparableConstraint
import org.example.demoapi.utils.ranged
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import kotlin.random.Random

object DataRepository {

    private val items = (1..1000).map {
        Item(
                id = it.toLong(),
                name = "Item $it",
                active = true,
                value = Random.nextFloat().takeIf { it > 0.2f }?.toBigDecimal(),
                date = LocalDate.of(2000, Month.JANUARY, 1).plusDays(Random.nextLong(10000))
        )
    }.toMutableList()

    fun getItems(
            name: Set<String>? = null,
            date: Set<ComparableConstraint<LocalDate>>? = null,
            value: Set<ComparableConstraint<BigDecimal>>? = null,
            active: Set<Boolean>? = null,
            range: Range? = null
    ) = items.filter { item ->
        (name == null || name.any { item.name.contains(it, true) })
                && item.date.matches(date)
                && item.value.matches(value)
                && item.value.matches(active)
    }.ranged(range)

    fun observeItems(
            name: Set<String>? = null,
            date: Set<ComparableConstraint<LocalDate>>? = null,
            value: Set<ComparableConstraint<BigDecimal>>? = null,
            active: Set<Boolean>? = null,
            range: Range? = null
    ) = Mono.fromCallable { getItems(name, date, value, active, range) }
            .withUpdates()

    fun getItem(id: Long) = items.single { it.id == id }

    fun observeItem(id: Long) = Mono.fromCallable { getItem(id) }
            .withUpdates { it.id == id }

    private fun getNextId() = (items.map { it.id }.max() ?: 0) + 1L

    fun createItem(item: Item) {
        items.add(item.copy(id = getNextId()))
        itemUpdates.onNext(Update(item.id, item))
    }

    fun updateItem(item: Item) = item.also {
        items.replaceAll { if (it.id == item.id) item else it }
        itemUpdates.onNext(Update(item.id, item))
    }

    fun updateItem(id: Long, patch: Item.Patch) = updateItem(getItem(id).run {
        copy(
                name = patch.name ?: name,
                active = patch.active ?: active,
                value = patch.value?.orElse(null) ?: value,
                date = patch.date ?: date
        )
    })

    fun deleteItem(id: Long) {
        assert(items.removeIf { it.id == id })
        itemUpdates.onNext(Update(id))
    }

    private val itemUpdates = EmitterProcessor.create<Update>(false)

    private data class Update(
            val id: Long? = null,
            val value: Item? = null
    )

    private fun <T> Mono<T>.withUpdates(updateFilter: (Update) -> Boolean = { true }) = this
            .repeatWhen { it.switchMap { itemUpdates.filter(updateFilter) } }
            .distinctUntilChanged()

    private fun <T : Comparable<T>> T?.matches(constraints: Collection<ComparableConstraint<T>>?) = when {
        constraints == null -> true
        constraints.isEmpty() -> this != null
        else -> constraints.any {
            when (it) {
                is ComparableConstraint.Value -> when (it.value) {
                    null -> this == null
                    else -> this?.compareTo(it.value) == 0
                }
                is ComparableConstraint.Bounds -> this != null
                        && (if (it.min != null) (this >= it.min) else true)
                        && (if (it.max != null) (this <= it.max) else true)
            }
        }
    }

    private fun <T> T?.matches(constraints: Collection<T?>?) = when {
        constraints == null -> true
        constraints.isEmpty() -> this != null
        else -> constraints.contains(this)
    }

}