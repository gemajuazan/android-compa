package org.example.compa.models

import java.io.Serializable

data class Member(
    val id: Int,
    val name: String,
    val username: String,
    val email: String
) : Serializable