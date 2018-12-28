package za.co.no9.literate


abstract class Result<S, T> {
    abstract fun <U> andThen(process: (T) -> Result<S, U>): Result<S, U>

    abstract fun <U> map(process: (T) -> U): Result<S, U>
}


data class Okay<S, T>(val value: T) : Result<S, T>() {
    override fun <U> andThen(process: (T) -> Result<S, U>): Result<S, U> =
            process(value)

    override fun <U> map(process: (T) -> U): Result<S, U> =
            Okay(process(value))
}


data class Error<S, T>(val error: S) : Result<S, T>() {
    override fun <U> andThen(process: (T) -> Result<S, U>): Result<S, U> =
            Error(error)

    override fun <U> map(process: (T) -> U): Result<S, U> =
            Error(error)
}