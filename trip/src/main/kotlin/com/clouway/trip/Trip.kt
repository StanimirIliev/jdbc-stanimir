package com.clouway.trip

import java.time.LocalDate

open class Trip(var tripId: Int, var town: String, var arriveDate: LocalDate, var leaveDate: LocalDate) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Trip

        if (town != other.town) return false
        if (arriveDate != other.arriveDate) return false
        if (leaveDate != other.leaveDate) return false
        if (tripId != other.tripId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = town.hashCode()
        result = 31 * result + arriveDate.hashCode()
        result = 31 * result + leaveDate.hashCode()
        result = 31 * result + tripId
        return result
    }

    override fun toString(): String {
        return "Trip(town='$town', arriveDate=$arriveDate, leaveDate=$leaveDate, tripId=$tripId)"
    }
}