package com.clouway.contacts.adapter.jdbc

import com.clouway.contacts.adapter.JdbcUserRepository
import com.clouway.contacts.core.User
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.FileReader
import java.sql.DriverManager

class JdbcUserRepositoryTest {

    val con = DriverManager.getConnection("jdbc:mysql://${System.getenv("DB_HOST")}/" +
            System.getenv("DB_TABLE"), System.getenv("DB_USER"), System.getenv("DB_PASS"))
    val userRepo = JdbcUserRepository(con, "Users")

    @Before
    fun setUp() {
        val statement = con.createStatement()
        statement.execute("DROP TABLE IF EXISTS Users")
        statement.execute(FileReader("schema/Users.sql").readText())
    }

    @Test
    fun getUserThatWasRegistered() {
        val user = User(1, "Stanimir", "Iliev", 20)
        userRepo.register(user)
        assertThat(userRepo.getById(1), `is`(equalTo(user)))
    }

    @Test
    fun tryToGetUnregistredUser() {
        assertThat(userRepo.getById(1), `is`(nullValue()))
    }

    @Test
    fun getRegisteredUsersByFirstName() {
        val user1 = User(1, "Stanimir", "Iliev", 20)
        val user2 = User(2, "Stanimir", "Petrov", 19)
        val user3 = User(3, "Vasil", "Hristov", 22)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        assertThat(userRepo.getByFirstName("Stanimir"), `is`(equalTo(listOf(user1, user2))))
    }

    @Test
    fun getRegisteredUsersByLastName() {
        val user1 = User(1, "Stanimir", "Iliev", 20)
        val user2 = User(2, "Stanimir", "Petrov", 19)
        val user3 = User(3, "Vasil", "Petrov", 22)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        assertThat(userRepo.getByLastName("Petrov"), `is`(equalTo(listOf(user2, user3))))
    }

    @Test
    fun getRegisteredUsersByAge() {
        val user1 = User(1, "Stanimir", "Iliev", 20)
        val user2 = User(2, "Stanimir", "Petrov", 19)
        val user3 = User(3, "Vasil", "Petrov", 20)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        assertThat(userRepo.getByAge(20), `is`(equalTo(listOf(user1, user3))))
    }

    @Test
    fun getAllRegisteredUsers() {
        val user1 = User(1, "Stanimir", "Iliev", 20)
        val user2 = User(2, "Stanimir", "Petrov", 19)
        val user3 = User(3, "Vasil", "Petrov", 20)
        userRepo.register(user1)
        userRepo.register(user2)
        userRepo.register(user3)
        assertThat(userRepo.getAll(), `is`(equalTo(listOf(user1, user2, user3))))
    }

    @Test
    fun tryToGetAllUsersWithEmptySet() {
        assertThat(userRepo.getAll().size, `is`(equalTo(0)))
    }
}