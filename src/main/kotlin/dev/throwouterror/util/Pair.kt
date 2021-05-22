package dev.throwouterror.util

class Pair<A, B>(val left: A?, val right: B?) {
    fun withLeft(newLeft: A?): Pair<A?, B?> {
        return Pair(newLeft, right)
    }

    fun withRight(newRight: B?): Pair<A?, B?> {
        return Pair(left, newRight)
    }
}
