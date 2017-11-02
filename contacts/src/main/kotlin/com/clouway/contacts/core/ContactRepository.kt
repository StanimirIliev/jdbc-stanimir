package com.clouway.contacts.core

interface ContactRepository {
    fun register(contact: Contact)
    fun getByName(name: String): Contact?
    fun getByUserId(userId: Int): Contact?
    fun getByAddressId(addressId: Int): List<Contact>
    fun getAddressByUserId(userId: Int): Address?
    fun getUsersByAddressId(addressId: Int): List<User>
    fun getAllUsersAt(town: String): List<User>
    fun getAllUsersAt(town: String, street: String): List<User>
    fun getAllUsersAt(town: String, street: String, number: Int): List<User>
}