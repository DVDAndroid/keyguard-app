package com.artemchep.keyguard.ui

import kotlin.streams.asSequence

fun String.asCodePointsSequence(): Sequence<String> = sequence {
    val out = codePoints()
        .asSequence()
        .map { codePoint ->
            String(Character.toChars(codePoint))
        }
    yieldAll(out)
}
