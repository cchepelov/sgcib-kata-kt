package org.chepelov.sgcib.kata.kotlin.model

import java.math.BigDecimal
import java.time.Instant

/**
 * This represents a balance-modifying event that can happen onto an [Account]
 */
sealed class AccountEvent() {
    abstract val instant: Instant
    abstract val direction: EventDirection
    abstract val value: MonetaryAmount

    companion object {
        /**
         * This event represents the action of depositing physical cash into the customer's account
         *
         * Note that at this point we do not track the pile onto which the physical cash is put
         */
        data class CashDeposit(override val instant: Instant,
                               override val value: MonetaryAmount): AccountEvent() {
            override val direction: EventDirection = EventDirection.Receive
        }


        /**
         * This event represents the action of withdrawing physical cash from the customer's account
         *
         * Note that at this point we do not manage whence to take the actual cash from.
         */
        data class CashWithdrawal(override val instant: Instant,
                                  override val value: MonetaryAmount): AccountEvent() {
            override val direction: EventDirection = EventDirection.Pay
        }
    }

}

