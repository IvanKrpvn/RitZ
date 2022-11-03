package com.example.viewpagerexperiment.data

import androidx.core.content.edit
import com.example.utils.sharedConfig

class User {
    var id = 0
    var uin = ""
    var token = ""
    var phone = ""
    var firstName = ""
    var lastName = ""
    var avatar = ""
    var email = ""
}

fun user() = UserConfig.user
fun userLogged() = UserConfig.userLogged()

object UserConfig {
    private var userPrefs = sharedConfig("userConfig")
    var user = User()

    fun userLogged() = user.uin.isNotEmpty()
    var firstTime = true

    init {
        getUser()
    }

    fun setNotFirst() {
        userPrefs.edit {
            putBoolean("firstTime",false)
        }
    }

    fun saveUser(user: User) {
        this.user = user
        userPrefs.edit {
            user.apply {
                putInt("id",id)
                putString("uin",uin)
                putString("phone",phone)
                putString("firstName",firstName)
                putString("lastName",lastName)
                putString("avatar",avatar)
                putString("email",email)
            }
        }
    }

    private fun getUser() {
        userPrefs.apply {
            firstTime = getBoolean("firstTime",true)
            user.apply {
                id = getInt("id",0)
                uin = getString("uin","").toString()
                phone = getString("phone","").toString()
                firstName = getString("firstName","").toString()
                lastName = getString("lastName","").toString()
                avatar = getString("avatar","").toString()
                email = getString("email","").toString()
            }
        }
    }
}