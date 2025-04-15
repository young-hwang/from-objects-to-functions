package me.zettai._04.domain.tooling

import me.zettai._04.fp.Outcome
import me.zettai._04.fp.OutcomeError
import me.zettai._04.fp.onFailure
import org.junit.jupiter.api.fail

fun <E : OutcomeError, T> Outcome<E, T>.expectSuccess(): T =
    onFailure { error -> fail { "$this expected success but was $error" } }

fun <E : OutcomeError, T> Outcome<E, T>.expectFailure(): E =
    onFailure { error -> return error }
        .let { fail { "Expected failure but was $it" } }
