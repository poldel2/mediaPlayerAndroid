package com.example.rmp.data

import android.content.Context
import com.example.rmp.MainActivity
import com.example.rmp.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val databaseHelper: DatabaseHelper) {

    fun login(userLogin: String, password: String): Result<LoggedInUser> {

        val database = databaseHelper.readableDatabase


        val projection = arrayOf("login", "password", "name", "gender", "birthdate", "photo")
        val cursor = database.query("User", projection, null, null, null, null, null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val userLogin = cursor.getString(cursor.getColumnIndexOrThrow("login"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))

                println("Login: $userLogin, Password: $password, Name: $name")
            }
            cursor.close()
        }
        database.close()

        try {
            val user = databaseHelper.getUserByUsernameAndPassword(userLogin, password)
            //val user = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            if (user != null) {
                return Result.Success(user)
            } else {
                return Result.Error(Exception("Некорректные учетные данные"))
            }
        } catch (e: Throwable) {
           return Result.Error(IOException("Ошибка аутентификации", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}