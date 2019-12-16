package org.chepelov.sgcib.kata.kotlin.model

import java.math.BigDecimal

data class MonetaryAmount(val amount: BigDecimal, val currency: CurrencyCode){
}