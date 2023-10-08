package dev.limebeck.templater.cli.render.templateProcessor

import dev.limebeck.templater.cli.exceptions.ObjectNotFoundException
import dev.limebeck.templater.cli.render.resources.ResourceLoader
import dev.limebeck.templater.cli.utils.Provider
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

/**
 * Загрузчик шаблонов из ресурсов напрямую с локальной файловой системы
 */
class PlainFileTemplateLoader(
    private val baseDir: Provider<File>,
    private val resourceLoader: ResourceLoader
) : FreemarkerTemplateLoader() {
    override fun closeTemplateSource(templateSource: Any?) {}

    override fun getLastModified(templateSource: Any?): Long {
        // FIXME: Implement real last modified time
        return System.currentTimeMillis()
    }

    override fun loadTemplate(nameWithoutPrefix: String, type: TemplateType): ByteArray {
        when (type) {
            TemplateType.CONTENT -> {
                val filePath = File("${baseDir.get()}/$nameWithoutPrefix")

                try {
                    return FileInputStream(filePath).use { stream ->
                        val byteArray = ByteArray(stream.available())
                        stream.read(byteArray)
                        byteArray
                    }
                } catch (e: FileNotFoundException) {
                    throw ObjectNotFoundException("<5de49788> File with path=${filePath.canonicalPath} not found")
                }
            }

            TemplateType.RESOURCE -> {
                return resourceLoader.loadBinary(nameWithoutPrefix).data
            }
        }
    }
}
