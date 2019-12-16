package org.chepelov.sgcib.kata.kotlin.model

import java.math.BigDecimal

data class MonetaryAmount(val amount: BigDecimal, val currency: CurrencyCode): Comparable<MonetaryAmount> {
    override fun compareTo(other: MonetaryAmount): Int {
        if (this.currency != other.currency) {
            TODO("distinct-currency comparison policy should be defined")
        } else {
            return this.amount.compareTo(other.amount)
        }
    }
}