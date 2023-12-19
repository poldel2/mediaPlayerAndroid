package com.example.rmp.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    var userId: String,
    val displayName: String,
    val password: String,
    val displayGender: String,
    val displayBirthday: String
)