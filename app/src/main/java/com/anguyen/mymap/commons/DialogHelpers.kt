package com.anguyen.mymap.commons

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.widget.TextView
import com.anguyen.mymap.R

fun changeColor(field: List<TextView>, colorId: Int){
    field.forEach{ it.setTextColor(it.resources.getColor(colorId)) }
}


fun errorDialog(from: Context, titleId: Int, messageId: Int) {

    AlertDialog.Builder(from).apply {

        setTitle(context.getString(titleId))

        setMessage(context.getString(messageId))

        setPositiveButton(from.getString(R.string.general_positive_button)){ dialog, _ -> dialog.dismiss() }

    }.show()!!

}

fun confirmDialog(from: Context, titleId: Int, messageId: Int, confirmFunction: ()-> Unit)  {

    AlertDialog.Builder(from).apply {

        setTitle (context.getString(titleId))

        setMessage(context.getString(messageId))

        setIcon(R.drawable.ic_warning)

        setPositiveButton(from.getString(R.string.general_positive_button)){ _, _ -> confirmFunction() }

        setNegativeButton(from.getString(R.string.general_negative_button)){ dialog, _ -> dialog.cancel() }

    }.show()!!
}

fun showInternetErrorDialog(from: Context) {
    confirmDialog(from,
        R.string.internet_error_title,
        R.string.internet_error_message
    ){
        from.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }
}

fun showLocationErrorDialog(from: Context, confirmFunction: ()-> Unit) {
    confirmDialog(from,
        R.string.location_error_title,
        R.string.location_error_message,
        confirmFunction
    )
}

//fun errorDialog(from: Context){
//    errorDialog(from, R.string.error_title, R.string.error_message)
//}


fun showMultiChoiceDialog(from: Context,
                          choices: Array<String>,
                          checked: BooleanArray,
                          items: Array<String>,
                          multiChoiceClickedHandler: (Array<String>, Boolean) -> Unit,
//                          itemHandler: (Boolean) -> Unit,
                          positiveHandler: (Boolean) -> Unit){

    AlertDialog.Builder(from).apply {

        var choice = false

        setTitle(from.getString(R.string.google_logout_dialog_message))

        setIcon(from.getDrawable(R.drawable.ic_check))

        setMultiChoiceItems(choices, checked) { _, _, isCheck ->
            multiChoiceClickedHandler(choices, isCheck)

            choice = isCheck
        }

        setItems(items) { _, _ -> }

        setNegativeButton(from.getString(R.string.general_negative_button)) {
                dialog, _ -> dialog.cancel()
        }

        setPositiveButton(from.getString(R.string.general_positive_button)) { _, _ ->
                positiveHandler(choice)
        }

        create()

        show()

    }

}
