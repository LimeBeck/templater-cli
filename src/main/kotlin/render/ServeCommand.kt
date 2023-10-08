package dev.limebeck.templater.cli.render

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import dev.limebeck.templater.cli.render.server.Config
import dev.limebeck.templater.cli.render.server.ServerConfig
import dev.limebeck.templater.cli.render.server.runServer
import java.io.File

class ServeCommand : CliktCommand(
    name = "serve",
    help = """
        Serve template in browser with live-reloading
    """.trimIndent()
) {
    private val port: Int by option("-p", help = "Port").int().default(8080)
    private val host: String by option("-h", help = "Host").default("0.0.0.0")
    private val templateDescFile: File by option("-t", help = "template_desc file")
        .file(canBeDir = false, mustBeReadable = true)
        .default(File("./template_desc.json"))
    private val dataFile: File by option("-d", "--data", help = "example data json file")
        .file(canBeDir = false, mustBeReadable = true)
        .default(File("./data.json"))

    override fun run(): Unit = runServer(
        Config(
            server = ServerConfig(host, port),
            templateDescFile = templateDescFile,
            dataFile = dataFile
        )
    )
}
