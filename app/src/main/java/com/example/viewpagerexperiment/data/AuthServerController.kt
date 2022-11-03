package com.example.viewpagerexperiment.data

import com.example.utils.launchOnMain
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.json.JSONArray
import org.json.JSONObject

class AuthServerController {

    companion object {
        private const val PROFILE_BASE_URL = "http://64.225.108.130"
        private const val AUTH_BASE_ENDPOINT = "auth"
        private const val REGISTRATION_BASE_ENDPOINT = "registration"
        private const val DRIVER_BASE_ENDPOINT = "driver"
        private const val API_ENDPOINT = "api"
        private const val STORAGE_ENDPOINT = "storage"

        const val authPath = "$PROFILE_BASE_URL/$API_ENDPOINT"

        private var auth: AuthServerController? = null

        fun getInstance(): AuthServerController {
            return auth ?: AuthServerController().also { auth = it }
        }
    }

    private val defaultClient = HttpClient(Android) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it]) {
            is JSONArray -> {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }

            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else -> value
        }
    }

    suspend fun saveUserInfo(
        gmail: String,
        firstName: String,
        lastName: String,
        result: (success: Boolean,updateRecords: Int) -> Unit
    ) {
        val user = UserConfig.user
        user.email = gmail
        user.firstName = firstName
        user.lastName = lastName
        UserConfig.saveUser(user)

        val response = JSONObject(defaultClient.submitForm(
            url = "$authPath/registration/updateuser",
            formParameters = Parameters.build {
                append("uin",user.uin)
                append("email",gmail)
                append("firstName",firstName)
                append("lastName",lastName)
            }
        ).body<String>()).toMap()

        val updateRecords = response["update_records"]?.toString()?.toInt() ?: 0
        launchOnMain {
            if (updateRecords == 0) {
                result.invoke(false,updateRecords)
            } else {
                result.invoke(true,updateRecords)
            }
        }
    }

    suspend fun checkCode(ph: String, code: String, result: (isSuccess: Boolean) -> Unit) {
        val responseMap = JSONObject(
            defaultClient.submitForm(
                url = "$authPath/$AUTH_BASE_ENDPOINT/codeverification",
                formParameters = Parameters.build {
                    append("phoneNumber", ph)
                    append("code", code)
                }).body<String>()
        ).toMap()

        val id = responseMap["id"]?.toString()?.toInt() ?: -1
        val uin = responseMap["uin"]?.toString()
        val token = responseMap["token"]?.toString()

        launchOnMain {
            if (uin != null) {
                val user = user()
                user.id = id
                user.uin = uin
                user.token = token ?: ""
                UserConfig.saveUser(user)

                result.invoke(true)
            } else {
                result.invoke(false)
            }
        }
    }

    suspend fun sendVerificationCode(
        ph: String,
        register: Boolean,
        result: (result: ServerRequestResult<String>) -> Unit
    ) {
        val builder = StringBuilder()
        ph.forEach {
            val ignore = it == '+' || it == '-' || it == ' ' || it == '@' || it == '.'
            if (!ignore) {
                builder.append(it)
            }
        }
        val phone = builder.toString()
        val responseMap = JSONObject(defaultClient.submitForm(
            url = "$authPath/$AUTH_BASE_ENDPOINT/sendsms",
            formParameters = Parameters.build {
                append("phoneNumber", phone)
                append("isRegistration", if (register) "1" else "0")
            }
        ).body<String>()).toMap()

        val verificationCode = responseMap["send"].toString()
        val errorDescription = responseMap["error_description"]?.toString()
        launchOnMain {
            if (errorDescription == null) {
                result(ServerRequestResult.Success(verificationCode))
            } else {
                result(ServerRequestResult.Error(errorDescription))
            }
        }
    }
}

sealed class ServerRequestResult<T> {
    class Success<T>(val result: T) : ServerRequestResult<T>()
    class Error(val errorDescription: String) : ServerRequestResult<String>()
}
