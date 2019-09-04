package org.example.demoapi.services

import org.example.demoapi.data.DataRepository
import org.example.demoapi.model.Item
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/items")
class ItemsController {

    // Collection routes

    @GetMapping("")
    fun list(
            @RequestParam(required = false) name: String? = null,
            @RequestParam(required = false) date: String? = null
    ) = DataRepository.getItems(name)

    @GetMapping("", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun observeList(
            @RequestParam(required = false) name: String? = null,
            @RequestParam(required = false) date: String? = null
    ) = DataRepository.observeItems(name)

    @PatchMapping("")
    fun patch(@RequestBody patches: Map<Long, Item.Patch?>) = patches.mapValues {
        val patch = it.value
        if (patch != null) this.patch(it.key, patch)
        else this.delete(it.key).let { null }
    }

    @PostMapping("")
    fun create(@RequestBody item: Item) = DataRepository.createItem(item)

    @PostMapping("")
    fun create(@RequestBody items: List<Item>) = items.map {
        DataRepository.createItem(it)
    }

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