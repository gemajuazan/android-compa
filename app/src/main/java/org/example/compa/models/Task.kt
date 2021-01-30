package org.example.compa.models

import java.io.Serializable

data class Task(
    val id: Int,
    val name: String,
    val startDate: String,
    val finishDate: String,
    val category: String,
    val numberMembers: Int,
    val members: List<String>,
    val description: String
) : Serializable