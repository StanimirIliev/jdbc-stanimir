package com.clouway.trip.adapter.jdbc

import com.clouway.trip.core.Customer
import com.clouway.trip.core.CustomerRepository
import java.sql.Connection

class JdbcCustomerRepository(val con: Connection, val customersTable: String) : CustomerRepository {

    override fun registerCustomer(customer: Customer) {
        val statement = con.createStatement()
        statement.execute("INSERT INTO $customersTable VALUES('${customer.name}', ${customer.id}, ${customer.age}, " +
                "'${customer.email}')")
    }

    override fun updateCustomer(customer: Customer, name: String, id: Long, age: Int, email: String) {
        customer.name = name
        customer.id = id
        customer.age = age
        customer.email = email
        val statement = con.createStatement()
        statement.execute("UPDATE $customersTable SET Name='$name',PersonalId=$id,Age=$age,Email='$email' " +
                "WHERE PersonalId=${customer.id}")
    }

    override fun isRegistered(customerId: Long): Boolean {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $customersTable WHERE PersonalId = $customerId")
        return result.next()
    }

}