package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tilicho.grocery.mangement.api.FirebaseAuthManager
import com.tilicho.grocery.mangement.api.FirebaseResult
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.UserModel
import java.util.*

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private var mAuth: FirebaseAuth? = null
    private var authManager: FirebaseAuthManager? = null
    var mGlobalDatabase: DatabaseReference? = null
    var mUserDatabase: DatabaseReference? = null
    var mDefaultsDatabase: DatabaseReference? = null

    private val _loginUIShown: MutableLiveData<Boolean> = MutableLiveData(true)

    val firstName: MutableLiveData<String> = MutableLiveData("")
    val lastName: MutableLiveData<String> = MutableLiveData("")
    //val email: MutableLiveData<String> = MutableLiveData("")

    private val _loginAuthResult: MutableLiveData<FirebaseResult<AuthResult>> = MutableLiveData()
    val loginAuthResult: LiveData<FirebaseResult<AuthResult>>
        get() = _loginAuthResult
    private val _signUpAuthResult: MutableLiveData<FirebaseResult<AuthResult>> = MutableLiveData()
    val signUpAuthResult: LiveData<FirebaseResult<AuthResult>>
        get() = _signUpAuthResult
    private val _resetPasswoardAuthResult: MutableLiveData<FirebaseResult<AuthResult>> =
        MutableLiveData()
    val resetPasswoardAuthResult: LiveData<FirebaseResult<AuthResult>>
        get() = _resetPasswoardAuthResult

    val _isSignUpSucess: MutableLiveData<Boolean> = MutableLiveData(false)
    val _isSignUpFailed: MutableLiveData<Boolean> = MutableLiveData(false)
    val _isLoginSucess: MutableLiveData<Boolean> = MutableLiveData(false)
    val _isLoginFailed: MutableLiveData<Boolean> = MutableLiveData(false)
    val _isRestPasswordnSucess: MutableLiveData<Boolean> = MutableLiveData(false)
    val _isRestPasswordFailed: MutableLiveData<Boolean> = MutableLiveData(false)


    ///  private val firebaseAuthManager: MutableLiveData<FirebaseAuthManager> = MutableLiveData()

    val loginUIShown: LiveData<Boolean>
        get() = _loginUIShown


    init {
        mGlobalDatabase = FirebaseDatabase.getInstance().getReference()
        mUserDatabase = FirebaseDatabase.getInstance().getReference("users")
        mDefaultsDatabase = FirebaseDatabase.getInstance().getReference("defaults")
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        //  firebaseAuthManager.value = FirebaseAuthManager.getInstance()
        if (authManager == null) {
            authManager = FirebaseAuthManager()
            mAuth = FirebaseAuth.getInstance();
        }
    }

    fun setUIState(loginUIShown: Boolean = true) {
        _loginUIShown.value = loginUIShown
    }

    fun performLogin(email: String, password: String) {
        val authResult: FirebaseResult<AuthResult> = FirebaseResult()

        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    authResult.isSuccessful = it.isSuccessful
                    authResult.result = it.result!!
                    _loginAuthResult.value = authResult
                    readUserDetails(it.result!!.user?.uid!!)
                } else {
                    authResult.isSuccessful = false
                    authResult.errorMessage =
                        it.exception?.localizedMessage ?: it.exception?.message ?: ""
                    _loginAuthResult.value = authResult
                    _isLoginFailed.value = true
                }
            }
            .addOnFailureListener {

                authResult.isSuccessful = false
                authResult.errorMessage = it.localizedMessage

                _loginAuthResult.value = authResult
                _isLoginFailed.value = true
            }
    }

    fun performSignUp(email: String, password: String) {
        val authResult: FirebaseResult<AuthResult> = FirebaseResult()

        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                /* authResult.isSuccessful = it.isSuccessful
                 authResult.result = it.result!!
                 _signUpAuthResult.value = authResult*/
                if (it.isSuccessful) {
                    authResult.isSuccessful = it.isSuccessful
                    authResult.result = it.result!!
                    _signUpAuthResult.value = authResult
                    createUser(
                        UserModel(
                            it.result?.user!!.uid, firstName.value ?: "", lastName.value ?: "",
                            it.result!!.user?.email ?: "", System.currentTimeMillis(), ""
                        )
                    )
                } else {
                    authResult.isSuccessful = false
                    authResult.errorMessage =
                        it.exception?.localizedMessage ?: it.exception?.message ?: ""
                    _signUpAuthResult.value = authResult
                    _isSignUpFailed.value = true
                }
            }
            .addOnFailureListener {
                authResult.isSuccessful = false
                authResult.errorMessage = it.localizedMessage
                _signUpAuthResult.value = authResult
                _isSignUpFailed.value = true
            }

    }

    fun performResetPassword(email: String) {
        val authResult: FirebaseResult<AuthResult> = FirebaseResult()

        mAuth!!.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    authResult.isSuccessful = it.isSuccessful
                    _resetPasswoardAuthResult.value = authResult
                    _isRestPasswordnSucess.value = true

                } else {
                    authResult.isSuccessful = false
                    authResult.errorMessage =
                        it.exception?.localizedMessage ?: it.exception?.message ?: ""
                    _loginAuthResult.value = authResult
                    _isRestPasswordFailed.value = true
                }
            }
            .addOnFailureListener {
                authResult.isSuccessful = false
                authResult.errorMessage = it.localizedMessage
                _resetPasswoardAuthResult.value = authResult
                _isRestPasswordFailed.value = true
            }
    }

    fun createUser(user: UserModel) {
        mUserDatabase?.child(user.id)?.setValue(user)
            ?.addOnCompleteListener {
                storeUserDatatoAppPreff(user)
                initDefaultUserData(user.id)
                _isSignUpSucess.value = true
            }?.addOnFailureListener {
                _isSignUpFailed.value = true
            }
    }

    private fun initDefaultUserData(id: String) {
        mDefaultsDatabase?.child("categories")?.get()
            ?.addOnCompleteListener {
                mGlobalDatabase?.child("categories")?.child(id)?.setValue(it.result?.getValue())
            }

        mDefaultsDatabase?.child("items")?.get()
            ?.addOnCompleteListener {
                mGlobalDatabase?.child("items")?.child(id)?.setValue(it.result?.getValue())
            }
    }

    private fun readUserDetails(userID: String) {

        mUserDatabase?.child(userID)?.get()
            ?.addOnCompleteListener {

                val user: UserModel? = it.result?.getValue(UserModel::class.java)
                if (user != null) {
                    storeUserDatatoAppPreff(user)
                    _isLoginSucess.value = true
                } else {
                    _isLoginFailed.value = true
                    _loginAuthResult.value?.errorMessage =
                        it.exception?.localizedMessage ?: it?.exception?.message ?: ""
                }
            }?.addOnFailureListener {
                _loginAuthResult.value?.errorMessage = it.localizedMessage ?: it.message ?: ""
                _isLoginFailed.value = true

            }
    }

    fun storeUserDatatoAppPreff(user: UserModel) {
        AppPreff(getApplication()).setFirstName(user?.firstName!!)
        AppPreff(getApplication()).setLastName(user.lastName)
        AppPreff(getApplication()).setUserEmail(user.email)
        AppPreff(getApplication()).setUserID(user.id)
    }


}