package org.chepelov.sgcib.kata.kotlin

import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.chepelov.sgcib.kata.kotlin.mocks.AccountRepositorDummy
import org.chepelov.sgcib.kata.kotlin.mocks.ClientRepositoryDummy
import org.chepelov.sgcib.kata.kotlin.model.*
import org.chepelov.sgcib.kata.kotlin.services.AccountManager
import org.chepelov.sgcib.kata.kotlin.services.ClientManager
import org.chepelov.sgcib.kata.kotlin.services.kataServicesModule
import org.chepelov.sgcib.kata.kotlin.services.repo.AccountRepository
import org.chepelov.sgcib.kata.kotlin.services.repo.ClientRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.stubbing.Answer

class KataSpec: WordSpec(), KoinTest {

    val mocks = module() {
        single { ClientRepositoryDummy() as ClientRepository }
        single { AccountRepositorDummy() as AccountRepository }
    }

    val clientManager: ClientManager by inject()
    val accountManager: AccountManager by inject()

    init {
        startKoin { modules(listOf(kataServicesModule, mocks)) }
        declareMock<ClientRepository> {
            given(this.get(ClientId("mickey"))).willReturn(Client(ClientId("mickey"), "Mickey"))
            given(this.get(ClientId("scrooge"))).willThrow(KataException.Companion.NotFound.Companion.Client(ClientId("scrooge") ))
        }

        declareMock<AccountRepository> {
            given(this.get(AccountId("000123E"))).willReturn(Account(AccountId("00123E"), ClientId("mickey"), CurrencyCode.Companion.Eur))
            /* given(this.get(any<AccountId>())).willAnswer(Answer<Account> {
                val arg = it.getArgument<AccountId?>(0)
                throw KataException.Companion.NotFound.Companion.Account(arg ?: AccountId("<null>"))
            })
             */ // fails to init for some reason ("Java-Kotlin interop is perfect", but truth in advertising)
        }

        ("preliminaries: basic client accesses") When {
            "locating client Mickey on behalf of Mickey" should {
                "succeed" {

                    val client = clientManager.get(ClientId("mickey"), UserId("mickey"))
                }
            }

            "locating client Mickey on behalf of Donald (who lacks POA over Mickey)" should {
                "fail (denied)" {
                    shouldThrow<KataException.Companion.AccessDenied.Companion.User> {
                        clientManager.get(ClientId("mickey"), UserId("donald"))
                    }
                }
            }

            "locating non-client Scrooge on behalf of Mickey" should {
                "fail (not found)" {
                    shouldThrow<KataException.Companion.NotFound.Companion.Client> {
                        clientManager.get(ClientId("scrooge"), UserId("mickey"))
                    }
                }
            }
        }

        ("US1: In order to save money" +
                "as a bank client" +
                "I want to make a deposit in my account") When {

            "depositing 35 euros into Mickey's euro account, being Mickey" should {
                "succeed" {
                    val account = accountManager.get(AccountId("000123E"), UserId("mickey"))

                }
            }



        }
    }
}