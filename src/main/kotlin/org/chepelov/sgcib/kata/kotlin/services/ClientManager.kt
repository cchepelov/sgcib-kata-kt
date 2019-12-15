package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.model.Client
import org.chepelov.sgcib.kata.kotlin.model.ClientId
import org.chepelov.sgcib.kata.kotlin.model.UserId

interface ClientManager {
    fun get(clientId: ClientId, authentifiedAs: UserId): Client
}