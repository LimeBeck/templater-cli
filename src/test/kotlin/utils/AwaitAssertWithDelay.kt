package utils

import kotlinx.coroutines.delay
import kotlin.time.TimeSource

suspend fun awaitAssertWithDelay(
    timeoutMillis: Long = 500,
    delayMillis: Long = 100,
    assertion: () -> Unit
) {
    val startTime = TimeSource.Monotonic.markNow()
    while (startTime.elapsedNow().inWholeMilliseconds <= timeoutMillis) {
        try {
            assertion()
            return
        } catch (t: AssertionError) {
            println("Skip $t")
            delay(delayMillis)
        }
    }
    assertion()
}
