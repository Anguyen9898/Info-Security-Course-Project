package com.anguyen.mymap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.R
import com.anguyen.mymap.adapters.gender.GenderAdapter
import com.anguyen.mymap.adapters.gender.OnGendersItemsClickListener
import com.anguyen.mymap.models.genderDetail
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.gender_bottom_dialog.*


class BottomDialogGenderChooserFragment(
    private val onItemClick: OnGendersItemsClickListener
) : BottomSheetDialogFragment(){

    private lateinit var mAdapter: GenderAdapter

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
    }

    private fun setupRecycleView(){

        mAdapter = GenderAdapter(context!!, genderDetail, onItemClick)

        recycle_hidden_gender_chooser.apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

}
