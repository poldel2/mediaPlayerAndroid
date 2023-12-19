package com.example.rmp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rmp.data.DatabaseHelper
import com.example.rmp.data.LoginDataSource
import com.example.rmp.data.LoginRepository
import android.database.sqlite.SQLiteDatabase

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(private val databaseHelper: DatabaseHelper)
 : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                   // dataSource = LoginDataSource(databaseHelper)

                    dataSource  = LoginDataSource(databaseHelper)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}