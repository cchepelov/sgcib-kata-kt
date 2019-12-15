package org.chepelov.sgcib.kata.kotlin.model

data class Account(val id: AccountId, val owner: ClientId, val currency: CurrencyCode) {
}