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
    fun tryToGetUnregisteredContactByName() {
        assertThat(contactRepo.getByName("Gosho"), `is`(nullValue()))
    }

    @Test
    fun tryToGetUnregisteredContactById() {
        assertThat(contactRepo.getByUserId(1), `is`(nullValue()))
    }

    @Test
    fun getRegisteredContactByName() {
        userRepo.register(User(1, "Georgi", "Vasilev", 20))
        addressRepo.register(Address(1, "Vidima", 17, "Gabrovo"))
        val contact1 = Contact("Gosho", 1, 1)
        contactRepo.register(contact1)
        assertThat(contactRepo.getByName("Gosho"), `is`(equalTo(contact1)))
    }

    @Test
    fun getRegisteredContactByUserId() {
        userRepo.register(User(1, "Georgi", "Vasilev", 20))
        addressRepo.register(Address(1, "Vidima", 17, "Gabrovo"))
        val contact = Contact("Gosho", 1, 1)
        contactRepo.register(contact)
        assertThat(contactRepo.getByUserId(1), `is`(equalTo(contact)))
    }

    @Test
    fun getRegisteredContactsByAddressId() {
        pretendThatRepositoryContainsUsers(
                User(1, "Georgi", "Vasilev", 20),
                User(2, "Velizar", "Vasilev", 19),
                User(3, "Stanimir", "Iliev", 20)
        )
        pretendThatRepositoryContainsAddresses(
                Address(1, "Vidima", 17, "Gabrovo"),
                Address(2, "Zelena Livada", 30, "Gabrovo")
        )
        val contact1 = Contact("Gosho", 1, 1)
        val contact2 = Contact("Velizar", 2, 1)
        pretendThatRepositoryContainsContacts(
                contact1, contact2, Contact("Stanimir", 3, 2)
        )
        assertThat(contactRepo.getByAddressId(1), `is`(equalTo(listOf(contact1, contact2))))
    }

    @Test
    fun getAllRegisteredUsersAtTheSameTown() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        pretendThatRepositoryContainsUsers(
                user1, user2, User(3, "Stanimir", "Iliev", 20)
        )
        pretendThatRepositoryContainsAddresses(
                Address(1, "Vidima", 17, "Gabrovo"),
                Address(2, "Zelena Livada", 30, "Veliko Tarnovo")
        )
        pretendThatRepositoryContainsContacts(
                Contact("Gosho", 1, 1),
                Contact("Velizar", 2, 1),
                Contact("Stanimir", 3, 2)
        )
        assertThat(contactRepo.getAllUsersAt("Gabrovo"), `is`(equalTo(listOf(user1, user2))))
    }

    @Test
    fun getAllRegisteredUsersAtTheSameTownAndTheSameStreet() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        pretendThatRepositoryContainsUsers(
                user1, user2, User(3, "Stanimir", "Iliev", 20)
        )
        pretendThatRepositoryContainsAddresses(
                Address(1, "Vidima", 17, "Gabrovo"),
                Address(2, "Zelena Livada", 30, "Gabrovo")
        )
        pretendThatRepositoryContainsContacts(
                Contact("Gosho", 1, 1),
                Contact("Velizar", 2, 1),
                Contact("Stanimir", 3, 2)
        )
        assertThat(contactRepo.getAllUsersAt("Gabrovo", "Vidima"), `is`(equalTo(listOf(user1, user2))))
    }

    @Test
    fun getAllRegisteredUsersAtTheSameAddress() {
        val user1 = User(1, "Georgi", "Vasilev", 20)
        val user2 = User(2, "Velizar", "Vasilev", 19)
        pretendThatRepositoryContainsUsers(
                user1, user2, User(3, "Stanimir", "Iliev", 20))
        pretendThatRepositoryContainsAddresses(
                Address(1, "Vidima", 17, "Gabrovo"),
                Address(2, "Vidima", 30, "Gabrovo"))
        pretendThatRepositoryContainsContacts(
                Contact("Gosho", 1, 1),
                Contact("Velizar", 2, 1),
                Contact("Stanimir", 3, 2))
        assertThat(contactRepo.getAllUsersAt("Gabrovo", "Vidima", 17), `is`(equalTo(listOf(user1, user2))))
    }

    private fun pretendThatRepositoryContainsUsers(vararg users: User) {
        for(user in users) {
            userRepo.register(user)
        }
    }

    private fun pretendThatRepositoryContainsAddresses(vararg addresses: Address) {
        for(address in addresses) {
            addressRepo.register(address)
        }
    }

    private fun pretendThatRepositoryContainsContacts(vararg contacts: Contact) {
        for(contact in contacts) {
            contactRepo.register(contact)
        }
    }
}