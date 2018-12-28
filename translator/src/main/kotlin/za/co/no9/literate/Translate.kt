package za.co.no9.literate

import freemarker.template.Template
import java.io.StringWriter


data class Argument(val name: String, val value: String)

data class Chunk(val content: String, val add: Boolean, val arguments: List<Argument>)


class ParseException(val position: Position, message: String) : Exception(message)

class ProcessException(message: String) : java.lang.Exception(message)


typealias Chunks =
        Map<String, List<Chunk>>


data class TranslateResult(val markdown: String, val items: List<Pair<String, String>>)


fun processChunks(chunk: Chunk, chunks: Chunks): Result<Exception, String> {
    var text =
            chunk.content

    while (true) {
        val startIndex =
                text.indexOf("[=")

        if (startIndex != -1) {
            val endIndex =
                    text.indexOf("]", startIndex)

            if (endIndex != -1) {
                val line =
                        parseLine(Lexer(text.substring(startIndex + 2, endIndex)))

                val chunkItems =
                        chunks[line.name]

                if (chunkItems == null) {
                    throw ProcessException("Reference to unknown chunk ${line.name}")
                } else {
                    val separator =
                            line.arguments.firstOrNull { it.name == "separator" }?.value ?: ""

                    text = text.substring(0, startIndex) + chunkItems.map { it.content }.joinToString(separator) + text.substring(endIndex + 1)
                }

            } else {
                return Okay(text)
            }
        } else {
            return Okay(text)
        }
    }
}


fun extractChunks(input: String) =
        extractChunks(input.lines())


fun extractChunks(lines: List<String>): Result<Exception, Chunks> =
        parse(lines)
                .map { root ->
                    val result =
                            mutableMapOf<String, List<Chunk>>()

                    var current: ALine? =
                            root

                    while (current != null) {

                        if (current is ChunkLine) {
                            val currentChunk =
                                    result[current.name]

                            if (currentChunk == null)
                                result[current.name] =
                                        listOf(Chunk(current.content, current.additive, current.arguments))
                            else
                                result[current.name] =
                                        currentChunk + Chunk(current.content, current.additive, current.arguments)

                        }

                        current = current.next
                    }

                    result
                }


fun parse(lines: List<String>): Result<Exception, ALine> {
    var root: ALine? =
            null

    var current: ALine? =
            null


    fun addLine(line: ALine) {
        if (root == null) {
            root = line
        }
        if (current == null) {
            current = null
        } else {
            current!!.next = line
        }
        current = line
    }


    var lp =
            0

    while (lp < lines.size) {
        if (lines[lp].startsWith("~~~")) {
            val line =
                    lines[lp].substring(3)

            val lexer =
                    Lexer(line)

            val parseResult =
                    try {
                        parseLine(lexer)
                    } catch (e: ParseException) {
                        return Error(ParseException(Position(e.position.line + lp, e.position.column), e.message!!))
                    }

            lp += 1

            val startLineNumber =
                    lp

            val content =
                    StringBuffer()

            while (lp < lines.size && !lines[lp].startsWith("~~~")) {
                if (!content.isEmpty()) {
                    content.append("\n")
                }
                content.append(lines[lp])
                lp += 1
            }

            addLine(ChunkLine(null, content.toString(), parseResult.name, parseResult.additive, parseResult.arguments, startLineNumber, lp))

            lp += 1
        } else {
            addLine(TextLine(null, lines[lp], lp))
            lp += 1
        }
    }

    return Okay(root!!)
}


data class Line(val additive: Boolean, val name: String, val arguments: List<Argument>)


/*
    line :==
        ( '+' )? ID ( argument )* EOF

    argument :==
        ID '=' value

    value :==
        ID
      | CONSTANT_INT
      | CONSTANT_STRING
 */
private fun parseLine(lexer: Lexer): Line {
    val additive =
            if (lexer.token == Token.PLUS) {
                lexer.next()
                true
            } else {
                false
            }

    return if (lexer.token == Token.ID) {
        val id =
                lexer.next()

        val arguments =
                mutableListOf<Argument>()

        while (lexer.token == Token.ID) {
            arguments.add(parseArgument(lexer))
        }

        if (lexer.token == Token.EOF) {
            Line(additive, id.text, arguments)
        } else {
            throw ParseException(lexer.position(), "Expected EOF")
        }
    } else {
        throw ParseException(lexer.position(), "Expected ID")
    }
}


private fun parseArgument(lexer: Lexer): Argument {
    if (lexer.token == Token.ID) {
        val id =
                lexer.next()

        if (lexer.token == Token.EQUAL) {
            lexer.skip()
        } else {
            throw ParseException(lexer.position(), "Expected '='")
        }
        val value =
                parseValue(lexer)

        return Argument(id.text, value)
    } else {
        throw ParseException(lexer.position(), "Expected ID")
    }
}


private fun parseValue(lexer: Lexer): String =
        when (lexer.token) {
            Token.ConstantInt ->
                lexer.next().text

            Token.ConstantString ->
                lexer.next().text.drop(1).dropLast(1)

            Token.ID ->
                lexer.next().text

            else ->
                throw ParseException(lexer.position(), "Expected constant string, constant int or ID")
        }


fun processTemplate(state: Map<String, Any>, template: Template): Result<Exception, String> {
    val output =
            StringWriter()

    return try {
        template.process(state, output)

        Okay(output.toString())
    } catch (e: Exception) {
        Error(e)
    }
}