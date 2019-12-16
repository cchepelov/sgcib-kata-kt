package org.chepelov.sgcib.kata.kotlin.services.impl

import org.chepelov.sgcib.kata.kotlin.model.*
import org.chepelov.sgcib.kata.kotlin.services.AccountManager
import org.chepelov.sgcib.kata.kotlin.services.ClientManager
import org.chepelov.sgcib.kata.kotlin.services.repo.AccountRepository
import org.chepelov.sgcib.kata.kotlin.services.repo.transactionally
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.time.Instant

class AccountManagerImpl(val clientManager: ClientManager, val accountRepository: AccountRepository):
    AccountManager {
    override fun get(accountId: AccountId, authentifiedAs: UserId): Account {
        val account = accountRepository.get(accountId)
        clientManager.get(account.owner, authentifiedAs) // this validates the caller may access the account

        return account
    }


    private fun rebuildBalance(accountId: AccountId): MonetaryAmount {
        val result = accountRepository.transactionally {
            val account = accountRepository.get(accountId)
            val events = accountRepository.getEvents(accountId)
            val value = events.fold(BigDecimal(0), { accumulator, event ->
                if (event.value.currency != account.currency)
                    throw IllegalStateException("invalid event currency exists within account: ${event.value.currency} on account denominated in ${account.currency}")

                accumulator + (event.value.amount * BigDecimal(event.direction.factor))
            })

            val balance = MonetaryAmount(value, account.currency)
            val txnResult = accountRepository.recordBalanceCache(accountId, balance)
            txnResult
        }
        return result
    }

    override fun getBalance(accountId: AccountId, authentifiedAs: UserId): MonetaryAmount {
        return accountRepository.transactionally {
            val cachedBalance = accountRepository.getCachedBalanceMaybe(accountId)
            val effectiveBalance = cachedBalance ?: rebuildBalance(accountId)
            effectiveBalance
        }
    }

    override fun depositCash(accountId: AccountId, authentifiedAs: UserId, amount: MonetaryAmount) {
        val account = get(accountId, authentifiedAs)

        if (account.currency != amount.currency) throw KataException.Companion.InvalidAccountCurrency(amount.currency, account.currency)

        accountRepository.transactionally {
            it.clearBalanceCache(account.id)
            it.recordEvent(account.id, AccountEvent.Companion.CashDeposit(Instant.now(), amount))
        }
    }

    override fun withdrawCash(accountId: AccountId, authentifiedAs: UserId, amount: MonetaryAmount) {
        val account = get(accountId, authentifiedAs)
        if (account.currency != amount.currency) throw KataException.Companion.InvalidAccountCurrency(amount.currency, account.currency)

        accountRepository.transactionally {
            val balance = it.getCachedBalanceMaybe(account.id) ?: MonetaryAmount(BigDecimal(0), account.currency)

            if (balance < amount) throw KataException.Companion.ExcessiveDraftAttempt()

            it.clearBalanceCache(account.id)
            it.recordEvent(account.id, AccountEvent.Companion.CashWithdrawal(Instant.now(), amount))
        }
    }


}