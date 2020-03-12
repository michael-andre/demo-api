package org.example.demoapi.services

import org.example.demoapi.api.RequestRange
import org.example.demoapi.utils.Range
import org.example.demoapi.data.DataRepository
import org.example.demoapi.model.Item
import org.example.demoapi.utils.SortSpec
import org.example.demoapi.utils.filters.ComparableConstraint
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/items")
class ItemsController {

    // Collection routes

    @GetMapping("")
    fun list(
            @RequestParam(required = false) name: Set<String>?,
            @RequestParam(required = false) date: Set<ComparableConstraint<LocalDate>>?,
            @RequestParam(required = false) value: Set<ComparableConstraint<BigDecimal>>?,
            @RequestParam(required = false) active: Set<Boolean>?,
            @RequestParam(name = "_sort", required = false) sort: List<SortSpec<Item.Sort>>?,
            @RequestRange(defaultSize = 50, maxSize = 100) range: Range?
    ) = DataRepository.getItems(name, date, value, active, sort, range)

    @GetMapping("", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun observeList(
            @RequestParam(required = false) name: Set<String>?,
            @RequestParam(required = false) date: Set<ComparableConstraint<LocalDate>>?,
            @RequestParam(required = false) value: Set<ComparableConstraint<BigDecimal>>?,
            @RequestParam(required = false) active: Set<Boolean>?,
            @RequestParam(name = "_sort", required = false) sort: List<SortSpec<Item.Sort>>?,
            @RequestRange(defaultSize = 50, maxSize = 100) range: Range?
    ) = DataRepository.observeItems(name, date, value, active, sort, range)

    @PatchMapping("")
    fun patch(@RequestBody patches: Map<Long, Item.Patch?>) = patches.mapValues {
        when (val patch = it.value) {
            null -> this.delete(it.key).let { null }
            else -> this.patch(it.key, patch)
        }
    }

    @PostMapping("")
    fun create(@RequestBody item: Item) = DataRepository.createItem(item)

    // Item routes

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = DataRepository.getItem(id)

    @GetMapping("/{id}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun observe(@PathVariable id: Long) = DataRepository.observeItem(id)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody patch: Item.Patch) = DataRepository.updateItem(id, patch)

    @PutMapping("/{id}")
    fun put(@PathVariable id: Long, @RequestBody item: Item): Item {
        assert(item.id == id)
        return DataRepository.updateItem(item)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        DataRepository.deleteItem(id)
    }

}