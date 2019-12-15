package org.chepelov.sgcib.kata.kotlin.model


data class Client(val id: ClientId, val name: String) {
    fun checkAccessBy(authentifiedAs: UserId): Client {
        return if (authentifiedAs.id == id.id) {
            this
        } else {
            throw KataException.Companion.AccessDenied.Companion.User(id, authentifiedAs)
        }
    }
}