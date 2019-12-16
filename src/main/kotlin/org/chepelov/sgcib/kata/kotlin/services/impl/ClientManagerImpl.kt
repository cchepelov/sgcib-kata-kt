package org.chepelov.sgcib.kata.kotlin.services.impl

import org.chepelov.sgcib.kata.kotlin.model.Client
import org.chepelov.sgcib.kata.kotlin.model.ClientId
import org.chepelov.sgcib.kata.kotlin.model.UserId
import org.chepelov.sgcib.kata.kotlin.services.ClientManager
import org.chepelov.sgcib.kata.kotlin.services.repo.ClientRepository

class ClientManagerImpl(val repository: ClientRepository):
    ClientManager {
    override fun get(clientId: ClientId, authentifiedAs: UserId): Client {
        val client = repository.get(clientId)
        return client.checkAccessBy(authentifiedAs)
    }
}