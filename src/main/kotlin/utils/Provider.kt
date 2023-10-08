package dev.limebeck.templater.cli.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Provider<T>(
    initValueProvider: () -> T
) : ReadOnlyProperty<Any?, T> {
    private val stateFlow = MutableStateFlow(initValueProvider())
    private val coroutineScope = CoroutineScope(SupervisorJob())

    fun get(): T = stateFlow.value

    suspend fun setValue(value: T) {
        stateFlow.emit(value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

    fun subscribe(): StateFlow<T> = stateFlow.asStateFlow()

    fun <R> map(mapper: (T) -> R): Provider<R> {
        val newProvider = Provider { mapper(this.get()) }
        coroutineScope.launch {
            subscribe().collect {
                newProvider.setValue(mapper(it))
            }
        }
        return newProvider
    }
}

fun <T> providerOf(provider: () -> T): Provider<T> = Provider(provider)
