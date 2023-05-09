package com.tilicho.grocery.mangement.api

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager {
    companion object{
        private var mAuth: FirebaseAuth? = null
        private var authManager: FirebaseAuthManager? = null

        fun getInstance() : FirebaseAuthManager{
            if(authManager == null){
                authManager = FirebaseAuthManager()
                mAuth = FirebaseAuth.getInstance();
            }

            return authManager as FirebaseAuthManager
        }
    }

    fun performSignUp(email : String, password : String) : MutableLiveData<FirebaseResult<AuthResult>> {
        lateinit var authResultLiveData : MutableLiveData<FirebaseResult<AuthResult>>
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    val authResult : FirebaseResult<AuthResult> = FirebaseResult()
                    authResult.isSuccessful = it.isSuccessful

                    if(it.isSuccessful){
                        authResult.result = it.result!!
                    }
                }
                .addOnFailureListener {
                    val authResult : FirebaseResult<AuthResult> = FirebaseResult()
                    authResult.isSuccessful = false
                    authResult.errorMessage = it.localizedMessage
                }

        return authResultLiveData
    }

    fun performSignIn(email : String, password : String) : MutableLiveData<FirebaseResult<AuthResult>>{
        lateinit var authResultLiveData : MutableLiveData<FirebaseResult<AuthResult>>
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    val authResult : FirebaseResult<AuthResult> = FirebaseResult()
                    authResult.isSuccessful = it.isSuccessful

                    if(it.isSuccessful){
                        authResult.result = it.result!!
                    }

                    authResultLiveData.value = authResult
                }
                .addOnFailureListener {
                    val authResult : FirebaseResult<AuthResult> = FirebaseResult()
                    authResult.isSuccessful = false
                    authResult.errorMessage = it.localizedMessage

                    authResultLiveData.value = authResult
                }

        return authResultLiveData
    }

    fun performForgotPassword(email: String) : MutableLiveData<FirebaseResult<AuthResult>> {
        lateinit var authResultLiveData : MutableLiveData<FirebaseResult<AuthResult>>
        mAuth!!.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    val authResult : FirebaseResult<AuthResult> = FirebaseResult()
                    authResult.isSuccessful = it.isSuccessful

                    authResultLiveData.value = authResult
                }
                .addOnFailureListener {
                    val authResult : FirebaseResult<AuthResult> = FirebaseResult()
                    authResult.isSuccessful = false
                    authResult.errorMessage = it.localizedMessage

                    authResultLiveData.value = authResult
                }

        return authResultLiveData
    }

    fun performLogout() {
        //TODO Integrate this in Settings screen
        mAuth!!.signOut()
    }
}

class FirebaseResult<T> {

    var isSuccessful: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    var errorMessage: String = ""
        get() = field
        set(value) {field = value}

    var result: T? = null
        get()  = field
        set(value) {field = value}
}