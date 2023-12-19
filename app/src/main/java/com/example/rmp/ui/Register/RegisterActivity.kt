package com.example.rmp.ui.Register

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.rmp.MainActivity
import com.example.rmp.R
import com.example.rmp.data.DatabaseHelper

class RegisterActivity : AppCompatActivity() {
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etGenderSpinner: Spinner
    private lateinit var etBirthday : EditText
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etFirstName = findViewById(R.id.firstName)
        etUsername = findViewById(R.id.username)
        etPassword = findViewById(R.id.password)
        etConfirmPassword = findViewById(R.id.confirmPassword)
        etGenderSpinner = findViewById(R.id.genderSpinner)
        etBirthday = findViewById(R.id.dateOfBirth)

        val genderArrayAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        )
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        etGenderSpinner.adapter = genderArrayAdapter

        val registerButton: Button = findViewById(R.id.register)
        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val firstName = etFirstName.text.toString()
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        val gender = etGenderSpinner.selectedItem.toString()
        val birthDay = etBirthday.text.toString()

        if (password == confirmPassword) {
            databaseHelper = DatabaseHelper.getInstance(applicationContext)!!
            databaseHelper.writableDatabase.use { db ->
                val contentValues = ContentValues().apply {
                    put("login", username)
                    put("password", password)
                    put("name", firstName)
                    put("gender", gender)
                    put("birthdate", birthDay)
                }
                val result = db.insert("User", null, contentValues)
                if (result != -1L) {
                    println("Регистрация выполнена успешно")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    println("Ошибка регистрации")
                }
            }
        } else {
            // Пароли не совпадают, выводим сообщение об ошибке
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
        }
    }

}