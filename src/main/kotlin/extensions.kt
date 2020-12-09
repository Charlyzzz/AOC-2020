import java.util.*

fun Any.print() {
    println(this)
}

fun <T> emptyQueue(elems: Collection<T>? = null): Queue<T> =
    if (elems.isNullOrEmpty()) {
        LinkedList()
    } else {
        LinkedList(elems)
    }
