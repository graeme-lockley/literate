package za.co.no9.literate


enum class LineState {
    Text,
    ChunkStart,
    InChunk,
    ChunkEnd
}


data class ChunkStartParameters(val name: String, val additive: Boolean, val arguments: List<Argument>)


class ALine(var next: ALine?, val content: String, val state: LineState, val argument: ChunkStartParameters?)