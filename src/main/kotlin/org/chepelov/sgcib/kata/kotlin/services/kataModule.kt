package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.services.ClientManager
import org.chepelov.sgcib.kata.kotlin.services.ClientManagerImpl
import org.chepelov.sgcib.kata.kotlin.services.repo.ClientRepository
import org.koin.dsl.module
import org.koin.core.inject

val kataServicesModule = module {
    single { ClientManagerImpl(get()) as ClientManager }
}