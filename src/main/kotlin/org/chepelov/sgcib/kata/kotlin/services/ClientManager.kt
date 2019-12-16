package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.model.Client
import org.chepelov.sgcib.kata.kotlin.model.ClientId
import org.chepelov.sgcib.kata.kotlin.model.UserId
import org.chepelov.sgcib.kata.kotlin.model.KataException

interface ClientManager {
    /**
     * Locate a [Client] record (basic metadata etc) given the user is authentified as being a specific [UserId]
     *
     * The authentication is expected to be performed in a transverse way, and is trusted at this layer
     * The authenticated User may or may not be identical to the [Client] given proxy account management privileges
     * (e.g. the user might be a bank employee, or might enjoy Power of Attorney from the actual Client, or might be
     * the Client's legal guardian, etc.)
     *
     * @throws KataException.Companion.AccessDenied in case the user is not allowed to deal with the [Client]'s data.
     */
    fun get(clientId: ClientId, authentifiedAs: UserId): Client
}