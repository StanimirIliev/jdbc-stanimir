package com.clouway.contacts.adapter.jdbc

import com.clouway.contacts.adapter.*
import com.clouway.contacts.core.Address
import com.clouway.contacts.core.Contact
import com.clouway.contacts.core.User
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.FileReader
import java.sql.DriverManager

class JdbcContactRepositoryTest {

    val con = DriverManager.getConnection("jdbc:mysql://${System.getenv("DB_HOST")}/" +
            System.getenv("DB_TABLE"), System.getenv("DB_USER"), System.getenv("DB_PASS"))
    val contactRepo = JdbcContactRepository(con, "Contacts", "Users", "Addresses")
    val userRepo = JdbcUserRepository(con, "Users")
    val addressRepo = JdbcAddressRepository(con, "Addresses")

    @Before
    fun setUp() {
        val statement = con.createStatement()
        statement.execute("DROP TABLE IF EXISTS Contacts")
        statement.execute("DROP TABLE IF EXISTS Users")
        statement.execute("DROP TABLE IF EXISTS Addresses")
        statement.execute(FileReader("schema/Users.sql").readText())
        statement.execute(FileReader("schema/Addresses.sql").readText())
        statement.execute(FileReader("schema/Contacts.sql").readText())
    }

    @Test
    fun tryToGetUnregisteredContact() {
        assertThat(contactRepo.getByName("Gosho"), `is`(nullValue()))
    }

    @Test
    fun GetRegisteredContactByName() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        userRepo.register(user1)
        userRepo.register(user2)
        val address = Address(1, "Vidima", 17, "Gabrovo")
        addressRepo.register(address)
        val contact1 = Contact("Gosho", 1, 1)
        val contact2 = Contact("Velizar", 2, 1)
        contactRepo.register(contact1)
        contactRepo.register(contact2)
        assertThat(contactRepo.getByName("Gosho"), `is`(equalTo(contact1)))
    }

    @Test
    fun getRegisteredContactByUserId() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        userRepo.register(user1)
        userRepo.register(user2)
        val address = Address(1, "Vidima", 17, "Gabrovo")
        addressRepo.register(address)
        val contact1 = Contact("Gosho", 1, 1)
        val contact2 = Contact("Velizar", 2, 1)
        contactRepo.register(contact1)
        contactRepo.register(contact2)
        assertThat(contactRepo.getByUserId(1), `is`(equalTo(contact1)))
    }

    @Test
    fun getRegisteredContactsByAddressId() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        val user3 = User(3, "Stanimir", "Iliev", 20)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        val address1 = Address(1, "Vidima", 17, "Gabrovo")
        val address2 = Address(2, "Zelena Livada", 30, "Gabrovo")
        addressRepo.register(address1)
        addressRepo.register(address2)
        val contact1 = Contact("Gosho", 1, 1)
        val contact2 = Contact("Velizar", 2, 1)
        val contact3 = Contact("Stanimir", 3, 2)
        contactRepo.register(contact1)
        contactRepo.register(contact2)
        contactRepo.register(contact3)
        assertThat(contactRepo.getByAddressId(1), `is`(equalTo(listOf(contact1, contact2))))
    }

    @Test
    fun getAllRegisteredUsersAtTheSameTown() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        val user3 = User(3, "Stanimir", "Iliev", 20)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        val address1 = Address(1, "Vidima", 17, "Gabrovo")
        val address2 = Address(2, "Zelena Livada", 30, "Veliko Tarnovo")
        addressRepo.register(address1)
        addressRepo.register(address2)
        val contact1 = Contact("Gosho", 1, 1)
        val contact2 = Contact("Velizar", 2, 1)
        val contact3 = Contact("Stanimir", 3, 2)
        contactRepo.register(contact1)
        contactRepo.register(contact2)
        contactRepo.register(contact3)
        assertThat(contactRepo.getAllUsersAt("Gabrovo"), `is`(equalTo(listOf(user1, user2))))
    }

    @Test
    fun getAllRegisteredUsersAtTheSameTownAndTheSameStreet() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        val user3 = User(3, "Stanimir", "Iliev", 20)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        val address1 = Address(1, "Vidima", 17, "Gabrovo")
        val address2 = Address(2, "Zelena Livada", 30, "Gabrovo")
        addressRepo.register(address1)
        addressRepo.register(address2)
        val contact1 = Contact("Gosho", 1, 1)
        val contact2 = Contact("Velizar", 2, 1)
        val contact3 = Contact("Stanimir", 3, 2)
        contactRepo.register(contact1)
        contactRepo.register(contact2)
        contactRepo.register(contact3)
        assertThat(contactRepo.getAllUsersAt("Gabrovo", "Vidima"), `is`(equalTo(listOf(user1, user2))))
    }

    @Test
    fun getAllRegisteredUsersAtTheSameAddress() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        val user3 = User(3, "Stanimir", "Iliev", 20)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        val address1 = Address(1, "Vidima", 17, "Gabrovo")
        val address2 = Address(2, "Vidima", 30, "Gabrovo")
        addressRepo.register(address1)
        addressRepo.register(address2)
        val contact1 = Contact("Gosho", 1, 1)
        val contact2 = Contact("Velizar", 2, 1)
        val contact3 = Contact("Stanimir", 3, 2)
        contactRepo.register(contact1)
        contactRepo.register(contact2)
        contactRepo.register(contact3)
        assertThat(contactRepo.getAllUsersAt("Gabrovo", "Vidima", 17), `is`(equalTo(listOf(user1, user2))))
    }
}