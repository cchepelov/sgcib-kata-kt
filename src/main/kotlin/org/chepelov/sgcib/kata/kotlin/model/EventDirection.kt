package org.chepelov.sgcib.kata.kotlin.model

enum class EventDirection(val factor: Int) {
    Receive(+1), Pay(-1)
}