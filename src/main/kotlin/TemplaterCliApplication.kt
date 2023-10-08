package dev.limebeck.templater.cli

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.limebeck.templater.cli.render.ServeCommand

fun main(args: Array<String>) = TemplaterCliApplication()
    .subcommands(ServeCommand())
    .completionOption()
    .main(args)

class TemplaterCliApplication : CliktCommand(
    printHelpOnEmptyArgs = true,
    help = """
        Limebeck`s templater cli application
    """.trimIndent()
) {
    override fun run() = Unit
}