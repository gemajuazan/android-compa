package org.example.compa.models

import java.io.Serializable

data class Friend(
    val person: Person,
    val solicitude: Boolean,
    val favourite: Boolean
) : Serializable