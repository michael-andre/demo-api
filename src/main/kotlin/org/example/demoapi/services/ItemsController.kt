package org.example.demoapi.services

import org.example.demoapi.data.DataRepository
import org.example.demoapi.model.Item
import org.example.demoapi.model.patches.ItemPatch
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

    @PostMapping("")
    fun create(@RequestBody item: Item) = DataRepository.createItem(item)

    // Item routes

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = DataRepository.getItem(id)

    @GetMapping("/{id}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun observe(@PathVariable id: Long) = DataRepository.observeItem(id)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody patch: ItemPatch) = DataRepository.updateItem(id, patch)

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