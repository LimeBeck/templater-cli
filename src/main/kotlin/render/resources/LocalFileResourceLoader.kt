package dev.limebeck.templater.cli.render.resources

import dev.limebeck.templater.cli.exceptions.ObjectNotFoundException
import dev.limebeck.templater.cli.utils.Provider
import org.apache.tika.Tika
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class LocalFileResourceLoader(
    urlPath: String,
    val resourcesBaseDir: Provider<File>
) : ResourceLoader {
    private val urlPath = urlPath.trimEnd('/')
    private val tika = Tika()

    override fun loadBinary(resourcePath: String): BinaryResource {
        val filePath = resourcesBaseDir.get().resolve(resourcePath)
        try {
            if (!filePath.isFile) {
                throw ObjectNotFoundException("<49bcc8f9> File with path=${filePath.canonicalPath} not found")
            }
            val contentType = tika.detect(filePath)
            FileInputStream(filePath).use { stream ->
                val byteArray = ByteArray(stream.available())
                stream.read(byteArray)
                return BinaryResource(
                    data = byteArray,
                    contentType = contentType
                )
            }
        } catch (e: FileNotFoundException) {
            throw ObjectNotFoundException("<ca2b4250> File with path=${filePath.canonicalPath} not found")
        }
    }

    override fun loadUrl(resourcePath: String): String {
        return "$urlPath/${resourcePath.trimStart('/', '.')}"
    }
}
