package com.clouway.trip.adapter.jdbc

import com.clouway.trip.Customer
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.FileReader
import java.sql.DriverManager
import java.time.LocalDate

class JdbcTourAgencyTest {

    val customerTable = "Customers"
    val tripTable = "Trips"
    val con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tripdb",
            "root", "1234")
    val tourManagement = JdbcTourAgency(con, customerTable, tripTable)

    @Before
    fun setUp() {
        val statement = con.createStatement()
        statement.execute("DROP TABLE IF EXISTS Trips")
        statement.execute("DROP TABLE IF EXISTS Customers")
        statement.execute(FileReader("schema/Customers.sql").readText())
        statement.execute(FileReader("schema/Trips.sql").readText())
    }

    @Test
    fun getCustomerThatWasRegistered() {
        val customer = Customer("Stanimir", 9709090000, 20)
        val customerRepo = JdbcCustomerRepository(con, customerTable)
        customerRepo.registerCustomer(customer)
        assertThat(tourManagement.getCustomers().size, `is`(equalTo(1)))
        assertThat(tourManagement.getCustomers().last(), `is`(equalTo(customer)))
    }

    @Test
    fun getTripThatWasRegistered() {
        val customer = Customer("Stanimir", 9709090000, 20)
        val customerRepo = JdbcCustomerRepository(con, customerTable)
        customerRepo.registerCustomer(customer)
        val trip = tourManagement.registerTrip(customer.id, "Gabrovo", LocalDate.now(), LocalDate.now())
        assertThat(tourManagement.getTrips().size, `is`(equalTo(1)))
        assertThat(tourManagement.getTrips().last(), `is`(equalTo(trip)))
    }

    @Test
    fun updateAvailableCustomer() {
        val customer = Customer("Stanimir", 9709090000, 20)
        val customerRepo = JdbcCustomerRepository(con, customerTable)
        customerRepo.registerCustomer(customer)
        customerRepo.updateCustomer(customer, "Stanimir", 9709090000, 21, "")
        assertThat(tourManagement.getCustomers().size, `is`(equalTo(1)))
        assertThat(tourManagement.getCustomers().last(), `is`(equalTo(customer.apply { age = 21 })))
    }

    @Test
    fun updateAvailableTrip() {
        val customer = Customer("Stanimir", 9709090000, 20)
        val customerRepo = JdbcCustomerRepository(con, customerTable)
        customerRepo.registerCustomer(customer)
        val trip = tourManagement.registerTrip(customer.id, "Gabrovo",
                LocalDate.of(2017, 5, 6),
                LocalDate.of(2017, 5, 7))
        tourManagement.updateTrip(trip!!, LocalDate.of(2017, 5, 9),
                LocalDate.of(2017, 5, 6), "Gabrovo")
        assertThat(tourManagement.getTrips().size, `is`(equalTo(1)))
        assertThat(tourManagement.getTrips().last(),
                `is`(equalTo(trip.apply { leaveDate = LocalDate.of(2017, 5, 9) })))
    }

    @Test
    fun tryToRegisterTripWithNotRegisteredUser() {
        val unRegisteredCustomer = Customer("Stanimir", 9709090000, 20)
        val trip = tourManagement.registerTrip(unRegisteredCustomer.id, "", LocalDate.now(), LocalDate.now())
        assertThat(trip, `is`(nullValue()))
    }

    @Test
    fun getOneUserFromTwoByPrefixInName() {
        val customer1 = Customer("Stanimir", 9709090000, 20)
        val customer2 = Customer("Ivan", 9709090001, 20)
        val customerRepo = JdbcCustomerRepository(con, customerTable)
        customerRepo.registerCustomer(customer1)
        customerRepo.registerCustomer(customer2)
        assertThat(tourManagement.getCustomersWithPrefixInName("S").size, `is`(equalTo(1)))
        assertThat(tourManagement.getCustomersWithPrefixInName("S").last(), `is`(equalTo(customer1)))
    }

    @Test
    fun getOnlyCustomersInTheSameTownAtTheSameTimeFromMany() {
        val customer1 = Customer("Stanimir", 9709090000, 20)
        val customer2 = Customer("Ivan", 9709090001, 20)
        val customer3 = Customer("Vasil", 9709090002, 20)
        val customer4 = Customer("Hristo", 9709090003, 20)
        val customerRepo = JdbcCustomerRepository(con, customerTable)
        customerRepo.registerCustomer(customer1)
        customerRepo.registerCustomer(customer2)
        customerRepo.registerCustomer(customer3)
        customerRepo.registerCustomer(customer4)
        tourManagement.registerTrip(customer1.id, "Gabrovo",
                LocalDate.of(2017, 5, 5),
                LocalDate.of(2017, 5, 10))
        tourManagement.registerTrip(customer2.id, "Gabrovo",
                LocalDate.of(2017, 1, 1),
                LocalDate.of(2017, 1, 2))
        tourManagement.registerTrip(customer3.id, "Veliko Tarnovo",
                LocalDate.of(2017, 5, 5),
                LocalDate.of(2017, 5, 10))
        tourManagement.registerTrip(customer4.id, "Gabrovo",
                LocalDate.of(2017, 5, 2),
                LocalDate.of(2017, 5, 6))

        assertThat(tourManagement.getMatchingCustomers().get("Gabrovo"), `is`(equalTo(listOf(customer1, customer4))))
    }

    @Test
    fun getListWithMostVisitedCitiesThatWasAdded() {
        val customer1 = Customer("Stanimir", 9709090000, 20)
        val customer2 = Customer("Ivan", 9709090001, 20)
        val customer3 = Customer("Vasil", 9709090002, 20)
        val customerRepo = JdbcCustomerRepository(con, customerTable)
        customerRepo.registerCustomer(customer1)
        customerRepo.registerCustomer(customer2)
        customerRepo.registerCustomer(customer3)
        tourManagement.registerTrip(customer1.id, "Gabrovo", LocalDate.now(), LocalDate.now())
        tourManagement.registerTrip(customer2.id, "Gabrovo", LocalDate.now(), LocalDate.now())
        tourManagement.registerTrip(customer3.id, "Veliko Tarnovo", LocalDate.now(), LocalDate.now())

        assertThat(tourManagement.getMostVisitedCities(), `is`(equalTo(listOf("Veliko Tarnovo", "Gabrovo"))))
    }
}