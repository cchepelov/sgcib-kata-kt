package org.chepelov.sgcib.kata.kotlin.services.repo

import org.chepelov.sgcib.kata.kotlin.model.Client
import org.chepelov.sgcib.kata.kotlin.model.ClientId
import org.chepelov.sgcib.kata.kotlin.model.KataException
import org.koin.core.KoinComponent

/**
 * This is the low-level storage interface to [Client] entities.
 *
 *  @note per the kata's ground rules, this will not be actually implemented
 */
interface ClientRepository: KoinComponent {
    /**
     * @return the client identified by ClientId
     * @throws [KataException.Companion.ClientNotFound] if the clientId can't be found.
     */
    @Throws(KataException.Companion.ClientNotFound::class)
    fun get(clientId: ClientId): Client
}