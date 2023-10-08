package dev.limebeck.templater.cli.render.server

import dev.limebeck.application.filesWatcher.UpdatedFile
import dev.limebeck.application.filesWatcher.watchFilesRecursive
import dev.limebeck.templater.cli.render.TemplateDesc
import dev.limebeck.templater.cli.render.resources.LocalFileResourceLoader
import dev.limebeck.templater.cli.render.templateProcessor.FreemarkerTemplateProcessor
import dev.limebeck.templater.cli.render.templateProcessor.PlainFileTemplateLoader
import dev.limebeck.templater.cli.render.templateProcessor.methods.GetResourceURLMethod
import dev.limebeck.templater.cli.utils.Provider
import dev.limebeck.templater.cli.utils.convertJson
import dev.limebeck.templater.cli.utils.providerOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import render.templateProcessor.methods.GetResourceBase64Method
import java.io.File
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

fun createRenderStateFlow(config: Config, contextDir: Provider<File>): StateFlow<String> {
    val coroutineScope = CoroutineScope(SupervisorJob())

    val templateDesc = providerOf {
        val stringData = config.templateDescFile.readText()
        Json.decodeFromString<TemplateDesc>(stringData)
    }

    val exampleData = providerOf {
        val stringData = config.dataFile.readText()
        Json.decodeFromString<JsonObject>(stringData).convertJson()
    }

    val resourceLoader = LocalFileResourceLoader(
        urlPath = STATIC_URL_PATH,
        resourcesBaseDir = contextDir
    )

    val processor = FreemarkerTemplateProcessor(
        templateLoader = PlainFileTemplateLoader(
            baseDir = contextDir,
            resourceLoader = resourceLoader
        ),
        methods = mapOf(
            "getResourceBase64" to GetResourceBase64Method(resourceLoader),
            "getResourceUrl" to GetResourceURLMethod(resourceLoader)
        )
    )

    val firstLoadResult: TimedValue<String> = measureTimedValue {
        runCatching {
            processor.process(templateDesc.get().entrypoint, exampleData.get())
        }.getOrElse { it.asHtml() }
    }

    serverLogger.info("First render took ${firstLoadResult.duration}")

    val updatedFilesStateFlow = MutableStateFlow<List<UpdatedFile>?>(null)
    val renderedTemplateStateFlow = MutableStateFlow<String>(firstLoadResult.value)

    coroutineScope.launch {
        watchFilesRecursive(config.templateDescFile.parent) {
            serverLogger.debug("<5623d47b> Files changed: {}", it)
            coroutineScope.launch {
                updatedFilesStateFlow.emit(it)
            }
        }
    }

    coroutineScope.launch {
        updatedFilesStateFlow.collect { sf ->
            if (sf != null) {
                if (sf.any { it.path.contains(config.templateDescFile.name) }) {
                    val stringData = config.templateDescFile.readText()
                    templateDesc.setValue(Json.decodeFromString(stringData))
                }

                if (sf.any { it.path.contains(config.dataFile.name) }) {
                    val stringData = config.dataFile.readText()
                    exampleData.setValue(Json.decodeFromString<JsonObject>(stringData).convertJson())
                }

                val result = measureTimedValue {
                    runCatching {
                        processor.process(templateDesc.get().entrypoint, exampleData.get())
                    }
                }

                serverLogger.info("<81ddab51> Render time: ${result.duration}")
                renderedTemplateStateFlow.emit(result.value.getOrElse { it.asHtml() })
            }
        }
    }

    return renderedTemplateStateFlow
}
