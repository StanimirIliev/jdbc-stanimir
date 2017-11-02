package com.clouway.contacts.core

interface UserRepository {
    fun register(user: User)
    fun getById(id: Int): User?
    fun getByFirstName(firstName: String): List<User>
    fun getByLastName(lastName: String): List<User>
    fun getByAge(age: Int): List<User>
    fun getAll(): List<User>
}