package org.chepelov.sgcib.kata.kotlin

import io.kotlintest.Spec
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

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)

        accountManager.depositCash(AccountId("000456G"), UserId("donald"), MonetaryAmount(BigDecimal(100), CurrencyCode.Gbp))
    }

    init {
        startKoin { modules(listOf(kataServicesModule, mocks)) }
        declareMock<ClientRepository> {
            given(this.get(ClientId("mickey"))).willReturn(Client(ClientId("mickey"), "Mickey"))
            given(this.get(ClientId("donald"))).willReturn(Client(ClientId("donald"), "Donald"))
            given(this.get(ClientId("scrooge"))).willThrow(KataException.Companion.NotFound.Companion.Client(ClientId("scrooge")))
        }

        declareMock<AccountRepositoryDummy.Companion.AccountSide> {
            given(this.getAccount(AccountId("000123E"))).willReturn(
                Account(
                    AccountId("000123E"),
                    ClientId("mickey"),
                    CurrencyCode.Eur
                )
            )
            given(this.getAccount(AccountId("000124U"))).willReturn(
                Account(
                    AccountId("000124U"),
                    ClientId("mickey"),
                    CurrencyCode.Usd
                )
            )
            given(this.getAccount(AccountId("000456G"))).willReturn(
                Account(
                    AccountId("000456G"),
                    ClientId("donald"),
                    CurrencyCode.Gbp
                )
            )



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

                        val quantity = BigDecimal(40)

                        accountManager.depositCash(
                            AccountId("000124U"), // this belongs to Mickey !!!
                            actingAsDonald,
                            MonetaryAmount(quantity, CurrencyCode.Usd)
                        )
                    }
                }
            }
        }

        // Eventually, Mickey should be able to deposit onto Pluto's account, as Mickey has Power of Attorney over Pluto's accounts
        // (not US1, but in a probable spinoff story)

        ("US2: In order to retrieve some or all of my savings" +
                "as a bank client" +
                "I want to withdraw money from my account") When {

            "Withdrawing £25 from Donald's pound account, being Donald" should {
                "succeed" {
                    val actingAsDonald = UserId("donald")
                    val account = accountManager.get(AccountId("000456G"), actingAsDonald)

                    val oldBalance = accountManager.getBalance(account.id, actingAsDonald)
                    oldBalance.currency shouldBe account.currency

                    val amount = MonetaryAmount(BigDecimal(25), CurrencyCode.Gbp)

                    accountManager.withdrawCash(account.id, actingAsDonald, amount)
                    val newBalance = accountManager.getBalance(account.id, actingAsDonald)

                    newBalance.currency shouldBe oldBalance.currency

                    (newBalance.amount - oldBalance.amount) shouldBe (-amount.amount)
                }
            }

            "Attempting to withdraw £25M from Donald's pound account (that had £100 to begin with), being Donald" should {
                "be denied due to overdraft, and the balance unchanged" {
                    val actingAsDonald = UserId("donald")
                    val account = accountManager.get(AccountId("000456G"), actingAsDonald)

                    val oldBalance = accountManager.getBalance(account.id, actingAsDonald)
                    oldBalance.currency shouldBe account.currency

                    shouldThrow<KataException.Companion.ExcessiveDraftAttempt> {
                        val amount = MonetaryAmount(BigDecimal(25000000), CurrencyCode.Gbp)

                        accountManager.withdrawCash(account.id, actingAsDonald, amount)
                    }

                    val newBalance = accountManager.getBalance(account.id, actingAsDonald)
                    newBalance shouldBe oldBalance
                }

            }


            "attempting to withdraw 35 euros from Mickey's dollar account, being Mickey" should {
                "fail due to wrong currency" {
                    shouldThrow<KataException.Companion.InvalidAccountCurrency> {
                        val actingAsMickey = UserId("mickey")
                        val account = accountManager.get(AccountId("000124U"), actingAsMickey)

                        val oldBalance = accountManager.getBalance(account.id, actingAsMickey)
                        oldBalance.currency shouldBe account.currency

                        val quantity = BigDecimal(35)

                        accountManager.withdrawCash(
                            account.id,
                            actingAsMickey,
                            MonetaryAmount(quantity, CurrencyCode.Eur)
                        )
                    }
                }
            }

            "attempting to withdraw 40 dollars from Mickey's dollar account, being Donald" should {
                "fail due to being denied" {
                    shouldThrow<KataException.Companion.AccessDenied.Companion.User> {
                        val actingAsDonald = UserId("donald")

                        val quantity = BigDecimal(40)

                        accountManager.withdrawCash(
                            AccountId("000124U"), // this belongs to Mickey !!!
                            actingAsDonald,
                            MonetaryAmount(quantity, CurrencyCode.Usd)
                        )
                    }
                }
            }

            // Eventually, Mickey should be able to withdraw from Pluto's account (unless the POA is limited)

            // Eventually, some accounts may be configured with some level of amount-limited and/or time-limited
            // overdraft allowances and we'll need to test for that as well

        }
    }


}
