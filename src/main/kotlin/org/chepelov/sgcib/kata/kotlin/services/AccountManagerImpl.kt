package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.model.Account
import org.chepelov.sgcib.kata.kotlin.model.AccountId
import org.chepelov.sgcib.kata.kotlin.model.UserId
import org.chepelov.sgcib.kata.kotlin.services.repo.AccountRepository

class AccountManagerImpl(val clientManager: ClientManager, val accountRepository: AccountRepository): AccountManager {
    override fun get(accountId: AccountId, authentifiedAs: UserId): Account {
        val account = accountRepository.get(accountId)
        clientManager.get(account.owner, authentifiedAs) // this validates the caller may access the account

        return account
    }
}