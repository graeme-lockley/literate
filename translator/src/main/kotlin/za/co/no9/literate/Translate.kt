package za.co.no9.literate

import freemarker.template.Template
import java.io.StringWriter


class ParseException(val position: Position, message: String) : Exception(message)

class ProcessException(message: String) : java.lang.Exception(message)


typealias Chunks =
        Map<String, List<ChunkLine>>


data class TranslateResult(val markdown: String, val items: List<Pair<String, String>>) {
    fun addItem(key: String, value: String): TranslateResult =
            TranslateResult(markdown, items + Pair(key, value))
}


fun translate(input: String): Result<Exception, TranslateResult> =
        parse(input.lines())
                .andThen { listOfLines ->
                    extractChunks(listOfLines)
                            .andThen { chunks ->
                                val initialValue: Result<Exception, TranslateResult> =
                                        Okay(TranslateResult(createMarkdown(listOfLines), emptyList()))

                                listOfLines.fold(initialValue) { result, line ->
                                    if (line is ChunkLine && line.hasArgument("file")) {
                                        result.andThen { translationResult ->
                                            processChunks(line, chunks)
                                                    .map { value ->
                                                        translationResult.addItem(line.argumentValue("file")!!, value)
                                                    }
                                        }
                                    } else
                                        result
                                }
                            }
                }


private fun createMarkdown(lines: List<Line>): String =
        lines.joinToString("\n") { b ->
            when (b) {
                is TextLine ->
                    b.content

                is ChunkLine ->
                    if (b.argumentValue("weave") ?: "True" == "True") {
                        "~~~ haskell\n" +
                                b.content +
                                "\n" +
                                "~~~"
                    } else
                        ""
                else ->
                    ""
            }
        }


fun processChunks(chunk: ChunkLine, chunks: Chunks): Result<Exception, String> {
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
        parse(input.lines())
                .andThen { extractChunks(it) }


fun extractChunks(root: List<Line>): Result<Exception, Chunks> =
        Okay(root.fold(mapOf()) { result, line ->
            when (line) {
                is ChunkLine ->
                    result + Pair(line.name, (result[line.name]
                            ?: emptyList()) + line)

                else ->
                    result
            }
        })


fun parse(lines: List<String>): Result<Exception, List<Line>> {
    val result =
            mutableListOf<Line>()

    var lp =
            0

    while (lp < lines.size) {
        if (lines[lp].startsWith("~~~")) {
            val line =
                    lines[lp].substring(3)

            val lexer =
                    Lexer(line)

            val parseLineResult =
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

            result.add(ChunkLine(content.toString(), parseLineResult.name, parseLineResult.additive, parseLineResult.arguments, startLineNumber, lp - 1))

            lp += 1
        } else {
            result.add(TextLine(lines[lp], lp))
            lp += 1
        }
    }

    return Okay(result)
}


data class ParseLineResult(val additive: Boolean, val name: String, val arguments: List<Argument>)


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
private fun parseLine(lexer: Lexer): ParseLineResult {
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
            ParseLineResult(additive, id.text, arguments)
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
                lexer.next().text
                        .drop(1)
                        .dropLast(1)
                        .replace("\\n", "\n")
                        .replace("\\\\", "\\")

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