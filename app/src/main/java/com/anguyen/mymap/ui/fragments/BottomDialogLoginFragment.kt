package com.anguyen.mymap.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import com.anguyen.mymap.R
import com.anguyen.mymap.commons.*
import com.anguyen.mymap.models.LoginDetail
import com.anguyen.mymap.presenter.BottomSheetDialogPresenter
import com.anguyen.mymap.presenter.LoginPresenter
import com.anguyen.mymap.ui.activities.RegisterActivity
import com.anguyen.mymap.ui.views.BottomDialogLoginView
import com.anguyen.mymap.ui.views.LoginView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.layout_email_login_bottom_dialog.*


class BottomDialogLoginFragment(private val mLoginView: LoginView) : BottomSheetDialogFragment(), BottomDialogLoginView {

    private lateinit var progressDialog: KProgressHUD

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_email_login_bottom_dialog, container, false)
    }

    /**
     * Initialize Login Presenter
     */
    private fun initPresenter(): BottomSheetDialogPresenter {
        return BottomSheetDialogPresenter(
            context!!,
            this,
            mLoginView,
            LoginDetail(edt_email.text.toString(), edt_password.text.toString()),
            listOf(edt_email, edt_password)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        progressDialog = initProgress(context!!)

        val mPresenter = initPresenter()

        //Catch errors while text's changed
        edt_email.onTextChanged { mPresenter.onEmailChange(it) }
        edt_password.onTextChanged { mPresenter.onPasswordChange(it) }

        btn_login.onClick{
            progressDialog.show()

            mPresenter.onLoginButtonClicked()
        }

        btn_jump_to_register.onClick {

            progressDialog.show()

            val intent = Intent(context!!, RegisterActivity::class.java)

            intent.putExtra("account", edt_email.text.toString())
            intent.putExtra(KEY_USER_TYPE, KEY_EMAIL_USER)

            startActivity(intent)

        }

    }

    override fun onResume() {
        progressDialog.dismiss()
        super.onResume()
    }

    override fun showEmailError() {
        edt_email.error = getString(R.string.invalid_email)
    }

    override fun showPasswordError(count: Int) {

        txt_login_min_val_required.visibility = View.VISIBLE
        txt_login_password_count.text = count.toString()

        if (isPasswordValid(edt_password.text.toString())) {
            changeColor(
                listOf(txt_login_password_count, txt_login_min_val_required),
                R.color.hintDefault
            )
        }else {
            changeColor(
                listOf(txt_login_password_count, txt_login_min_val_required),
                R.color.colorRequired
            )
        }

    }

    override fun onEmptyFieldsError(vararg emptyFields: EditText) {
        progressDialog.dismiss()
        emptyFields.forEach { it.error = getString(R.string.empty_field_required) }
    }

    override fun fireBaseExceptionError(message: String) {
        progressDialog.dismiss()
        showFirebaseError(context!!, message)
    }

    override fun internetError() {
        progressDialog.dismiss()
        showInternetErrorDialog(context!!)
    }

}
