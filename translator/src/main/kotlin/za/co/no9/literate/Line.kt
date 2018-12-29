package za.co.no9.literate


abstract class ConsList<X> {
    fun <R> foldLeft(z: R, f: (R, X) -> R): R =
            when {
                this is Cons<X> ->
                    this.cdr.foldLeft(f(z, this.car), f)

                else ->
                    z
            }

    abstract fun append(element: X): ConsList<X>
}


class Cons<X>(val car: X, val cdr: ConsList<X>) : ConsList<X>() {
    override fun append(element: X): ConsList<X> =
            Cons(car, cdr.append(element))
}


class Nil<X> : ConsList<X>() {
    override fun append(element: X): ConsList<X> =
            Cons(element, this)
}


abstract class ALine(open val content: String)


class TextLine(
        override val content: String,
        val lineNumber: Int) : ALine(content)

class ChunkLine(
        override val content: String,
        val name: String,
        val additive: Boolean,
        val arguments: List<Argument>,
        val startLineNumber: Int,
        val endLineNumber: Int) : ALine(content)