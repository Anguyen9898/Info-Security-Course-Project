package com.anguyen.mymap.commons

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder

inline fun View.onClick(crossinline onClickHandler: () -> Unit){
    setOnClickListener { onClickHandler() }
}

inline fun EditText.onTextChanged(crossinline onTextChangeHandler: (String) -> Unit){
    addTextChangedListener(object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChangeHandler(s?.toString() ?: "")
        }
    })
}

inline fun EditText.onFocusChanged(crossinline onFocusChangeHandler: (modeId: Int) -> Unit){
    val resizeMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    val panMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN

    setOnFocusChangeListener{_, hasFocus ->
        if(hasFocus) {
            onFocusChangeHandler(resizeMode)
        } else {
            onFocusChangeHandler(panMode)
        }
    }
}

inline fun BottomNavigationView.onItemSelected(crossinline onItemSelectedHandler
                                               : (menuItem: MenuItem) -> Boolean){
    setOnNavigationItemSelectedListener{ onItemSelectedHandler(it) }
}

inline fun RadioGroup.onCheckedChangeListener(crossinline onItemClickHandler: () -> Unit){
    setOnCheckedChangeListener{ _, _ ->  onItemClickHandler()}
}

fun Fragment.setup(from: FragmentActivity, id: Int, bundle: Bundle) {
    this.arguments = bundle
    from.supportFragmentManager
        .beginTransaction()
        .replace(id, this)
        .commit()
}

inline fun Toolbar.onItemClick(crossinline onItemClickHandler: (menuItem: MenuItem) -> Unit){
    setOnMenuItemClickListener {
        onItemClickHandler (it)
        true
    }
}
