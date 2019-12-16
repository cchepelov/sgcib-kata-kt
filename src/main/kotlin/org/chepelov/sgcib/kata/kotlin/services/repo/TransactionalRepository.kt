package org.chepelov.sgcib.kata.kotlin.services.repo

/**
 * Marks a repository that can perform multiple operations as part of a single transaction
 */
interface TransactionalRepository {
    fun begin() { }
    fun commit() { }
    fun rollback() { }
}


/**
 * Execute a transaction
 */
fun <T : TransactionalRepository, R> T.transactionally(op: (T) -> R): R {
    this.begin()
    var exceptionInFlight: Boolean = false

    try {
        return op(this)
    } catch (t: Throwable) {
        exceptionInFlight = true
        throw t
    }
    finally {
        if (exceptionInFlight) this.rollback() else this.commit()
    }
}
