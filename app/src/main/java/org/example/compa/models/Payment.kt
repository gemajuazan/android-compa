package org.example.compa.models

import java.io.Serializable

data class Payment (
    val id: Int,
    val transmitter: String,
    val receiver: String,
    val date: String,
    val price: Double,
    val concept: String,
    val statusPayment: String
) : Serializable