package com.galaxy.galaxynet.model

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val type: String? = null,
    val points: String? = null
){
    companion object{
        const val MANAGER = "manger"
        const val EMPLOYEE = "employee"
        const val COLLECTION_NAME = "tasks"
    }
}