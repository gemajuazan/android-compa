package org.example.compa.models

import java.io.Serializable

data class Payment (
    val id: String,
    val transmitter: String,
    val receiver: String,
    val date: Long,
    val price: Double,
    val concept: String,
    val typePayment: String,
    val statusPayment: String
) : Serializable