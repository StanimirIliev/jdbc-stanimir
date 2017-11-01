package com.clouway.trip

import java.time.LocalDate

data class Trip(var tripId: Int, var town: String, var arriveDate: LocalDate, var leaveDate: LocalDate)