package org.example.compa.models

import java.io.Serializable

data class Member(
    val id: String,
    val name: String,
    val username: String,
    val email: String
) : Serializable