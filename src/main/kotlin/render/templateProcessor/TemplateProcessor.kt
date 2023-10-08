package dev.limebeck.templater.cli.render.templateProcessor

interface TemplateProcessor {
    fun process(entrypoint: String, data: Map<String, Any?>): String
}
