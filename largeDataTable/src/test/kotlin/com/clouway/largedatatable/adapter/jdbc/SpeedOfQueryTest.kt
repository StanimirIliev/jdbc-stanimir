package com.clouway.largedatatable.adapter.jdbc

import com.clouway.largedatatable.core.User
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.FileReader
import java.sql.DriverManager

class SpeedOfQueryTest {

    val con  = DriverManager.getConnection("jdbc:mysql://${System.getenv("DB_HOST")}/" +
                        System.getenv("DB_TABLE"), System.getenv("DB_USER"), System.getenv("DB_PASS"))
    val userRepo = JdbcUserRepository(con, "Users")

    @Before
    fun createTableAndFillItWithAMillionRows() {
        val statement = con.createStatement()
        statement.execute("DROP TABLE IF EXISTS Users")
        statement.execute(FileReader("schema/Users.sql").readText())
        for(i in 0..3) {
            val query = StringBuilder("INSERT INTO Users(FirstName, LastName, Age, Phone) VALUES")
            for(j in 1..250_000) {
                query.append("('Stanimir', 'Iliev', 20, '${i * 250_000 + j}'),")
            }
            query.deleteCharAt(query.length - 1)
            statement.execute(query.toString())
        }
    }

    @Test
    fun getResultFromLargeDataBaseByPrimaryKey() {
        val expectedUser = User(500_000 , "Stanimir", "Iliev", 20, "500000")
        assertThat(userRepo.getById(500_000), `is`(equalTo(expectedUser)))
    }

    @Test
    fun getResultFromLargeDataBaseByUniqueKey() {
        val expectedUser = User(500_000 , "Stanimir", "Iliev", 20, "500000")
        assertThat(userRepo.getByNameAndPhone("Stanimir", "Iliev", "500000"), `is`(equalTo(expectedUser)))
    }
}