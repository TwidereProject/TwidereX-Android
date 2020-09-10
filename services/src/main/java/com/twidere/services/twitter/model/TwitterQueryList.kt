package com.twidere.services.twitter.model

class TwitterQueryList<T> : ArrayList<T> {
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(c: MutableCollection<out T>) : super(c)

    override fun toString(): String {
        return this.joinToString(",")
    }
}