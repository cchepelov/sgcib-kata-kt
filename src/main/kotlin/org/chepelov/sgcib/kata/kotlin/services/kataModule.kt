package org.chepelov.sgcib.kata.kotlin.services

import org.koin.dsl.module

val kataServicesModule = module {

    single { ClientManagerImpl(get()) as ClientManager }
    single { AccountManagerImpl(get(), get()) as AccountManager }
}