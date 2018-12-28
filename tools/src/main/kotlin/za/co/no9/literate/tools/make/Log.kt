package za.co.no9.literate.tools.make

interface Log {
    fun error(message: String)

    fun info(message: String)
}