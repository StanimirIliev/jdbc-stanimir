package com.clouway.contacts.adapter

import com.clouway.contacts.core.Address
import com.clouway.contacts.core.AddressRepository
import java.sql.Connection

class JdbcAddressRepository(val con: Connection, val table: String) : AddressRepository {

    override fun register(address: Address) {
        val statement = con.createStatement()
        statement.execute("INSERT INTO $table VALUES(" +
                "${address.id}, '${address.street}', ${address.number}, '${address.town}')")
    }

    override fun getById(id: Int): Address? {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Id=$id")
        if(!result.next()) {
            return null
        }
        return Address(result.getInt("Id"), result.getString("Street"),
                result.getInt("No"), result.getString("Town"))
    }

    override fun getByTown(town: String): List<Address> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Town = '$town'")
        val output = ArrayList<Address>()
        while(result.next()) {
            output.add(Address(result.getInt("Id"), result.getString("Street"),
                    result.getInt("No"), result.getString("Town")))
        }
        return output
    }

    override fun getByStreet(street: String): List<Address> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE Street = '$street'")
        val output = ArrayList<Address>()
        while(result.next()) {
            output.add(Address(result.getInt("Id"), result.getString("Street"),
                    result.getInt("No"), result.getString("Town")))
        }
        return output
    }

    override fun getByStreetNumber(number: Int): List<Address> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE No = $number")
        val output = ArrayList<Address>()
        while(result.next()) {
            output.add(Address(result.getInt("Id"), result.getString("Street"),
                    result.getInt("No"), result.getString("Town")))
        }
        return output
    }

    override fun getAll(): List<Address> {
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $table WHERE")
        val output = ArrayList<Address>()
        while(result.next()) {
            output.add(Address(result.getInt("Id"), result.getString("Street"),
                    result.getInt("No"), result.getString("Town")))
        }
        return output
    }
}