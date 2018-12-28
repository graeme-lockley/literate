package za.co.no9.literate


abstract class ALine(open var next: ALine?, open val content: String)


class TextLine(
        override var next: ALine?,
        override val content: String,
        val lineNumber: Int) : ALine(next, content)

class ChunkLine(
        override var next: ALine?,
        override val content: String,
        val name: String,
        val additive: Boolean,
        val arguments: List<Argument>,
        val startLineNumber: Int,
        val endLineNumber: Int) : ALine(next, content)