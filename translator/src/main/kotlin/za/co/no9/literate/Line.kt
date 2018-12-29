package za.co.no9.literate


abstract class ConsList<X> {
    abstract fun <R> foldLeft(z: R, f: (R, X) -> R): R

    abstract fun append(element: X): ConsList<X>
}


class Cons<X>(val car: X, val cdr: ConsList<X>) : ConsList<X>() {
    override fun <R> foldLeft(z: R, f: (R, X) -> R): R =
            this.cdr.foldLeft(f(z, this.car), f)

    override fun append(element: X): ConsList<X> =
            Cons(car, cdr.append(element))
}


class Nil<X> : ConsList<X>() {
    override fun <R> foldLeft(z: R, f: (R, X) -> R): R =
            z

    override fun append(element: X): ConsList<X> =
            Cons(element, this)
}


abstract class Line(open val content: String)


class TextLine(
        override val content: String,
        val lineNumber: Int) : Line(content)

class ChunkLine(
        override val content: String,
        val name: String,
        val additive: Boolean,
        val arguments: List<Argument>,
        val startLineNumber: Int,
        val endLineNumber: Int) : Line(content)