package render.templateProcessor.methods

import dev.limebeck.templater.cli.exceptions.MissingParameterException
import dev.limebeck.templater.cli.render.resources.ResourceLoader
import dev.limebeck.templater.cli.render.resources.encodeToBase64
import freemarker.template.TemplateMethodModelEx

class GetResourceBase64Method(
    private val resourceLoader: ResourceLoader
) : TemplateMethodModelEx {
    override fun exec(arguments: List<Any?>?): String {
        val value = arguments?.get(0)?.toString()
            ?: throw MissingParameterException("<401aa384> File name not found")
        val resource = resourceLoader.loadBinary(value)
        return resource.encodeToBase64()
    }
}
