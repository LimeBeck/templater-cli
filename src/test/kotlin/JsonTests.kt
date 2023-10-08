import dev.limebeck.templater.cli.render.TemplateDesc
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonTests {
    @Test
    fun `Decode template desc`() {
        @Language("Json")
        val templateDescExamplar = """
            {
              "identifier": "my-shiny-template",
              "description": "My super shiny template",
              "entrypoint": "index.html.ftl",
              "type": "freemarker",
              "tags": [
                ""
              ],
              "resourcesPath": "./assets"
            }
        """.trimIndent()

        val expected = TemplateDesc(
            identifier = "my-shiny-template",
            description = "My super shiny template",
            entrypoint = "index.html.ftl",
            type = "freemarker",
            tags = setOf(""),
            resourcesPath = "./assets"
        )

        val desc = Json.decodeFromString<TemplateDesc>(templateDescExamplar)
        assertEquals(expected, desc)
    }
}