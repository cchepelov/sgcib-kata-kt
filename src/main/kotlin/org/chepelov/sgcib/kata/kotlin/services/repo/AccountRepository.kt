package org.chepelov.sgcib.kata.kotlin.services.repo

import org.chepelov.sgcib.kata.kotlin.model.Account
import org.chepelov.sgcib.kata.kotlin.model.AccountId
import org.chepelov.sgcib.kata.kotlin.model.KataException

interface AccountRepository {
    @Throws(KataException.Companion.NotFound.Companion.Account::class)
    fun get(accountId: AccountId): Account

}