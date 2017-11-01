package com.clouway.trip.adapter.jdbc

import com.clouway.trip.core.CustomerRepository
import com.clouway.trip.Customer
import com.clouway.trip.Trip
import com.clouway.trip.core.TourAgency
import java.sql.Connection
import java.time.LocalDate

class JdbcTourAgency(val con: Connection, private val customerTable: String, private val tripTable: String) : TourAgency {

    override fun registerTrip(customerId: Long, town: String, arriveDate: LocalDate, leaveDate: LocalDate): Trip? {
        if(!JdbcCustomerRepository(con, customerTable).isRegistered(customerId)){
            return null
        }
        val statement = con.createStatement()
        statement.execute("INSERT INTO $tripTable(CustomerId, ArriveDate, LeaveDate, Town) VALUES(" +
                "(SELECT PersonalID FROM $customerTable WHERE PersonalId=$customerId)," +
                "'${arriveDate}'," +
                "'${leaveDate}'," +
                "'${town}')")
        val result = statement.executeQuery("SELECT MAX(TripId) AS TripId FROM Trips")
        result.next()
        return Trip(result.getInt("TripId"), town, arriveDate, leaveDate)
    }

    override fun updateTrip(trip: Trip, leaveDate: LocalDate, arriveDate: LocalDate, town: String) {
        trip.town = town
        trip.leaveDate = leaveDate
        trip.arriveDate = arriveDate
        val statement = con.createStatement()
        statement.execute("UPDATE $tripTable SET Town='$town',ArriveDate='$arriveDate',LeaveDate='$leaveDate' " +
                "WHERE TripId=${trip.tripId}")
    }

    override fun getTrips(): List<Trip> {
        val list = ArrayList<Trip>()
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $tripTable")
        while (result.next()) {
            val trip = Trip(result.getInt("TripId"), result.getString("Town"),
                    result.getDate("ArriveDate").toLocalDate(),
                    result.getDate("LeaveDate").toLocalDate())
            list.add(trip)
        }
        return list
    }

    override fun getCustomers(): List<Customer> {
        val list = ArrayList<Customer>()
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $customerTable")
        while (result.next()) {
            val customer = Customer(result.getString("Name"), result.getLong("PersonalId"),
                    result.getInt("Age"), result.getString("Email"))
            list.add(customer)
        }
        return list
    }

    override fun getCustomersWithPrefixInName(prefix: String): List<Customer> {
        val list = ArrayList<Customer>()
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT * FROM $customerTable WHERE Name LIKE '$prefix%'")
        while (result.next()) {
            val customer = Customer(result.getString("Name"), result.getLong("PersonalId"),
                    result.getInt("Age"), result.getString("Email"))
            list.add(customer)
        }
        return list
    }

    /**
     * Returns map with the customers that has been in the same town and in the same time
     * Key -> Name of the town: String
     * Value -> customers that has been there at the same time: List<Costumer>
     */
    override fun getMatchingCustomers(): Map<String, List<Customer>> {

        val statement = con.createStatement()
        val output = LinkedHashMap<String, List<Customer>>()

        //  1. Make duplicate of the trips table where there is duplicate towns
        statement.execute("CREATE TABLE DuplicateTownsTrips AS SELECT * FROM " +
                "Trips WHERE Town IN(SELECT Town FROM Trips GROUP BY Town HAVING COUNT(*) >=2)")
        while (true) {
            //  2. Check if there is any rows in DuplicateTownsTrips table
            if (!statement.executeQuery("SELECT * FROM DuplicateTownsTrips").next()) {
                break
            }
            //  3. Set variables for the last added row
            statement.execute("SET @tripId = (SELECT MAX(TripId) FROM DuplicateTownsTrips)")
            statement.execute("SET @arriveDate = (SELECT ArriveDate FROM DuplicateTownsTrips WHERE TripId = @tripId)")
            statement.execute("SET @leaveDate = (SELECT LeaveDate FROM DuplicateTownsTrips WHERE TripId = @tripId)")
            statement.execute("SET @townName = (SELECT Town FROM DuplicateTownsTrips WHERE TripId = @tripId)")
            //  4. Creates table with the matched customers in the first town
            statement.execute("CREATE TABLE MatchedCustomers AS SELECT * FROM DuplicateTownsTrips " +
                    "WHERE Town = @townName AND (ArriveDate >= @arriveDate AND ArriveDate <= @leaveDate OR " +
                    "LeaveDate >= @arriveDate)")
            statement
            //  5. Gets result
            var result = statement.executeQuery("SELECT CustomerId FROM MatchedCustomers")
            val customerIds = ArrayList<Long>()
            while (result.next()) {
                customerIds.add(result.getLong("customerId"))
            }
            val customers = ArrayList<Customer>()
            for (customerId in customerIds) {
                result = statement.executeQuery("SELECT * FROM $customerTable WHERE PersonalId = $customerId")
                result.next()
                customers.add(Customer(result.getString("Name"), customerId,
                        result.getInt("Age"), result.getString("Email")))
            }
            result = statement.executeQuery("SELECT @townName")
            result.next()
            output.put(result.getString("@townName"), customers)
            //  6. Delete the table with matched customers
            statement.execute("DROP TABLE MatchedCustomers")
            //  7. Delete this town from DuplicateTownsTrips table
            statement.execute("DELETE FROM DuplicateTownsTrips WHERE Town = @townName")
        }
        //  8. Delete DuplicateTownsTrips table
        statement.execute("DROP TABLE DuplicateTownsTrips")
        return output
    }

    /**
     * Returns list with most visited cities in ascending order
     * return List<String>
     */
    override fun getMostVisitedCities(): List<String> {
        val output = ArrayList<String>()
        val statement = con.createStatement()
        val result = statement.executeQuery("SELECT Town FROM Trips GROUP BY Town ORDER BY COUNT(TripId)")
        while (result.next()) {
            output.add(result.getString("Town"))
        }
        return output
    }
}