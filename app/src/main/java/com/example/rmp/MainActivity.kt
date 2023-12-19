package com.example.rmp

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rmp.data.DatabaseHelper
import com.example.rmp.data.SessionManager
import com.example.rmp.databinding.ActivityMainBinding
import com.example.rmp.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable


class MainActivity : AppCompatActivity(), Serializable {

    private lateinit var binding: ActivityMainBinding
   private lateinit var databaseHelper: DatabaseHelper
    private var sessionManager: SessionManager? = null

    private val sessionManagerInstance: SessionManager
        get() {
            if (sessionManager == null) {
                sessionManager = SessionManager.getInstance(this)
            }
            return sessionManager as SessionManager
        }

    fun getDatabaseHelper(): DatabaseHelper {
        return databaseHelper
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseHelper = DatabaseHelper.getInstance(applicationContext)!!
        val database: SQLiteDatabase = databaseHelper.getDatabase()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (sessionManagerInstance.isLoggedIn()) {
            // navigateToHome()
            println("Сессия есть")
        } else {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            println("Сессия net")
        }


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setupWithNavController(navController)
    }
}