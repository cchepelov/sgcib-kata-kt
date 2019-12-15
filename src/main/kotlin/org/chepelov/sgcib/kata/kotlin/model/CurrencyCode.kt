package org.chepelov.sgcib.kata.kotlin.model

data class CurrencyCode(val code: String) {
    companion object {
        val validPattern = Regex("^[A-Z]{3}$$")

        val Eur = CurrencyCode("EUR")
        val Usd = CurrencyCode("USD")
        val Gbp = CurrencyCode("GBP")
        val Jpy = CurrencyCode("JPY")
        val Sek = CurrencyCode("SEK")

    }
    init {
        if (!validPattern.matches(code)) {
            throw IllegalArgumentException("code $code is not a 3-letter ISO-4217 currency code")
        }
    }
}
