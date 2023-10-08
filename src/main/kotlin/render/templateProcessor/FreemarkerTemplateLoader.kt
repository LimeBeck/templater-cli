package dev.limebeck.templater.cli.render.templateProcessor

import freemarker.cache.TemplateLoader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.io.Reader

/**
 * Загрузчик шаблонов с помощью ресурсов из TemplateMeta
 */
abstract class FreemarkerTemplateLoader : TemplateLoader {
    enum class TemplateType(val prefix: String) {
        CONTENT("content:"),
        RESOURCE("resource:")
    }

    override fun getReader(templateSource: Any?, encoding: String?): Reader {
        return InputStreamReader(ByteArrayInputStream(templateSource as ByteArray))
    }

    /**
     * Метод для получения шаблона по его имени (без префикса, указываемого в имени)
     */
    abstract fun loadTemplate(nameWithoutPrefix: String, type: TemplateType): ByteArray

    override fun findTemplateSource(name: String): ByteArray {
        val templateType = TemplateType.entries.find {
            name.startsWith(it.prefix)
        } ?: TemplateType.RESOURCE

        return loadTemplate(name.removePrefix(templateType.prefix), templateType)
    }
}
