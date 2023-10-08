package dev.limebeck.templater.cli.render.server

import dev.limebeck.templater.cli.TemplaterCliConfig
import dev.limebeck.templater.cli.printToConsole
import dev.limebeck.templater.cli.utils.providerOf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.io.File
import java.net.URI
import kotlin.time.TimeSource


data class Config(
    val server: ServerConfig,
    val templateDescFile: File,
    val dataFile: File
)

data class ServerConfig(
    val host: String = "0.0.0.0",
    val port: Int = 8080,
)

const val STATIC_URL_PATH = "/static"
val serverLogger: Logger = LoggerFactory.getLogger("ServerLogger")

fun runServer(config: Config) {
    println("Starting application...")
    val startTime = TimeSource.Monotonic.markNow()

    val contextDir = providerOf { config.templateDescFile.parent.let(::File) }

    val renderedTemplateStateFlow = createRenderStateFlow(config, contextDir)

    embeddedServer(CIO, environment = applicationEngineEnvironment {
        connector {
            host = config.server.host
            port = config.server.port
        }

        module {
            install(StatusPages) {
                exception<NotFoundException> { call, cause ->
                    serverLogger.error("<2f75b6c6> Page ${call.request.uri} not found", cause)
                    call.respondText(cause.asHtml(), ContentType.Text.Html, status = HttpStatusCode.NotFound)
                }

                exception<Throwable> { call, cause ->
                    serverLogger.error("<2c1b0315> Internal error at ${call.request.uri}", cause)
                    call.respondText(cause.asHtml(), ContentType.Text.Html)
                }
            }

            routing {
                renderRouting(contextDir, renderedTemplateStateFlow)
            }

            environment.monitor.subscribe(ApplicationStarted) {
                val url = "http://${config.server.host}:${config.server.port}"

                """
                    Templater Local Server started at $url
                    Start duration: ${startTime.elapsedNow()}
                    Application version: ${TemplaterCliConfig.version}
                    Built at ${TemplaterCliConfig.buildTime}
                """.trimIndent().printToConsole(minRowLength = 60)

                try {
                    if (Desktop.isDesktopSupported()) {
                        val desktop = Desktop.getDesktop()
                        desktop.browse(URI.create(url))
                    }
                } catch (t: Throwable) {
                    // Ignore
                }
            }
        }
    }).start(wait = true)
}
