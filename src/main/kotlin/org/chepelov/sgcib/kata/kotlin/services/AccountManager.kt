package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.model.*
import java.math.BigDecimal

interface AccountManager {
    /**
     * Locate an [Account]'s primary metadata
     */
    fun get(accountId: AccountId, authentifiedAs: UserId): Account

    /**
     * Get the [Account]'s balance given the currently known and booked events
     */
    fun getBalance(accountId: AccountId, authentifiedAs: UserId): MonetaryAmount

    /**
     * Record a new cash deposit event within the account.
     * @throws KataException.Companion.InvalidAccountCurrency in case the deposit is attempted in a currency incompatible
     * with the account's currency
     */
    fun depositCash(accountId: AccountId, authentifiedAs: UserId, amount: MonetaryAmount)

    /**
     * Record a new cash deposit event within the account.
     * @throws KataException.Companion.InvalidAccountCurrency in case the withdrawal is attempted in a currency incompatible
     * with the account's currency
     * @throws KataException.Companion.ExcessiveDraftAttempt in case the user attempts to draw more than is allowed
     */
    fun withdrawCash(accountId: AccountId, authentifiedAs: UserId, amount: MonetaryAmount)
}
