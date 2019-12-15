package org.chepelov.sgcib.kata.kotlin

import io.kotlintest.specs.StringSpec
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.koin.test.KoinTest


class HelloSpec: StringSpec(), KoinTest {
    init {
        "test should run" {
            Hello("bonjour").greeting shouldBe "bonjour"
        }
    }
}
