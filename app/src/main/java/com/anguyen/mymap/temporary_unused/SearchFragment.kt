package com.anguyen.mymap.temporary_unused

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anguyen.mymap.R
import com.anguyen.mymap.temporary_unused.search.OnSearchItemsClickListener
import com.anguyen.mymap.temporary_unused.search.SearchingListAdapter
import com.anguyen.mymap.commons.showInternetErrorDialog
import com.anguyen.mymap.commons.showToastByResourceId
import com.anguyen.mymap.commons.showToastByString
import com.anguyen.mymap.models.UserRespondDetail
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), SearchView,
    OnSearchItemsClickListener {

    private lateinit var mPresenter: SearchPresenter
    private lateinit var mAdapter: SearchingListAdapter
    private var mUsers = ArrayList<UserRespondDetail>()

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
        mPresenter =
            SearchPresenter(context!!, this)
        mPresenter.setUserSearchingList()
    }

    override fun onSettingUserSearchingList(user: UserRespondDetail?) {
        mUsers.add(user!!)
        mAdapter = SearchingListAdapter(
            context!!,
            mUsers,
            this,
            mPresenter
        )

        recycle_users_list.apply {
            layoutManager = LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun onSendRequestClickHandler(receiverId: String, status: String?) {//Send Request
        mPresenter.sendRequestHandler(receiverId, status)
    }

    override fun onWaitingClickHandler(status: String?) { //Cancel Request

    }

    override fun onFollowingClickHandler(status: String?) { //Revoke Following

    }

    override fun onSendRequestSuccessfully() {
        showToastByResourceId(context!!, R.string.login_success)
    }

    override fun onSendRequestFailed() {
        showToastByResourceId(context!!, R.string.general_failed_message)
    }

    override fun fireBaseExceptionError(message: String) = showToastByString(context!!, message)

    override fun internetError() = showInternetErrorDialog(context!!)

}
