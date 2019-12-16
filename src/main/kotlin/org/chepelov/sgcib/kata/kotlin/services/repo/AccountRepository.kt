package org.chepelov.sgcib.kata.kotlin.services.repo

import org.chepelov.sgcib.kata.kotlin.model.*

interface AccountRepository: TransactionalRepository {
    @Throws(KataException.Companion.NotFound.Companion.Account::class)
    fun get(accountId: AccountId): Account



    fun clearBalanceCache(accountId: AccountId)
    fun recordBalanceCache(accountId: AccountId, amount: MonetaryAmount): MonetaryAmount
    fun getCachedBalanceMaybe(accountId: AccountId): MonetaryAmount?

    fun recordEvent(accountId: AccountId, event: AccountEvent) // implies clearing the balance cache for the account

    fun getEvents(accountId: AccountId): Iterable<AccountEvent>
}