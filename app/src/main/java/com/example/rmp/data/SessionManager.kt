package com.example.rmp.data

import android.content.Context
import android.widget.EditText
import com.example.rmp.data.model.LoggedInUser

class SessionManager(private val context: Context) {

    private var currentUser : LoggedInUser? = null

    fun getCurrentUser() : LoggedInUser? {
        return currentUser
    }

    companion object {
        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }


    fun createSession(username: String, password: String): Boolean {
        val databaseHelper = DatabaseHelper.getInstance(context)

        // Проверка наличия пользователя в базе данных
        val user = databaseHelper?.getUserByUsernameAndPassword(username, password)

        if (user != null) {
            // Пользователь найден, создаем сессию
            currentUser = user
            val sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userId", user.userId)
            editor.putString("displayName", user.displayName)
            editor.putString("userPassword", user.password)
            editor.putString("displayGender", user.displayGender)
            editor.putString("displayBirthday", user.displayBirthday)
            editor.apply()
            return true
        }

        return false
    }

    fun isLoggedIn(): Boolean {
        val sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)
        val displayName = sharedPreferences.getString("displayName", null)
        val userPassword = sharedPreferences.getString("userPassword", null)
        val displayGender= sharedPreferences.getString("displayGender", null)
        val displayBirthday = sharedPreferences.getString("displayBirthday", null)
            if (displayName != null && userPassword != null && displayGender != null && displayBirthday != null ) {
                val user = userId?.let {
                    LoggedInUser(
                        it,
                        displayName,
                        userPassword,
                        displayGender,
                        displayBirthday
                    )
                }
                currentUser = user
            }


        return userId != null
    }

    fun logout() {
        val sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("userId")
        editor.apply()
    }


}