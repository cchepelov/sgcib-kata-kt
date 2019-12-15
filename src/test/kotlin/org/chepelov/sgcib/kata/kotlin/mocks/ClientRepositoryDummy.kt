package org.chepelov.sgcib.kata.kotlin.mocks

import org.chepelov.sgcib.kata.kotlin.model.Client
import org.chepelov.sgcib.kata.kotlin.model.ClientId
import org.chepelov.sgcib.kata.kotlin.services.repo.ClientRepository

class ClientRepositoryDummy: ClientRepository {
    override fun get(clientId: ClientId): Client {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}