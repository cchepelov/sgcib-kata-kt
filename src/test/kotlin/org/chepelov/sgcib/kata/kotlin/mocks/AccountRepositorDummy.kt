package org.chepelov.sgcib.kata.kotlin.mocks

import org.chepelov.sgcib.kata.kotlin.model.Account
import org.chepelov.sgcib.kata.kotlin.model.AccountId
import org.chepelov.sgcib.kata.kotlin.services.repo.AccountRepository

class AccountRepositorDummy: AccountRepository {
    override fun get(accountId: AccountId): Account {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}