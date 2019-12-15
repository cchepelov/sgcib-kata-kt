package org.chepelov.sgcib.kata.kotlin.model

sealed class KataException: Exception() {
    companion object {
        data class ClientNotFound(val clientId: ClientId): KataException()

        sealed class AccessDenied(): KataException() {
            abstract val userId: UserId

            companion object {
                data class User(val clientId: ClientId, override val userId: UserId): AccessDenied()
            }
        }
    }
}

