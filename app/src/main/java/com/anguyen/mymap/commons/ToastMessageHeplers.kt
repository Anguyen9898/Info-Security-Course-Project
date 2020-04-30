package com.anguyen.mymap.commons

import android.content.Context
import android.widget.Toast

fun showToastByResourceId(from: Context, messageId: Int) = Toast.makeText(from
    , from.getString(messageId)
    , Toast.LENGTH_LONG).show()

fun showToastByString(from: Context, messageId: String) = Toast.makeText(from
    , messageId, Toast.LENGTH_LONG).show()
