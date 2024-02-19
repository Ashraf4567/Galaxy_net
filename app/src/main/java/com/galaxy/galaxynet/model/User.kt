package com.galaxy.galaxynet.model

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val type: String? = null,
    val points: Int? = null
){
    companion object {
        const val MANAGER = "مدير"
        const val EMPLOYEE = "موظف"
        const val COLLECTION_NAME = "users"
    }
}