package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.model.Account
import org.chepelov.sgcib.kata.kotlin.model.AccountId
import org.chepelov.sgcib.kata.kotlin.model.UserId

interface AccountManager {
    fun get(accountId: AccountId, authentifiedAs: UserId): Account
}
