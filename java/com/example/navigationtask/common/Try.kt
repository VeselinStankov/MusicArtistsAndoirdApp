package com.example.navigationtask.common

import arrow.core.Either
import arrow.core.left
import arrow.core.right

typealias Try<T> = Either<Err, T>

typealias Try2<A, B> = Either<Err, Pair<A, B>>

typealias Try3<A, B, C> = Either<Err, Triple<A, B, C>>

sealed class Err {

    object Unknown : Err()

    data class Exc(val throwable: Throwable) : Err()

    abstract class Custom : Err()
}

fun <T> T.ok(): Try<T> = this.right()

fun Throwable.err(): Try<Nothing> = Err.Exc(this).left()

fun Err.err(): Try<Nothing> = this.left()

fun <T> Try<T>.isErr(): Boolean = isLeft()

fun <T> Try<T>.isOk(): Boolean = isRight()

fun Try<*>.forceErr(): Err =
    this.fold(
        { it },
        { error("Forced Err but found Ok. Use Try.isErr() before forcing.") }
    )

fun <T> Try<T>.forceOk(): T =
    this.fold(
        { error("Forced Ok but found Err. Use Try.isOk() before forcing.") },
        { it }
    )