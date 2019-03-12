package org.example.demoapi.data

import org.example.demoapi.model.Item
import org.example.demoapi.model.patches.ItemPatch
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import java.time.LocalDate
import java.time.Month

object DataRepository {

    private val items = (1..1000).map {
        Item(it.toLong(), "Item $it", LocalDate.of(2000, Month.JANUARY, 1).plusDays(it.toLong()))
    }.toMutableList()

    fun getItems(
            name: String? = null,
            date: ClosedRange<LocalDate>? = null
    ) = items.filter {
        (name == null || it.name.contains(name, true))
                && (date == null || date.contains(it.date))
    }

    fun observeItems(
            name: String? = null,
            date: ClosedRange<LocalDate>? = null
    ) = trigger()
            .map { getItems(name, date) }
            .distinctUntilChanged()

    fun getItem(id: Long) = items.single { it.id == id }

    fun observeItem(id: Long) =
            trigger { it.id == id }
                    .map { it.value ?: getItem(id) }
                    .distinctUntilChanged()

    private fun getNextId() = (items.map { it.id }.max() ?: 0) + 1L

    fun createItem(item: Item) {
        items.add(item.copy(id = getNextId()))
        itemUpdates.onNext(Update(item.id, item))
    }

    fun updateItem(item: Item) = item.also {
        items.replaceAll { if (it.id == item.id) item else it }
        itemUpdates.onNext(Update(item.id, item))
    }

    fun updateItem(id: Long, patch: ItemPatch) = updateItem(getItem(id).run {
        copy(
                name = patch.name ?: name,
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

    private fun trigger(updateFilter: (Update) -> Boolean = { true }) =
            Flux.just(Update())
                    .concatWith(itemUpdates.filter(updateFilter))

}