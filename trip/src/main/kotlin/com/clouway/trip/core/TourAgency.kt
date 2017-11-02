package com.clouway.trip.core

import java.time.LocalDate

interface TourAgency {
    fun registerTrip(customerId: Long, town: String, arriveDate: LocalDate, leaveDate: LocalDate): Trip?
    fun updateTrip(trip: Trip, leaveDate: LocalDate, arriveDate: LocalDate, town: String)
    fun getTrips(): List<Trip>
    fun getCustomers(): List<Customer>
    fun getCustomersWithPrefixInName(prefix: String): List<Customer>
    /**
     * Returns map with the customers that has been in the same town and in the same time
     * Key -> Name of the town: String
     * Value -> customers that has been there at the same time: List<Costumer>
     */
    fun getMatchingCustomers(): Map<String, List<Customer>>
    /**
     * Returns list with most visited cities in ascending order
     * return List<String>
     */
    fun getMostVisitedCities(): List<String>
}