package org.chepelov.sgcib.kata.kotlin.model

sealed class KataException: Exception() {
    companion object {
        sealed class NotFound(): KataException() {
            companion object {
                data class Client(val clientId: ClientId): KataException()
                data class Account(val accountId: AccountId): KataException()
            }
        }

        sealed class AccessDenied(): KataException() {
            abstract val userId: UserId

            companion object {
                data class User(val clientId: ClientId, override val userId: UserId): AccessDenied()
            }
        }
    }
}

