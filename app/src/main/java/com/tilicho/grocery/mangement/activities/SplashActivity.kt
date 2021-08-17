package com.tilicho.grocery.mangement.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance();
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            //redirectToHome()
        } else {
            redirectToSignup()
        }
    }

    private fun redirectToSignup(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}