//package com.uxo.monax.game.dataStore
//
//import com.liberator.wisoliter.game.manager.DataStoreManager
//import com.liberator.wisoliter.game.utils.ITEM_COUNT
//import com.uxo.monax.game.manager.DataStoreManager
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.serialization.builtins.ListSerializer
//import kotlinx.serialization.builtins.serializer
//
//class DS_TestJson(override val coroutine: CoroutineScope): DataStoreJsonUtil<List<Int>>(
//    serializer   = ListSerializer(Int.serializer()),
//    deserializer = ListSerializer(Int.serializer()),
//) {
//
//    override val dataStore = DataStoreManager.ItemCount
//
//    override val flow = MutableStateFlow(List(ITEM_COUNT) { 0 })
//
//    init { initialize() }
//
//}