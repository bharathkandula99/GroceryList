package com.tilicho.grocery.mangement.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff


class SplashActivity : AppCompatActivity() {

    //private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // mAuth = FirebaseAuth.getInstance();
    }

    override fun onStart() {
        super.onStart()
        if (AppPreff(this).getUserID() != null && AppPreff(this).getUserID()?.isNotEmpty()!!) {
            redirectToHomeActivity()
        } else {
            redirectToSignup()
        }
    }

    private fun redirectToSignup() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun redirectToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}