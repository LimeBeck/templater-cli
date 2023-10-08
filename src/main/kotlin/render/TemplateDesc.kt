package dev.limebeck.templater.cli.render

import kotlinx.serialization.Serializable

@Serializable
data class TemplateDesc(
    val identifier: String,
    val description: String,
    val entrypoint: String,
    val type: String,
    val tags: Set<String>
)
