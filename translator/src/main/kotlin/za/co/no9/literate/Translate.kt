package za.co.no9.literate

import freemarker.template.Template
import java.io.StringWriter


data class Argument(val name: String, val value: String)

data class Chunk(val content: String, val add: Boolean, val arguments: List<Argument>)


class ParseException(val position: Position, message: String) : Exception(message)


fun extractChunks(input: String): Result<Exception, Map<String, List<Chunk>>> {
    val lines =
            input.lines()

    val result =
            mutableMapOf<String, List<Chunk>>()

    var lp = 0
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

            val content =
                    StringBuilder()

            lp += 1
            while (lp < lines.size && !lines[lp].startsWith("~~~")) {
                if (!content.isEmpty()) {
                    content.append("\n")
                }
                content.append(lines[lp])
                lp += 1
            }

            val currentChunk =
                    result[parseResult.text]

            if (currentChunk == null)
                result[parseResult.text] =
                        listOf(Chunk(content.toString(), false, emptyList()))
            else
                result[parseResult.text] =
                        currentChunk + Chunk(content.toString(), false, emptyList())

            lp += 1
        } else {
            lp += 1
        }
    }
    return Okay(result)
}


/*
    line :==
        ID EOF
 */
private fun parseLine(lexer: Lexer): Symbol =
        if (lexer.token == Token.ID) {
            val id =
                    lexer.next()

            if (lexer.token == Token.EOF) {
                id
            } else {
                throw ParseException(lexer.position(), "Expected EOF")
            }
        } else {
            throw ParseException(lexer.position(), "Expected ID")
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