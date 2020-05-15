package com.anguyen.mymap.adapters.gender

import android.view.View
import android.widget.RadioButton

interface OnGendersItemsClickListener {
    fun onRecycleViewItemClickHandler(checkedRd: RadioButton, selectedString: String?, selectedIndex: Int)
}