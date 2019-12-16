package org.chepelov.sgcib.kata.kotlin

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.chepelov.sgcib.kata.kotlin.mocks.AccountRepositoryDummy
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
import java.math.BigDecimal

class KataSpec: WordSpec(), KoinTest {

    val mocks = module() {
        single { ClientRepositoryDummy() as ClientRepository }
        single { AccountRepositoryDummy.Companion.AccountSideDumy() as AccountRepositoryDummy.Companion.AccountSide }
        single { AccountRepositoryDummy(get()) as AccountRepository }
    }

    val clientManager: ClientManager by inject()
    val accountManager: AccountManager by inject()

    init {
        startKoin { modules(listOf(kataServicesModule, mocks)) }
        declareMock<ClientRepository> {
            given(this.get(ClientId("mickey"))).willReturn(Client(ClientId("mickey"), "Mickey"))
            given(this.get(ClientId("scrooge"))).willThrow(KataException.Companion.NotFound.Companion.Client(ClientId("scrooge") ))
        }

        declareMock<AccountRepositoryDummy.Companion.AccountSide> {
            given(this.getAccount(AccountId("000123E"))).willReturn(Account(AccountId("000123E"), ClientId("mickey"), CurrencyCode.Companion.Eur))
            given(this.getAccount(AccountId("000124U"))).willReturn(Account(AccountId("000124U"), ClientId("mickey"), CurrencyCode.Companion.Usd))

            /* given(this.get(any<AccountId>())).willAnswer(Answer<Account> {
                val arg = it.getArgument<AccountId?>(0)
                throw KataException.Companion.NotFound.Companion.Account(arg ?: AccountId("<null>"))
            })
             */ // fails to init, as ("Java-Kotlin interop" shouldBe "perfect"), but truth in advertising
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
                    val actingAsMickey = UserId("mickey")
                    val account = accountManager.get(AccountId("000123E"), actingAsMickey)

                    val oldBalance = accountManager.getBalance(account.id, actingAsMickey)
                    oldBalance.currency shouldBe account.currency

                    val quantity = BigDecimal(35)

                    accountManager.depositCash(account.id, actingAsMickey, MonetaryAmount(quantity, CurrencyCode.Eur))
                    val newBalance = accountManager.getBalance(account.id, actingAsMickey)

                    newBalance.currency shouldBe oldBalance.currency

                    (newBalance.amount - oldBalance.amount) shouldBe quantity
                }
            }

            "attempting to deposit 35 euros into Mickey's dollar account, being Mickey" should {
                "fail due to wrong currency" {
                    shouldThrow<KataException.Companion.InvalidAccountCurrency> {
                        val actingAsMickey = UserId("mickey")
                        val account = accountManager.get(AccountId("000124U"), actingAsMickey)

                        val oldBalance = accountManager.getBalance(account.id, actingAsMickey)
                        oldBalance.currency shouldBe account.currency

                        val quantity = BigDecimal(35)

                        accountManager.depositCash(
                            account.id,
                            actingAsMickey,
                            MonetaryAmount(quantity, CurrencyCode.Eur)
                        )
                    }
                }
            }

            "attempting to deposit 40 dollars into Mickey's dollar account, being Donald" should {
                "fail due to being denied" {
                    shouldThrow<KataException.Companion.AccessDenied.Companion.User> {
                        val actingAsDonald = UserId("donald")

                        val quantity = BigDecimal(35)

                        accountManager.depositCash(
                            AccountId("000124U"), // this belongs to Mickey !!!
                            actingAsDonald,
                            MonetaryAmount(quantity, CurrencyCode.Usd)
                        )
                    }
                }
            }

            // Eventually, Mickey should be able to deposit onto Pluto's account, as Mickey has Power of Attorney over Pluto's accounts
            // (not US1, but in a probable spinoff story)


        }
    }
}