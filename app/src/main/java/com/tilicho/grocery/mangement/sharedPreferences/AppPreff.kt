package com.tilicho.grocery.mangement.sharedPreferences

import android.content.Context

class AppPreff(context: Context) {
    val PREFERENCE_NAME = "GroczySharedPreference"
    val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    val EMAIL = "Email"
    val FIRSTNAME = "FirstName"
    val LASTNAME = "LastName"
    val USERID = "UserID"


    fun clearAppPref() {
        preference.edit().remove(EMAIL).apply();
    }

    fun setUserEmail(email: String) {
        return preference.edit().putString(EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return preference.getString(EMAIL, "")
    }

    fun setFirstName(firstName: String) {
        return preference.edit().putString(FIRSTNAME, firstName).apply()
    }

    fun getFirstName(): String? {
        return preference.getString(FIRSTNAME, "")
    }

    fun setLastName(lastName: String) {
        return preference.edit().putString(LASTNAME, lastName).apply()
    }

    fun getLastName(): String? {
        return preference.getString(LASTNAME, "")
    }

    fun setUserID(userID: String) {
        return preference.edit().putString(USERID, userID).apply()
    }

    fun getUserID(): String? {
        return preference.getString(USERID, "")
    }

}