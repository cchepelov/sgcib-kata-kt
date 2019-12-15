package org.chepelov.sgcib.kata.kotlin

import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.chepelov.sgcib.kata.kotlin.mocks.ClientRepositoryMock
import org.chepelov.sgcib.kata.kotlin.model.Client
import org.chepelov.sgcib.kata.kotlin.model.ClientId
import org.chepelov.sgcib.kata.kotlin.model.KataException
import org.chepelov.sgcib.kata.kotlin.model.UserId
import org.chepelov.sgcib.kata.kotlin.services.ClientManager
import org.chepelov.sgcib.kata.kotlin.services.kataServicesModule
import org.chepelov.sgcib.kata.kotlin.services.repo.ClientRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given

class KataSpec: WordSpec(), KoinTest {

    val mocks = module() {
        single { ClientRepositoryMock() as ClientRepository }
    }

    val clientRepository: ClientRepository by inject()
    val clientManager: ClientManager by inject()

    init {
        startKoin { modules(listOf(kataServicesModule, mocks)) }
        declareMock<ClientRepository> {
            given(this.get(ClientId("mickey"))).willReturn(Client(ClientId("mickey"), "Mickey"))
            given(this.get(ClientId("scrooge"))).willThrow(KataException.Companion.ClientNotFound(ClientId("scrooge") ))
        }

        ("US1: In order to save money" +
                "as a bank client" +
                "I want to make a deposit in my account") When {

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
                    shouldThrow<KataException.Companion.ClientNotFound> {
                        clientManager.get(ClientId("scrooge"), UserId("mickey"))
                    }
                }
            }


        }
    }
}