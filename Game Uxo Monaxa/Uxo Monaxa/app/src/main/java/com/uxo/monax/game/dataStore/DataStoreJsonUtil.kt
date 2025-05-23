package com.uxo.monax.game.dataStore

import com.uxo.monax.game.manager.AbstractDataStore
import com.uxo.monax.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

abstract class DataStoreJsonUtil<T>(
    private val serializer  : KSerializer<T>,
    private val deserializer: DeserializationStrategy<T>
) {
    val simpleName: String get() = this::class.java.simpleName

    abstract val coroutine: CoroutineScope
    abstract val flow     : MutableStateFlow<T>
    abstract val dataStore: AbstractDataStore.DataStoreElement<String>

    open fun initialize() {
        coroutine.launch(Dispatchers.IO) {
            dataStore.get()?.let { value -> flow.value = Json.decodeFromString(deserializer, value) }
            log("Store $simpleName = ${flow.value}")
        }
    }

    open fun update(block: (T) -> T) {
        coroutine.launch(Dispatchers.IO) {
            flow.value = block(flow.value)

            log("Store $simpleName update = ${flow.value}")
            dataStore.update { Json.encodeToString(serializer, flow.value) }
        }
    }
}
