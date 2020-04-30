package com.anguyen.mymap.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.anguyen.mymap.R
import com.anguyen.mymap.commons.internetErrorDialog
import com.anguyen.mymap.commons.onClick
import com.anguyen.mymap.commons.showToastByString
import com.anguyen.mymap.presenter.ProfileFragmentPresenter
import com.anguyen.mymap.ui.activities.LoginActivity
import com.anguyen.mymap.ui.activities.StartActivity
import com.anguyen.mymap.ui.views.ProfileFragmentView
import com.anguyen.mymap.ui.views.RegisterView
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(), ProfileFragmentView {

    private lateinit var mPresenter: ProfileFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI(){
        mPresenter = ProfileFragmentPresenter(context!!, this)
        btn_logout.onClick { mPresenter.onLogoutClicked() }
        btn_revoke.onClick { mPresenter.onRevokeClicked() }
    }

    override fun openLoginUI() {
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    override fun onRevokeSuccess() {
        showToastByString(context!!, "Successful")
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }

    override fun fireBaseExceptionError(message: String) = showToastByString(context!!, message)

    override fun internetError() = internetErrorDialog(context!!)
}
