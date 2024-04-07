package com.galaxy.galaxynet.model

import com.google.firebase.auth.FirebaseAuth

data class Token(
    val id: String = FirebaseAuth.getInstance().currentUser?.uid.toString(),
    val tokenValue: String? = null
) {
    companion object {
        const val TOKENS_COLLECTION = "tokens"
    }
}
