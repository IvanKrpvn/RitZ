package com.example.utils

//String extensions

fun String?.isNotNotNullAndEmpty() = this != null && isNotEmpty()

fun checkIsTextLatin(text: String): Boolean {
    return text.replace(" ", "").trim().matches("^[a-zA-Z0-9.@]+$".toRegex())
}

fun checkEmailValid(text: String): Boolean {
    return text.matches("^(.+)@(\\S+)$".toRegex())
}
