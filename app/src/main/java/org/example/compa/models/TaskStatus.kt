package org.example.compa.models

data class TaskStatus(
    val id: String,
    val percentage: Int,
    val members: ArrayList<MemberTask> = arrayListOf()
)