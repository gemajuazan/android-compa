package org.example.compa.models

import java.io.Serializable

data class Person(
    val id: Int,
    val name: String,
    val surnames: String,
    val birthdate: String,
    val email: String,
    val password: String
): Serializable