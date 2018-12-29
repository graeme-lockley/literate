package za.co.no9.literate


abstract class Line(open val content: String)


data class TextLine(
        override val content: String,
        val lineNumber: Int) : Line(content)

data class ChunkLine(
        override val content: String,
        val name: String,
        val additive: Boolean,
        val arguments: List<Argument>,
        val startLineNumber: Int,
        val endLineNumber: Int) : Line(content) {
    fun hasArgument(name: String): Boolean =
            arguments.any { it.name == name }

    fun argumentValue(name: String): String? =
            arguments.firstOrNull { it.name == name }?.value
}