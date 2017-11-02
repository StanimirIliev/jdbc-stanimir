package com.clouway.trip.core

data class Customer(var name: String, var id: Long, var age: Int, var email: String = "No email") {
    init {
        if (id < 0 || id > 9999999999) {
            throw IllegalArgumentException("Invalid id")
        }
    }
}