package com.anguyen.mymap.commons

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.util.regex.Pattern

const val EMAIL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
const val MIN_CREDENTIAL_LENGTH = 6
const val PHONE_NUMBER_REGEX = "(09|01[2|6|8|9])+([0-9]{8})\\b"

fun isEmailValid(email : String) = Pattern.matches(EMAIL_REGEX, email)

fun isPasswordValid(password: String) = password.length >= MIN_CREDENTIAL_LENGTH

fun isUsernameValid(username: String) = username.length >= MIN_CREDENTIAL_LENGTH

fun isPhoneNumberValid(phone: String) = Pattern.matches(PHONE_NUMBER_REGEX, phone)

fun arePasswordsSame(password: String, repeatPassword: String) = isPasswordValid(password)
        && isPasswordValid(repeatPassword) && password == repeatPassword

fun isFieldEmpty(strField: String) = strField.isEmpty() or (strField == "")

fun areAnyFieldsEmpty(vararg strFields: String) = strFields.any { isFieldEmpty(it) }