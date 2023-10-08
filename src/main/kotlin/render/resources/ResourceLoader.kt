package dev.limebeck.templater.cli.render.resources

import java.util.*

interface ResourceLoader {
    fun loadBinary(resourcePath: String): BinaryResource
    fun loadUrl(resourcePath: String): String
}

data class BinaryResource(
    val data: ByteArray,
    val contentType: String
)

fun BinaryResource.encodeToBase64(): String {
    return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(data)
}