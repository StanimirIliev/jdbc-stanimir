package com.clouway.contacts.adapter

import com.clouway.contacts.core.User
import com.clouway.contacts.core.UserRepository
import java.sql.Connection

class JdbcUserRepository(val con: Connection, val table: String): UserRepository {
    override fun register(user: User) {
        val statement = con.createStatement()
        statement.execute("INSERT INTO $table VALUES(" +
                "${user.id}, '${user.firstName}', '${user.lastName}', ${user.age})")
    }

    override fun getById(id: Int): User? {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Id=$id")
        if(!result.next()) {
            return null
        }
        return User(result.getInt("Id"), result.getString("FirstName"),
                result.getString("LastName"), result.getInt("Age"))
    }

    override fun getByFirstName(firstName: String): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE FirstName='$firstName'")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }

    override fun getByLastName(lastName: String): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE LastName='$lastName'")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }

    override fun getByAge(age: Int): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Age=$age")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }

    override fun getAll(): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }

}