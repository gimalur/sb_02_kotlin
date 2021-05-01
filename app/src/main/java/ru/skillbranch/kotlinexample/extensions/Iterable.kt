package ru.skillbranch.kotlinexample.extensions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    val idx = this.indexOfFirst(predicate)
    return if (idx == -1) return this else subList(0, idx)
}
