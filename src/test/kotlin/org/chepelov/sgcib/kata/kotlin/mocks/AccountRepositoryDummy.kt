package org.chepelov.sgcib.kata.kotlin.mocks

import org.chepelov.sgcib.kata.kotlin.model.Account
import org.chepelov.sgcib.kata.kotlin.model.AccountEvent
import org.chepelov.sgcib.kata.kotlin.model.AccountId
import org.chepelov.sgcib.kata.kotlin.model.MonetaryAmount
import org.chepelov.sgcib.kata.kotlin.services.repo.AccountRepository
import org.chepelov.sgcib.kata.kotlin.services.repo.transactionally
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class AccountRepositoryDummy(val accounts: AccountSide): AccountRepository {

    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)

        interface AccountSide {
            fun getAccount(accountId: AccountId): Account
        }
        class AccountSideDumy: AccountSide {
            override fun getAccount(accountId: AccountId): Account {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }


    }


    private val txnLock = ReentrantLock()
    override fun begin() {
        txnLock.lock()
    }

    override fun commit() {
        txnLock.unlock()
    }

    override fun rollback() {
        txnLock.unlock()
        logger.warn("doing a rollback — this is unsupported in a dummy")
    }

    override fun get(accountId: AccountId): Account {
        return accounts.getAccount(accountId)
    }

    private val balanceCache = mutableMapOf<AccountId, MonetaryAmount>()

    override fun clearBalanceCache(accountId: AccountId) {
        transactionally {
            balanceCache.remove(accountId)
        }
    }

    override fun recordBalanceCache(accountId: AccountId, amount: MonetaryAmount): MonetaryAmount {
        transactionally {
            balanceCache.put(accountId, amount)
        }
        return amount
    }

    override fun getCachedBalanceMaybe(accountId: AccountId): MonetaryAmount? {
        return transactionally {
            balanceCache.get(accountId)
        }
    }

    private val eventStore = mutableMapOf<AccountId, List<AccountEvent>>()

    override fun recordEvent(accountId: AccountId, event: AccountEvent) {
        transactionally {
            clearBalanceCache(accountId)

            val events = getEvents(accountId)
            eventStore.put(accountId, events.plus(event))
        }
    }

    override fun getEvents(accountId: AccountId): Iterable<AccountEvent> {
        return transactionally {
            eventStore.getOrDefault(accountId, listOf())
        }
    }
}