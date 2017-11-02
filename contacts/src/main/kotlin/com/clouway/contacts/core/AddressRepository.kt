package com.clouway.contacts.core

interface AddressRepository {
    fun register(address: Address)
    fun getById(id: Int): Address?
    fun getByTown(town: String): List<Address>
    fun getByStreet(street: String): List<Address>
    fun getByStreetNumber(number: Int): List<Address>
    fun getAll(): List<Address>
}