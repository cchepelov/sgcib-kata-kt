package org.chepelov.sgcib.kata.kotlin.model

import java.math.BigDecimal
import java.time.Instant

sealed class AccountEvent() {
    abstract val instant: Instant
    abstract val direction: EventDirection
    abstract val value: MonetaryAmount

    companion object {
        data class CashDeposit(override val instant: Instant,
                               override val value: MonetaryAmount): AccountEvent() {
            override val direction: EventDirection = EventDirection.Receive
        }

    }

}

