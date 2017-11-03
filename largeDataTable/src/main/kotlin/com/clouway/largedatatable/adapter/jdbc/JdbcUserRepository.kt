package com.clouway.largedatatable.adapter.jdbc

import com.clouway.largedatatable.core.User
import com.clouway.largedatatable.core.UserRepository
import java.sql.Connection

class JdbcUserRepository(val con: Connection, val table: String): UserRepository {

    override fun register(user: User) {
        val statement = con.createStatement()
        statement.execute("INSERT INTO $table VALUES(" +
                "${user.id}, '${user.firstName}', '${user.lastName}', ${user.age}, '${user.phone}')")
    }

    override fun getById(id: Int): User? {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Id = $id")
        if(!result.next()) {
            return null
        }
        return User(result.getInt("Id"), result.getString("FirstName"),
                result.getString("LastName"), result.getInt("Age"),
                result.getString("Phone"))
    }

    override fun getByNameAndPhone(firstName: String, lastName: String, phone: String): User? {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE FirstName = '$firstName' AND " +
                "LastName = '$lastName' AND Phone = '$phone'")
        if(!result.next()) {
            return null
        }
        return User(result.getInt("Id"), result.getString("FirstName"),
                result.getString("LastName"), result.getInt("Age"),
                result.getString("Phone"))
    }
}