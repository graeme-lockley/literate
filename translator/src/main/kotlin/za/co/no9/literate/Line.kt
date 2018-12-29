package za.co.no9.literate


abstract class ConsList<X> {
    abstract fun <R> foldLeft(z: R, f: (R, X) -> R): R

    abstract fun append(element: X): ConsList<X>

    abstract fun <E, R> rmap(f: (X) -> Result<E, R>): Result<E, ConsList<R>>
}


class Cons<X>(val car: X, val cdr: ConsList<X>) : ConsList<X>() {
    override fun <R> foldLeft(z: R, f: (R, X) -> R): R =
            this.cdr.foldLeft(f(z, this.car), f)

    override fun append(element: X): ConsList<X> =
            Cons(car, cdr.append(element))

    override fun <E, R> rmap(f: (X) -> Result<E, R>): Result<E, ConsList<R>> =
            this.cdr.rmap(f)
                    .andThen { cdr ->
                        f(this.car)
                                .andThen { car -> Okay<E, ConsList<R>>(Cons(car, cdr)) }
                    }
}


class Nil<X> : ConsList<X>() {
    override fun <R> foldLeft(z: R, f: (R, X) -> R): R =
            z

    override fun append(element: X): ConsList<X> =
            Cons(element, this)

    override fun <E, R> rmap(f: (X) -> Result<E, R>): Result<E, ConsList<R>> =
            Okay(Nil())
}


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