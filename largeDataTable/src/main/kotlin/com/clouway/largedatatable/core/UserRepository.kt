package com.clouway.largedatatable.core

interface UserRepository {
    fun register(user: User)
    fun getById(id: Int): User?
    fun getByNameAndPhone(firstName: String, lastName: String, phone: String): User?
}