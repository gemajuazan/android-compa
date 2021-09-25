package org.example.compa.models

import java.io.Serializable

data class Person(
    val id: String,
    val name: String,
    val surnames: String,
    val birthdate: Long?,
    val email: String,
    val username: String,
    val phone: String,
    val image: String
) : Serializable