package dev.limebeck.templater.cli.render.templateProcessor.methods

import dev.limebeck.templater.cli.exceptions.MissingParameterException
import dev.limebeck.templater.cli.render.resources.ResourceLoader
import freemarker.template.TemplateMethodModelEx

/**
 * Получить URL ресурса по его имени
 */
class GetResourceURLMethod(
    private val resourceLoader: ResourceLoader
) : TemplateMethodModelEx {
    override fun exec(arguments: MutableList<Any?>?): String {
        val value = arguments?.get(0)?.toString()
            ?: throw MissingParameterException("<d93da2f3> File name not found")
        return resourceLoader.loadUrl(value)
    }
}
