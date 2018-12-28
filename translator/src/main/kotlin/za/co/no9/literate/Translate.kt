package za.co.no9.literate

import freemarker.template.Template
import java.io.StringWriter


data class SourceOutput(
        val name: String,
        val content: String)


data class Translation(
        val markdown: String,
        val output: List<SourceOutput>
)


fun translate(input: String): Result<String, Translation> {
    return Okay(Translation("", listOf()))
}



fun processTemplate(state : Map<String, Any>, template: Template): Result<Exception, String> {
    val output =
            StringWriter()

    return try {
        template.process(state, output)

        Okay(output.toString())
    } catch(e: Exception) {
        Error(e)
    }
}