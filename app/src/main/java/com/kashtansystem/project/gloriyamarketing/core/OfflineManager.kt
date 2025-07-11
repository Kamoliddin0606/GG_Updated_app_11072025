package com.kashtansystem.project.gloriyamarketing.core

import android.content.Context
import android.content.SharedPreferences
import com.kashtansystem.project.gloriyamarketing.utils.UserType

object OfflineManager {

    lateinit var preferences: SharedPreferences
    var goOffline = false

    fun init(context: Context) {
        preferences = context.getSharedPreferences("offline.gm", Context.MODE_PRIVATE)
    }
    var login: String
        get() = preferences.getString(::login.name, "") ?: ""
        set(value) {
            preferences.edit().putString(::login.name, value).apply()
        }
    var password: String
        get() = preferences.getString(::password.name, "")?: ""
        set(value) {
            preferences.edit().putString(::password.name, value).apply()
        }

    var projectURL: String
        get() = preferences.getString(::projectURL.name, "")?: ""
        set(value) {
            preferences.edit().putString(::projectURL.name, value).apply()
        }

    var code: String
        get() = preferences.getString(::code.name, "")?: ""
        set(value) {
            preferences.edit().putString(::code.name, value).apply()
        }

    var name: String
        get() = preferences.getString(::name.name, "")?: ""
        set(value) {
            preferences.edit().putString(::name.name, value).apply()
        }

    var codeProject: String
        get() = preferences.getString(::codeProject.name, "")?: ""
        set(value) {
            preferences.edit().putString(::codeProject.name, value).apply()
        }

    var userType: UserType
        get():UserType{
            return UserType.getUserTypeByValue(preferences.getInt(::userType.name,1))
        }
        set(value) {
            preferences.edit().putInt(::userType.name, value.value).apply()
        }

    var codeSklad: String
        get() = preferences.getString(::codeSklad.name, "")?: ""
        set(value) {
            preferences.edit().putString(::codeSklad.name, value).apply()
        }

}