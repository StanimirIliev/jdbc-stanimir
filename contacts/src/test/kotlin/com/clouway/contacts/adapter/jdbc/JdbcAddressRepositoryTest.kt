package com.clouway.contacts.adapter.jdbc

import com.clouway.contacts.adapter.JdbcAddressRepository
import com.clouway.contacts.core.Address
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.FileReader
import java.sql.DriverManager

class JdbcAddressRepositoryTest {

    val con = DriverManager.getConnection("jdbc:mysql://${System.getenv("DB_HOST")}/" +
            System.getenv("DB_TABLE"), System.getenv("DB_USER"), System.getenv("DB_PASS"))
    val addressRepo = JdbcAddressRepository(con, "Addresses")

    @Before
    fun setUp() {
        val statement = con.createStatement()
        statement.execute("DROP TABLE IF EXISTS Addresses")
        statement.execute(FileReader("schema/Addresses.sql").readText())
    }

    @Test
    fun getAddressThatWasRegistered() {
        val address = Address(1, "Zelena Livada", 30, "Gabrovo")
        addressRepo.register(address)
        assertThat(addressRepo.getById(1), `is`(equalTo(address)))
    }

    @Test
    fun tryToGetUnregisteredAddress() {
        assertThat(addressRepo.getById(1), `is`(nullValue()))
    }

    @Test
    fun getRegisteredAddressesByStreet() {
        val address1 = Address(1, "Zelena Livada", 30, "Gabrovo")
        val address2 = Address(2, "Zelena Livada", 15, "Gabrovo")
        val address3 = Address(3, "Nikola Gabrovski", 48, "Veliko Tarnovo")
        addressRepo.register(address1)
        addressRepo.register(address2)
        addressRepo.register(address3)
        assertThat(addressRepo.getByStreet("Zelena Livada"), `is`(equalTo(listOf(address1, address2))))
    }

    @Test
    fun getRegisteredAddressesByTown() {
        val address1 = Address(1, "Zelena Livada", 30, "Gabrovo")
        val address2 = Address(2, "Gradishte", 7, "Gabrovo")
        val address3 = Address(3, "Nikola Gabrovski", 48, "Veliko Tarnovo")
        addressRepo.register(address1)
        addressRepo.register(address2)
        addressRepo.register(address3)
        assertThat(addressRepo.getByTown("Gabrovo"), `is`(equalTo(listOf(address1, address2))))
    }

    @Test
    fun getRegisteredAddressesByNumber() {
        val address1 = Address(1, "Zelena Livada", 30, "Gabrovo")
        val address2 = Address(2, "Hristo Smirnenski", 48, "Gabrovo")
        val address3 = Address(3, "Nikola Gabrovski", 48, "Veliko Tarnovo")
        addressRepo.register(address1)
        addressRepo.register(address2)
        addressRepo.register(address3)
        assertThat(addressRepo.getByStreetNumber(48), `is`(equalTo(listOf(address2, address3))))
    }
}