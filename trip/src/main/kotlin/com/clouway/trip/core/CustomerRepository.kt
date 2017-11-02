package com.clouway.trip.core

interface CustomerRepository {
    fun registerCustomer(customer: Customer)
    fun updateCustomer(customer: Customer, name: String, id: Long, age: Int, email: String)
    fun isRegistered(customerId: Long): Boolean
}