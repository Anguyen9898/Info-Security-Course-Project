package com.anguyen.mymap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.R
import com.anguyen.mymap.adapters.gender.GenderAdapter
import com.anguyen.mymap.adapters.gender.OnGendersItemsClickListener
import com.anguyen.mymap.commons.onClick
import com.anguyen.mymap.models.genderDetail
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.gender_bottom_dialog.*


class BottomDialogGenderChooserFragment(
    private val genderEdt: EditText?
) : BottomSheetDialogFragment(), OnGendersItemsClickListener{

    private lateinit var mAdapter: GenderAdapter
    private var genderText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gender_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()

        btn_apply.onClick {
            genderEdt?.setText(genderText)
            this.dismiss()
        }
    }

    private fun setupRecycleView(){

        mAdapter = GenderAdapter(context!!, genderDetail, this)

        recycle_hidden_gender_chooser.apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun onRecycleViewItemClickHandler(
        checkedRd: RadioButton,
        selectedString: String?,
        selectedIndex: Int
    ) {
        genderText = selectedString!!
    }

}
