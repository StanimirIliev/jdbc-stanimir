package com.clouway.contacts.adapter

import com.clouway.contacts.core.Address
import com.clouway.contacts.core.Contact
import com.clouway.contacts.core.ContactRepository
import com.clouway.contacts.core.User
import java.sql.Connection

class JdbcContactRepository(val con: Connection, val contactTable: String,
                            val userTable: String, val addressTable: String): ContactRepository {

    override fun register(contact: Contact) {
        val statement = con.createStatement()
        statement.execute("INSERT INTO $contactTable VALUES('${contact.name}'," +
                "(SELECT Id FROM $userTable WHERE Id = ${contact.userId})," +
                "(SELECT Id FROM $addressTable WHERE Id = ${contact.addressId}))")
    }

    override fun getByName(name: String): Contact? {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $contactTable WHERE Name = '$name'")
        if(!result.next()) {
            return null
        }
        return Contact(result.getString("Name"),
                result.getInt("UserId"), result.getInt("AddressId"))
    }

    override fun getByUserId(userId: Int): Contact? {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $contactTable WHERE UserId = $userId")
        if(!result.next()) {
            return null
        }
        return Contact(result.getString("Name"),
                result.getInt("UserId"), result.getInt("AddressId"))
    }

    override fun getByAddressId(addressId: Int): List<Contact> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $contactTable WHERE AddressId = $addressId")
        val output = ArrayList<Contact>()
        while(result.next()) {
            output.add(Contact(result.getString("Name"),
                    result.getInt("UserId"), result.getInt("AddressId")))
        }
        return output
    }

    override fun getAddressByUserId(userId: Int): Address? {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $addressTable WHERE Id = (" +
                "SELECT AddressId FROM $contactTable WHERE UserId = $userId)")
        if(!result.next()) {
            return null
        }
        return Address(result.getInt("Id"), result.getString("Street"),
                result.getInt("No"), result.getString("Town"))
    }

    override fun getUsersByAddressId(addressId: Int): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $userTable WHERE " +
                "Id = IN(SELECT UserId FROM $contactTable WHERE AddressId = $addressId)")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }

    override fun getAllUsersAt(town: String): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT $userTable.Id, $userTable.FirstName," +
                "$userTable.LastName, $userTable.Age, $addressTable.Town FROM $contactTable INNER JOIN $userTable ON " +
                "$contactTable.UserId = $userTable.Id INNER JOIN $addressTable ON " +
                "$contactTable.AddressId = $addressTable.Id WHERE Town = '$town'")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }

    override fun getAllUsersAt(town: String, street: String): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT $userTable.Id, $userTable.FirstName," +
                "$userTable.LastName, $userTable.Age, $addressTable.Town, $addressTable.Street FROM $contactTable " +
                "INNER JOIN $userTable ON $contactTable.UserId = $userTable.Id INNER JOIN $addressTable ON " +
                "$contactTable.AddressId = $addressTable.Id WHERE Town = '$town' AND Street = '$street'")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }

    override fun getAllUsersAt(town: String, street: String, number: Int): List<User> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT $userTable.Id, $userTable.FirstName," +
                "$userTable.LastName, $userTable.Age, $addressTable.Town, $addressTable.Street, $addressTable.No FROM " +
                "$contactTable INNER JOIN $userTable ON $contactTable.UserId = $userTable.Id INNER JOIN " +
                "$addressTable ON $contactTable.AddressId = $addressTable.Id WHERE Town = '$town' AND " +
                "Street = '$street' AND No = $number")
        val output = ArrayList<User>()
        while(result.next()) {
            output.add(User(result.getInt("Id"), result.getString("FirstName"),
                    result.getString("LastName"), result.getInt("Age")))
        }
        return output
    }
}