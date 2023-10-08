package dev.limebeck.templater.cli.render.templateProcessor

import freemarker.template.Configuration
import freemarker.template.TemplateMethodModelEx
import java.io.StringWriter

class FreemarkerTemplateProcessor(
    private val templateLoader: FreemarkerTemplateLoader,
    private val methods: Map<String, TemplateMethodModelEx>
) : TemplateProcessor {

    override fun process(entrypoint: String, data: Map<String, Any?>): String {
        val cfg = Configuration(Configuration.VERSION_2_3_31).apply {
            templateLoader = this@FreemarkerTemplateProcessor.templateLoader
            defaultEncoding = "UTF-8"
            localizedLookup = false
            methods.forEach { (name, method) ->
                setSharedVariable(name, method)
            }
        }

        val template = cfg.getTemplate("${FreemarkerTemplateLoader.TemplateType.CONTENT.prefix}$entrypoint")
        return StringWriter().use {
            template.process(data, it)
            it.toString()
        }
    }
}
