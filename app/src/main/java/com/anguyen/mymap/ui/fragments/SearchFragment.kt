package com.anguyen.mymap.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.OnRecycleViewItemClickListener
import com.anguyen.mymap.R
import com.anguyen.mymap.adapters.SearchAdapter
import com.anguyen.mymap.commons.showInternetErrorDialog
import com.anguyen.mymap.commons.showToastByString
import com.anguyen.mymap.models.UserSearchListDetail
import com.anguyen.mymap.presenter.SearchPresenter
import com.anguyen.mymap.ui.views.SearchView
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), SearchView, OnRecycleViewItemClickListener {

    private lateinit var mPresenter: SearchPresenter
    private lateinit var mAdapter: SearchAdapter
    private var mUsers = ArrayList<UserSearchListDetail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI(){
        mPresenter = SearchPresenter(context!!, this)
        mPresenter.setUserSearchingList()
    }

    override fun setUserSearchingList(user: UserSearchListDetail?) {
        mUsers.add(user!!)
        mAdapter = SearchAdapter(context!!, mUsers, this)

        recycle_users_list.apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun onRecycleViewItemClickHandler(view: View?) {
        if(view is Button){
            view.background = activity?.getDrawable(R.drawable.button_request)
        }
    }

    override fun fireBaseExceptionError(message: String) = showToastByString(context!!, message)

    override fun internetError() = showInternetErrorDialog(context!!)

}
