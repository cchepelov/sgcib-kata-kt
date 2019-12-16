package org.chepelov.sgcib.kata.kotlin.services

import org.chepelov.sgcib.kata.kotlin.services.impl.AccountManagerImpl
import org.chepelov.sgcib.kata.kotlin.services.impl.ClientManagerImpl
import org.koin.dsl.module

val kataServicesModule = module {

    single { ClientManagerImpl(get()) as ClientManager }
    single { AccountManagerImpl(get(), get()) as AccountManager }
}