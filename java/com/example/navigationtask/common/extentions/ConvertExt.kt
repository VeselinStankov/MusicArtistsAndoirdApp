package com.example.navigationtask.common.extentions

typealias Convert<A, B> = (A) -> B

operator fun <A, B> Convert<A, B>.invoke(inputList: List<A>): List<B> =
    if (inputList.isEmpty()) {
        emptyList()
    } else {
        inputList.map { this(it) }
    }