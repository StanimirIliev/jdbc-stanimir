package com.clouway.trip

import java.sql.Connection

open class Customer(var name: String, var id: Long, var age: Int, var email: String = "No email") {

    init {
        if (id < 0 || id > 9999999999) {
            throw IllegalArgumentException("Invalid id")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Customer

        if (name != other.name) return false
        if (id != other.id) return false
        if (age != other.age) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + age
        result = 31 * result + email.hashCode()
        return result
    }

    override fun toString(): String {
        return "Customer(name='$name', id=$id, age=$age, email='$email')\n"
    }
}