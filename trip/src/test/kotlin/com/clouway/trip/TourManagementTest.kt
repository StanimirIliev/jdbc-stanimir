package com.clouway.trip

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.FileReader
import java.sql.DriverManager
import java.time.LocalDate

class TourManagementTest {

    val customerTable = "Customers"
    val tripTable = "Trips"
    val con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tripdb",
            "root", "1234")
    val tourManagement = TourManagement(con, customerTable, tripTable)

    @Before
    fun setUp() {
        val statement = con.createStatement()
        statement.execute("DROP TABLE IF EXISTS Trips")
        statement.execute("DROP TABLE IF EXISTS Customers")
        statement.execute(FileReader("schema/Customers.sql").readText())
        statement.execute(FileReader("schema/Trips.sql").readText())
    }

    @Test
    fun getCustomerThatWasAdded() {
        val customer = Customer("Stanimir", 9709090000, 20)
        tourManagement.newCustomer(customer)
        assertThat(tourManagement.getCustomers().size, `is`(equalTo(1)))
        assertThat(tourManagement.getCustomers().last(), `is`(equalTo(customer)))
    }

    @Test
    fun getTripThatWasAdded() {
        val customer = Customer("Stanimir", 9709090000, 20)
        tourManagement.newCustomer(customer)
        val trip = tourManagement.newTrip(customer.id, "Gabrovo", LocalDate.now(), LocalDate.now())
        assertThat(tourManagement.getTrips().size, `is`(equalTo(1)))
        assertThat(tourManagement.getTrips().last(), `is`(equalTo(trip)))
    }

    @Test
    fun updateAvailableCustomer() {
        val customer = Customer("Stanimir", 9709090000, 20)
        tourManagement.newCustomer(customer)
        tourManagement.updateCustomer(customer, "Stanimir", 9709090000, 21)
        assertThat(tourManagement.getCustomers().size, `is`(equalTo(1)))
        assertThat(tourManagement.getCustomers().last(), `is`(equalTo(customer.apply { age = 21 })))
    }

    @Test
    fun updateAvailableTrip() {
        val customer = Customer("Stanimir", 9709090000, 20)
        tourManagement.newCustomer(customer)
        val trip = tourManagement.newTrip(customer.id, "Gabrovo",
                LocalDate.of(2017, 5, 6),
                LocalDate.of(2017, 5, 7))
        tourManagement.updateTrip(trip, LocalDate.of(2017, 5, 9))
        assertThat(tourManagement.getTrips().size, `is`(equalTo(1)))
        assertThat(tourManagement.getTrips().last(),
                `is`(equalTo(trip.apply{leaveDate = LocalDate.of(2017, 5, 9)})))
    }

    @Test
    fun getOneUserFromTwoByPrefixInName() {
        val customer1 = Customer("Stanimir", 9709090000, 20)
        val customer2 = Customer("Ivan", 9709090001, 20)
        tourManagement.newCustomer(customer1)
        tourManagement.newCustomer(customer2)
        assertThat(tourManagement.getCustomersWithPrefixInName("S").size, `is`(equalTo(1)))
        assertThat(tourManagement.getCustomersWithPrefixInName("S").last(), `is`(equalTo(customer1)))
    }

    @Test
    fun getOnlyCustomersInTheSameTownAtTheSameTimeFromMany() {
        val customer1 = Customer("Stanimir", 9709090000, 20)
        val customer2 = Customer("Ivan", 9709090001, 20)
        val customer3 = Customer("Vasil", 9709090002, 20)
        val customer4 = Customer("Hristo", 9709090003, 20)
        tourManagement.newCustomer(customer1)
        tourManagement.newCustomer(customer2)
        tourManagement.newCustomer(customer3)
        tourManagement.newCustomer(customer4)
        tourManagement.newTrip(customer1.id, "Gabrovo",
                LocalDate.of(2017, 5, 5),
                LocalDate.of(2017, 5, 10))
        tourManagement.newTrip(customer2.id, "Gabrovo",
                LocalDate.of(2017, 1, 1),
                LocalDate.of(2017, 1, 2))
        tourManagement.newTrip(customer3.id, "Veliko Tarnovo",
                LocalDate.of(2017, 5, 5),
                LocalDate.of(2017, 5, 10))
        tourManagement.newTrip(customer4.id, "Gabrovo",
                LocalDate.of(2017, 5, 2),
                LocalDate.of(2017, 5, 6))

        assertThat(tourManagement.getMatchingCustomers().get("Gabrovo"), `is`(equalTo(listOf(customer1, customer4))))
    }

    @Test
    fun getListWithMostVisitedCitiesThatWasAdded() {
        val customer1 = Customer("Stanimir", 9709090000, 20)
        val customer2 = Customer("Ivan", 9709090001, 20)
        val customer3 = Customer("Vasil", 9709090002, 20)
        tourManagement.newCustomer(customer1)
        tourManagement.newCustomer(customer2)
        tourManagement.newCustomer(customer3)
        tourManagement.newTrip(customer1.id, "Gabrovo", LocalDate.now(), LocalDate.now())
        tourManagement.newTrip(customer2.id, "Gabrovo", LocalDate.now(),  LocalDate.now())
        tourManagement.newTrip(customer3.id, "Veliko Tarnovo", LocalDate.now(), LocalDate.now())

        assertThat(tourManagement.getMostVisitedCities(), `is`(equalTo(listOf("Veliko Tarnovo", "Gabrovo"))))
    }

    @Test
    fun clearCustomersTable() {
        tourManagement.newCustomer(Customer("Stanimir", 9709090000, 20))
        tourManagement.clearTable(Table.Customers)
        assertThat(tourManagement.getCustomers().size, `is`(equalTo(0)))
    }

    @Test
    fun clearTripsTable() {
        val customer = Customer("Stanimir", 9709090000, 20)
        tourManagement.newCustomer(customer)
        tourManagement.newTrip(customer.id, "", LocalDate.now(), LocalDate.now())
        tourManagement.clearTable(Table.Trips)
        assertThat(tourManagement.getTrips().size, `is`(equalTo(0)))
    }
}