package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.model.*
import java.math.BigDecimal

interface AccountManager {
    fun get(accountId: AccountId, authentifiedAs: UserId): Account

    fun getBalance(accountId: AccountId, authentifiedAs: UserId): MonetaryAmount

    fun depositCash(accountId: AccountId, authentifiedAs: UserId, amount: MonetaryAmount)
    fun withdrawCash(accountId: AccountId, authentifiedAs: UserId, amount: MonetaryAmount)
}
