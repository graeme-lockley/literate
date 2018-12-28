package za.co.no9.literate


enum class Token {
    EOF,

    ERROR,

    ConstantChar,
    ConstantInt,
    ConstantString,

    ID,

    Other
}

const val EOS =
        0.toChar()


data class Symbol(val token: Token, val location: Location, val text: String) {
    val column: Int
        get() = location.start.column
}


class Lexer(private val input: String) {
    var currentSymbol =
            Symbol(Token.EOF, Location(Position(0, 0), Position(0, 0)), "")
        private set

    val token: Token
        get() = this.currentSymbol.token

    val text: String
        get() = this.currentSymbol.text

    val column: Int
        get() = this.currentSymbol.location.start.column


    private val inputLength =
            input.length

    private var currentIndex =
            -1

    private var currentLine =
            1

    private var currentColumn =
            0

    private var currentCh =
            ' '

    private var nextCh =
            ' '

    init {
        skip()
    }

    fun next(): Symbol {
        val current =
                currentSymbol

        skip()

        return current
    }


    fun skip() {
        try {
            nextCharacter()

            while (currentCh.isWhitespace()) {
                nextCharacter()
            }

            when {
                currentCh in 'a'..'z' || currentCh in 'A'..'Z' -> {
                    val startPosition =
                            position()

                    val startIndex =
                            currentIndex

                    while (nextCh.isLetterOrDigit() || nextCh == '_') {
                        nextCharacter()
                    }
                    while (nextCh == '\'') {
                        nextCharacter()
                    }

                    val endPosition =
                            position()

                    val endIndex =
                            currentIndex

                    val text =
                            input.substring(startIndex, endIndex + 1)

                    currentSymbol =
                            Symbol(Token.ID, Location(startPosition, endPosition), text)
                }

                currentCh in '0'..'9' -> {
                    val startPosition =
                            position()

                    val startIndex =
                            currentIndex

                    while (nextCh.isDigit()) {
                        nextCharacter()
                    }
                    markEnd(startIndex, startPosition, Token.ConstantInt)
                }

                currentCh == '"' -> {
                    val startPosition =
                            position()

                    val startIndex =
                            currentIndex

                    nextCharacter()
                    while (true) {
                        if (currentCh == '"') {
                            break
                        } else if (currentCh == '\\') {
                            nextCharacter()
                            nextCharacter()
                        } else {
                            nextCharacter()
                        }
                    }
                    markEnd(startIndex, startPosition, Token.ConstantString)
                }

                currentCh == '\'' -> {
                    val startPosition =
                            position()

                    val startIndex =
                            currentIndex

                    nextCharacter()
                    if (currentCh == '\\') {
                        nextCharacter()
                        nextCharacter()
                    } else {
                        nextCharacter()
                    }

                    if (currentCh == '\'')
                        markEnd(startIndex, startPosition, Token.ConstantChar)
                    else
                        markEnd(startIndex, startPosition, Token.ERROR)
                }

                else ->
                    markEnd(currentIndex, position(), Token.Other)
            }
        } catch (e: StringIndexOutOfBoundsException) {
            currentSymbol = Symbol(Token.EOF, location(), "")
        }
    }


    private fun markEnd(startIndex: Int, startPosition: Position, token: Token) {
        val endPosition =
                position()

        val endIndex =
                currentIndex

        val text =
                input.substring(startIndex, endIndex + 1)

        currentSymbol =
                Symbol(token, Location(startPosition, endPosition), text)
    }


    private fun nextCharacter() {
        if (currentIndex < inputLength) {
            currentIndex += 1
            currentCh = input[currentIndex]
            nextCh = if (currentIndex + 1 < inputLength) input[currentIndex + 1] else ' '

            if (currentCh == '\n') {
                currentColumn = 0
                currentLine += 1
            } else if (currentCh != '\r') {
                currentColumn += 1
            }
        } else {
            currentCh = EOS
        }
    }

    fun location(): Location =
            Location(position())


    fun position(): Position =
            Position(currentLine, currentColumn)
}