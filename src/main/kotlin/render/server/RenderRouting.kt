package dev.limebeck.templater.cli.render.server

import dev.limebeck.templater.cli.utils.Provider
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.logging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import java.io.File
import java.util.*

fun Routing.renderRouting(
    contextDir: Provider<File>,
    renderedTemplateStateFlow: StateFlow<String>
) {
    get("/static/{assetName...}") {
        val assetName = call.parameters.getAll("assetName")!!
        serverLogger.debug("<4f153957> Get asset {}", assetName)
        val file = contextDir.get().resolve(assetName.joinToString("/"))
        call.respondFile(file)
    }

    get("/") {
        val renderResult = renderedTemplateStateFlow.value.appendSseReloadScript()
        call.respondText(renderResult, ContentType.Text.Html)
    }

    get("/sse") {
        serverLogger.debug("<967c07f Subscribed with ${this.call.request.toLogString()}")
        val events = renderedTemplateStateFlow
            .drop(1)
            .map { SseEvent(data = it, event = "PageUpdated", id = UUID.randomUUID().toString()) }
            .produceIn(this)

        try {
            call.respondSse(events)
        } catch (e: CancellationException) {
        }
        finally {
            events.cancel()
            this.finish()
        }
    }
}
